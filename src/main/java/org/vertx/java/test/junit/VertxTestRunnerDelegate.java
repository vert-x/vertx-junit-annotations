package org.vertx.java.test.junit;

import java.io.File;
import java.util.Set;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.vertx.java.core.impl.DefaultVertx;
import org.vertx.java.deploy.Verticle;
import org.vertx.java.deploy.impl.VerticleManager;
import org.vertx.java.test.junit.annotations.TestModule;
import org.vertx.java.test.junit.annotations.TestVerticle;
import org.vertx.java.test.junit.annotations.VertxConfiguration;
import org.vertx.java.test.junit.support.DeploymentUtils;
import org.vertx.java.test.junit.support.VertxReflectionUtils;

public class VertxTestRunnerDelegate {

  private long timeout = 15L;

  private File currentModDir;

  private DefaultVertx vertx;

  private VerticleManager manager;

  private Verticle verticle;

  private VertxTestRule testRule;

  public void runBeforeTest(Description description) {

    VertxConfiguration config = description.getAnnotation(VertxConfiguration.class);
    VertxBuilder builder = new VertxBuilder();
    String vertxMods;

    if (config != null) {
      this.timeout = config.shutdownTimeoutSeconds();
      if (config.port() > 0) {
        builder.setPort(config.port());
      }
      if (!"".equals(config.hostname())) {
        builder.setAddress(config.hostname());
      }
      vertxMods = System.getProperty("vertx.mods", config.modsDir());
    }
    else {
      this.timeout = 30L;
      this.vertx = new DefaultVertx();
      vertxMods = System.getProperty("vertx.mods");
    }

    this.vertx = builder.build();

    this.currentModDir = new File(vertxMods);
    if (!currentModDir.exists()) {
      currentModDir.mkdirs();
    }

    this.manager = new VerticleManager(vertx);
    this.testRule = new VertxTestRule(manager, currentModDir);

    Set<TestModule> testModules = DeploymentUtils.findTestModules(description);
    DeploymentUtils.deployModules(manager, testModules, currentModDir, timeout);

    Set<TestVerticle> testVerticles = DeploymentUtils.findTestVerticles(description);
    DeploymentUtils.deployVerticles(manager, testVerticles, currentModDir, timeout);
  }

  public void runBeforeClass(Object target) {
    this.verticle = VertxReflectionUtils.configureTargetTestClass(vertx, manager, target);
  }

  public TestRule runBeforeEachTest(Object target) {
    return testRule;
  }

  public void runAfterTest() {
    System.out.println("Delegate.runAfterTest");

    DeploymentUtils.undeployAll(manager, timeout);

    if (verticle != null) {
      try {
        verticle.stop();
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

}
