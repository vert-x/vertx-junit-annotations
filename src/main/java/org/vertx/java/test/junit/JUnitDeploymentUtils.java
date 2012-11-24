package org.vertx.java.test.junit;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.runner.Description;
import org.vertx.java.deploy.impl.VerticleManager;
import org.vertx.java.test.TestModule;
import org.vertx.java.test.TestModules;
import org.vertx.java.test.TestVerticle;
import org.vertx.java.test.TestVerticles;
import org.vertx.java.test.utils.DeploymentUtils;

public class JUnitDeploymentUtils {

  /**
   * @param manager
   * @param description
   * @return map
   */
  public static Map<Annotation, String> deploy(VerticleManager manager, File modDir, Description description, long timeout) {

    Map<Annotation, String> deployments = new HashMap<>();

    // ------------------------------------------------------------------------------
    // Discover and deploy verticles
    Set<TestVerticle> verticles = new HashSet<>();
    TestVerticle testVerticle = description.getAnnotation(TestVerticle.class);
    if (testVerticle != null) {
      verticles.add(testVerticle);
    }

    TestVerticles testVerticles = description.getAnnotation(TestVerticles.class);
    if (testVerticles != null) {
      for (TestVerticle v : testVerticles.value()) {
        verticles.add(v);
      }
    }

    Map<Annotation, String> verticleDeployments = DeploymentUtils.deployVerticles(manager, modDir, verticles, timeout);
    deployments.putAll(verticleDeployments);

    // ------------------------------------------------------------------------------
    // Discover and deploy modules
    Set<TestModule> modules = new HashSet<>();
    TestModule testModule = description.getAnnotation(TestModule.class);
    if (testModule != null) {
      modules.add(testModule);
    }

    TestModules testModules = description.getAnnotation(TestModules.class);
    if (testModules != null) {
      for (TestModule v : testModules.value()) {
        modules.add(v);
      }
    }

    Map<Annotation, String> modulesDeployments = DeploymentUtils.deployModules(manager, modDir, modules, timeout);
    deployments.putAll(modulesDeployments);

    // ------------------------------------------------------------------------------
    // return result
    return deployments;
  }

}
