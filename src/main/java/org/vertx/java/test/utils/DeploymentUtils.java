package org.vertx.java.test.utils;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
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
import org.vertx.java.deploy.impl.VerticleManager;
import org.vertx.java.test.TestModule;
import org.vertx.java.test.TestVerticle;

public class DeploymentUtils {

  private static final Logger LOG = Logger.getLogger(DeploymentUtils.class.getName());

  public static Map<Annotation, String> deployVerticles(VerticleManager manager, File modDir, Set<TestVerticle> verticles) {
    Map<Annotation, String> deployments = new HashMap<>();

    if (verticles.size() > 0) {
      CountDownLatch latch = new CountDownLatch(verticles.size());
      Map<TestVerticle, DeploymentHandler> handlers = new HashMap<>();

      for (TestVerticle v : verticles) {
        DeploymentHandler handler = new DeploymentHandler(latch);
        handlers.put(v, handler);

        JsonObject config = getJsonConfig(v.jsonConfig());
        URL[] urls = findVerticleURLs(v);

        LOG.log(Level.FINE, "DeploymentUtils.deployVerticle(%s)%n", v);

        // we are having to set null here which is not that clever
        String includes = ("".equals(v.includes())) ? null : v.includes();
        manager.deployVerticle(v.worker(), v.main(), config, urls, v.instances(), modDir, includes, handler);
      }

      await(latch);

      Set<Entry<TestVerticle, DeploymentHandler>> entrySet = handlers.entrySet();
      for (Entry<TestVerticle, DeploymentHandler> e : entrySet) {
        deployments.put(e.getKey(), e.getValue().getDeploymentID());
      }
    }

    return deployments;
  }

  public static Map<Annotation, String> deployModules(VerticleManager manager, File modDir, Set<TestModule> modules) {
    Map<Annotation, String> deployments = new HashMap<>();

    if (modules.size() > 0) {
      CountDownLatch latch = new CountDownLatch(modules.size());
      Map<TestModule, DeploymentHandler> handlers = new HashMap<>();

      for (TestModule m : modules) {
        DeploymentHandler handler = new DeploymentHandler(latch);
        handlers.put(m, handler);

        JsonObject config = getJsonConfig(m.jsonConfig());

        LOG.log(Level.FINE, "DeploymentUtils.deployModule(%s)%n", m);
        manager.deployMod(m.name(), config, m.instances(), modDir, handler);
      }

      await(latch);

      Set<Entry<TestModule, DeploymentHandler>> entrySet = handlers.entrySet();
      for (Entry<TestModule, DeploymentHandler> e : entrySet) {
        deployments.put(e.getKey(), e.getValue().getDeploymentID());
      }
    }

    return deployments;
  }

  public static void undeploy(VerticleManager manager, Map<Annotation, String> deployments) {

    final CountDownLatch latch = new CountDownLatch(deployments.size());

    for (final String id : deployments.values()) {
      try {
        manager.undeploy(id, new Handler<Void>() {
          @Override
          public void handle(Void event) {
            System.out.printf("DeploymentUtils.undeployed(%s)%n", id);
            latch.countDown();
          }
        });

        await(latch, 2000L);  // FIXME this appears to hang
      }
      catch (IllegalArgumentException e) {
        LOG.log(Level.WARNING, String.format("DeploymentUtils.undeployed(%s)%n", id), e);
      }
    }
  }


  public static JsonObject getJsonConfig(String jsonConfig) {
    JsonObject config;

    if (jsonConfig.startsWith("file:")) {
      String filename = jsonConfig.replaceFirst("file:", "");
      Path json = new File(filename).toPath();

      try {
        Charset utf8 = Charset.forName("UTF-8");
        byte[] bytes = Files.readAllBytes(json);
        config = new JsonObject(new String(bytes, utf8));

      } catch (IOException e) {
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
          // TODO log something here
          e.printStackTrace();
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
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public static void await(final CountDownLatch latch, final long timeout) {
    try {
      latch.await(timeout, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
