package org.vertx.java.test.junit;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.rules.ExternalResource;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.vertx.java.deploy.impl.VerticleManager;
import org.vertx.java.test.junit.annotations.TestModule;
import org.vertx.java.test.junit.annotations.TestVerticle;
import org.vertx.java.test.junit.support.DeploymentUtils;

public class VertxTestRule extends ExternalResource {

  private static final long TIMEOUT = 15L;

  private final Set<TestVerticle> testVerticles = new HashSet<>();

  private final Set<TestModule> testModules = new HashSet<>();

  private final VerticleManager manager;

  private File currentModDir;

  public VertxTestRule(VerticleManager manager) {
    this.manager = manager;

    String vertxMods = System.getProperty("vertx.mods");
    this.currentModDir = new File(vertxMods);
    if (!currentModDir.exists()) {
      currentModDir.mkdirs();
    }
  }

  public VertxTestRule(VerticleManager manager, File currentModDir) {
    this.manager = manager;
    this.currentModDir = currentModDir;
  }

  @Override
  public Statement apply(final Statement base, final Description description) {
    findAnnotations(description);
    return super.apply(base, description);
  }

  private void findAnnotations(final Description description) {
    testModules.addAll(DeploymentUtils.findTestModules(description));
    testVerticles.addAll(DeploymentUtils.findTestVerticles(description));
  }

  @Override
  protected void before() throws Throwable {

    if (!currentModDir.exists()) {
      currentModDir.mkdirs();
    }

    DeploymentUtils.deployVerticles(manager, testVerticles, currentModDir, TIMEOUT);
    DeploymentUtils.deployModules(manager, testModules, currentModDir, TIMEOUT);

    super.before();
  }

  @Override
  protected void after() {

    CountDownLatch latch = new CountDownLatch(1);
    CountDownLatchDoneHandler<Void> handler = new CountDownLatchDoneHandler<Void>(latch);

    manager.undeployAll(handler);

    try {
      latch.await(TIMEOUT, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      //
    }

    super.after();
  }

}
