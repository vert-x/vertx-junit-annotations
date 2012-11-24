package org.vertx.java.test.junit;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.Map;

import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.vertx.java.core.Handler;
import org.vertx.java.core.impl.VertxInternal;
import org.vertx.java.deploy.Container;
import org.vertx.java.deploy.Verticle;
import org.vertx.java.deploy.impl.VerticleManager;
import org.vertx.java.test.VertxConfiguration;
import org.vertx.java.test.utils.DeploymentRegistry;
import org.vertx.java.test.utils.DeploymentUtils;
import org.vertx.java.test.utils.InjectionUtils;

public class VertxJUnit4ClassRunner extends JUnit4ClassRunnerAdapter {

  private VertxInternal vertx;

  private VerticleManager manager;

  private VertxConfiguration configuration;

  private File modDir;

  private Map<Annotation, String> classDeployments;

  private Map<Annotation, String> methodDeployments;

  private Verticle verticle;

  public VertxJUnit4ClassRunner(Class<?> klass) throws InitializationError {
    super(klass);
  }

  @Override
  protected void beforeAll() {

    VertxBuilder builder = new VertxBuilder();

    String vertxMods = System.getProperty("vertx.mods");
    this.configuration = getDescription().getAnnotation(VertxConfiguration.class);
    if (configuration != null) {
      if (!"".equalsIgnoreCase(configuration.hostname())) {
        builder.setAddress(configuration.hostname());
      }
      if (configuration.port() > 0) {
        builder.setPort(configuration.port());
      }
      vertxMods = System.getProperty("vertx.mods", configuration.modsDir());
    }

    this.modDir = new File(vertxMods);
    if (!modDir.exists()) {
      modDir.mkdirs();
    }

    this.vertx = builder.build();
    this.manager = new VerticleManager(vertx);
  }

  @Override
  protected void beforeCreateTest() {
    //
  }

  @Override
  protected void afterCreateTest(Object target) {

    if (target instanceof Verticle) {
      this.verticle = (Verticle) target;
      verticle.setVertx(vertx);
      verticle.setContainer(new Container(manager));
      try {
        System.out.println("Starting test verticle!");
        verticle.start();
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  @Override
  protected void injectResources(Object target) {
    if ((configuration != null && configuration.injectResources()) || configuration == null) {
      InjectionUtils.inject(vertx, target);
      InjectionUtils.inject(manager, target);
    }
  }

  @Override
  protected void injectMethodResources(FrameworkMethod method, Object target) {
//    injectResources(target); // TODO probable double setting... test & remove
  }

  @Override
  protected void beforeClass() {
    long timeout = Long.getLong("vertx.test.timeout", 15000L);
    this.classDeployments = JUnitDeploymentUtils.deploy(manager, modDir, getDescription(), timeout);
  }

  @Override
  protected void beforeTest(Description description, Object target) {
    long timeout = Long.getLong("vertx.test.timeout", 15000L);
    this.methodDeployments = JUnitDeploymentUtils.deploy(manager, modDir, description, timeout);
    DeploymentRegistry.register(methodDeployments);
  }

  @Override
  protected void afterTest(Description description, Object target) {
    if (methodDeployments.size() > 0) {
      DeploymentUtils.undeploy(manager, methodDeployments);
    }

    // avoid leaks
    DeploymentRegistry.clear();
  }

  @Override
  protected void beforeAfterClass() {

    if (this.verticle != null) {
      try {
        verticle.stop();
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  @Override
  protected void afterClass() {
    if (classDeployments.size() > 0) {
      DeploymentUtils.undeploy(manager, classDeployments);
    }
  }

  @Override
  protected void afterAll() {

    manager.undeployAll(new Handler<Void>() {
      @Override
      public void handle(Void event) {
        // TODO log this
      }});
  }

}
