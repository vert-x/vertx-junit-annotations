package org.vertx.java.test.junit.support;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.runner.Description;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.deploy.impl.VerticleManager;
import org.vertx.java.test.junit.CountDownLatchDoneHandler;
import org.vertx.java.test.junit.DeploymentHandler;
import org.vertx.java.test.junit.annotations.TestModule;
import org.vertx.java.test.junit.annotations.TestModules;
import org.vertx.java.test.junit.annotations.TestVerticle;
import org.vertx.java.test.junit.annotations.TestVerticles;

public class DeploymentUtils {

  public static Set<TestModule> findTestModules(Description description) {
    Set<TestModule> testModules = new HashSet<>();

    TestModules modules = description.getAnnotation(TestModules.class);
    if (modules != null) {
      for (TestModule m : modules.value()) {
        testModules.add(m);
      }
    }

    TestModule m = description.getAnnotation(TestModule.class);
    if (m != null) {
      testModules.add(m);
    }

    return testModules;
  }

  public static Set<TestVerticle> findTestVerticles(Description description) {
    Set<TestVerticle> testVerticles = new HashSet<>();

    TestVerticles verticles = description.getAnnotation(TestVerticles.class);
    if (verticles != null) {
      for (TestVerticle v : verticles.value()) {
        testVerticles.add(v);
      }
    }

    TestVerticle v = description.getAnnotation(TestVerticle.class);
    if (v != null) {
      testVerticles.add(v);
    }

    return testVerticles;
  }

  public static Set<String> deployVerticles(VerticleManager manager, Set<TestVerticle> testVerticles, File currentModDir, long timeout) {

    Set<String> verticleDeployments = new HashSet<>();

    if (testVerticles.size() > 0) {
      CountDownLatch latch = new CountDownLatch(testVerticles.size());

      DeploymentHandler handler = new DeploymentHandler(latch);
      for (TestVerticle v : testVerticles) {
        JsonObject config = getJsonConfig(v.jsonConfig());
        URL[] urls = findVerticleURLs(v);

        System.out.printf("Deployment.deployVerticle(%s)%n", v);
        manager.deployVerticle(v.worker(), v.main(), config, urls, v.instances(), currentModDir, handler);
      }

      try {
        latch.await(timeout, TimeUnit.SECONDS);
      } catch (InterruptedException e) {

      }
      verticleDeployments.addAll(handler.getDeploymentIDs());
    }
    
    return verticleDeployments;
  }

  public static Set<String> deployModules(VerticleManager manager, Set<TestModule> testModules, File currentModDir, long timeout) {
    Set<String> moduleDeployments = new HashSet<>();

    if (testModules.size() > 0) {

      CountDownLatch latch = new CountDownLatch(testModules.size());
      DeploymentHandler handler = new DeploymentHandler(latch);
      for (TestModule m : testModules) {

        System.out.printf("Deployment.deployModule(%s)%n", m);
        JsonObject config = getJsonConfig(m.jsonConfig());

        manager.deployMod(m.name(), config, m.instances(), currentModDir, handler);
      }

      try {
        latch.await(timeout, TimeUnit.SECONDS);
      } catch (InterruptedException e) {

      }
      moduleDeployments.addAll(handler.getDeploymentIDs());
    }
    return moduleDeployments;
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

  public static void undeployAll(VerticleManager manager, long timeout) {

    CountDownLatch latch = new CountDownLatch(1);
    CountDownLatchDoneHandler<Void> handler = new CountDownLatchDoneHandler<Void>(latch);

    manager.undeployAll(handler);

    try {
      latch.await(timeout, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      //
    }
  }

}
