/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vertx.java.test.utils;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.vertx.java.core.Handler;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.PlatformManager;
import org.vertx.java.test.TestModule;
import org.vertx.java.test.TestVerticle;

/**
 * @author swilliams
 *
 */
public class DeploymentUtils {

  private static final Logger LOG = Logger.getLogger(DeploymentUtils.class.getName());

  public static Map<Annotation, String> deployVerticles(PlatformManager platformManager, File modDir, Set<TestVerticle> verticles, long timeout) {
    Map<Annotation, String> deployments = new HashMap<>();

    if (verticles.size() > 0) {
      CountDownLatch latch = new CountDownLatch(verticles.size());
      Map<TestVerticle, DeploymentHandler> handlers = new HashMap<>();

      for (TestVerticle v : verticles) {
        DeploymentHandler doneHandler = new DeploymentHandler(latch);
        handlers.put(v, doneHandler);

        JsonObject config = getJsonConfig(v.jsonConfig());
        URL[] classpath = findVerticleURLs(v);

        LOG.log(Level.FINE, "deployVerticle(%s)%n", v);

        // we are having to set null here which is not that clever
        String includes = ("".equals(v.includes())) ? null : v.includes();

        try {
          if (v.worker()) {
            platformManager.deployWorkerVerticle(v.multiThreaded(), v.main(), config, classpath, v.instances(), includes, doneHandler);
          }
          else {
            platformManager.deployVerticle(v.main(), config, classpath, v.instances(), includes, doneHandler);
          }
        } catch (Throwable e) {
          e.printStackTrace();
          LOG.log(Level.WARNING, String.format("Problem deploying (%s) %n", v), e);
        }
        finally {
          //
        }
        System.out.println("latch: " + latch.getCount());
      }

      System.out.println("latch: " + latch.getCount());

      await(latch);
      
      // await(latch, timeout); // Eh?

      Set<Entry<TestVerticle, DeploymentHandler>> entrySet = handlers.entrySet();
      for (Entry<TestVerticle, DeploymentHandler> e : entrySet) {
        String id = e.getValue().getDeploymentID();
        if (id != null) deployments.put(e.getKey(), id);
      }
    }

    return deployments;
  }

  public static Map<Annotation, String> deployModules(PlatformManager platformManager, File modDir, Set<TestModule> modules, long timeout) {
    Map<Annotation, String> deployments = new HashMap<>();

    if (modules.size() > 0) {
      CountDownLatch latch = new CountDownLatch(modules.size());
      Map<TestModule, DeploymentHandler> handlers = new HashMap<>();

      for (TestModule m : modules) {
        DeploymentHandler handler = new DeploymentHandler(latch);
        handlers.put(m, handler);

        JsonObject config = getJsonConfig(m.jsonConfig());

        LOG.log(Level.FINE, "deployModule(%s)%n", m);
        try {
          platformManager.deployModule(m.name(), config, m.instances(), handler);
        } catch (Throwable e) {
          e.printStackTrace();
          LOG.log(Level.WARNING, String.format("Problem deploying (%s) %n", m), e);
          latch.countDown();
        }
      }

      await(latch);
      // FIXME - why does this cause NPE's?
      // latch.await(timeout, TimeUnit.MILLISECONDS);

      Set<Entry<TestModule, DeploymentHandler>> entrySet = handlers.entrySet();
      for (Entry<TestModule, DeploymentHandler> e : entrySet) {
        String id = e.getValue().getDeploymentID();
        if (id != null) deployments.put(e.getKey(), id);
      }
    }

    return deployments;
  }

  public static void undeploy(PlatformManager container, Map<Annotation, String> deployments) {

    final CountDownLatch latch = new CountDownLatch(deployments.size());

    for (Annotation a : deployments.keySet()) {
      final String id = deployments.get(a);

      if (id != null) {
        try {
          container.undeploy(id, new Handler<Void>() {
            @Override
            public void handle(Void event) {
              LOG.log(Level.FINE, String.format("DeploymentUtils undeployed (%s) %n", id));
              latch.countDown();
            }
          });
        } catch (Exception e) {
          LOG.log(Level.WARNING, String.format("Problem undeploying (%s) %n", id), e);
        }
      }
    }

    await(latch, 2000L);  // FIXME this appears to hang
  }

  public static JsonObject getJsonConfig(String jsonConfig) {
    JsonObject config;

    if (jsonConfig.startsWith("file:")) {
      String filename = jsonConfig.replaceFirst("file:", "");

      try {
        // TCCL may be problematic
        URL url = Thread.currentThread().getContextClassLoader().getResource(filename);
        Path json = Paths.get(url.toURI());
        Charset utf8 = Charset.forName("UTF-8");
        byte[] bytes = Files.readAllBytes(json);
        config = new JsonObject(new String(bytes, utf8));

      } catch (IOException e) {
        throw new RuntimeException(e);
      } catch (URISyntaxException e) {
        throw new RuntimeException(e);
      }
    }
    else {
      config = new JsonObject(jsonConfig);
    }

    return config;
  }

  public static URL[] findVerticleURLs(TestVerticle v) {
    Set<URL> urlSet = new HashSet<URL>();

    if (v.urls().length > 0) {
      for (String path : v.urls()) {

        try {

          URL url = new File(path).toURI().toURL();
          urlSet.add(url);

        } catch (Exception e) {
          LOG.log(Level.SEVERE, e.getMessage(), e);
        }
      }
    }

    try {
      String main = v.main();
      if (main.indexOf(':') > -1) {
        main = main.substring(main.indexOf(':') + 1);
      }

      // check for class, prep for locating root URL
      int parts = 0;
      if (!main.endsWith(".xml")) {
        parts = main.split("\\.").length;
        main = main.replaceAll("\\.", "/");
        main = main + ".class";
      }

      // contortions to get parent, may not be entirely accurate...
      // URL url = getClass().getClassLoader().getResource(main);
      URL url = Thread.currentThread().getContextClassLoader().getResource(main);

      if (url != null) {
        Path path = Paths.get(url.toURI());

        int i = parts;
        while (i > 0) {
          path = path.getParent();
          i--;
        }

        url = path.toUri().toURL();
        urlSet.add(url);
      }

    } catch (Exception e) {
      // TODO log something here
      e.printStackTrace();
    }

    URL[] urls = new URL[urlSet.size()];
    return urlSet.toArray(urls);
  }

  public static void await(final CountDownLatch latch) {
    try {
      latch.await();
    } catch (Throwable e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public static void await(final CountDownLatch latch, final long timeout) {
    try {
      latch.await(timeout, TimeUnit.MILLISECONDS);
    } catch (Throwable e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
