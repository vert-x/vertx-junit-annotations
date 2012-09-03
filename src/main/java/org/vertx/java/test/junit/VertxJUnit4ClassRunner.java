package org.vertx.java.test.junit;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.Map;

import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.vertx.java.core.Handler;
import org.vertx.java.core.impl.VertxInternal;
import org.vertx.java.deploy.impl.VerticleManager;
import org.vertx.java.test.VertxConfiguration;
import org.vertx.java.test.utils.DeploymentUtils;
import org.vertx.java.test.utils.InjectionUtils;

public class VertxJUnit4ClassRunner extends JUnit4ClassRunnerAdapter {

  private VertxInternal vertx;

  private VerticleManager manager;

  private VertxConfiguration configuration;

  private File modDir;

  private Map<Annotation, String> classDeployments;

  private Map<Annotation, String> methodDeployments;

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
  protected void injectResources(Object target) {
    if ((configuration != null && configuration.injectResources()) || configuration == null) {
      InjectionUtils.inject(vertx, target);
      InjectionUtils.inject(manager, target);
    }
  }

  @Override
  protected void injectResources(FrameworkMethod method, Object target) {
    injectResources(target); // TODO probable double setting... test & remove
  }

  @Override
  protected void beforeClass() {
    this.classDeployments = JUnitDeploymentUtils.deploy(manager, modDir, getDescription());
  }

  @Override
  protected void beforeTest(Description description, Object target) {
    this.methodDeployments = JUnitDeploymentUtils.deploy(manager, modDir, description);
  }

  @Override
  protected void afterTest(Description description, Object target) {
    if (methodDeployments.size() > 0) {
      DeploymentUtils.undeploy(manager, methodDeployments);
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
        //
      }});
  }

}
