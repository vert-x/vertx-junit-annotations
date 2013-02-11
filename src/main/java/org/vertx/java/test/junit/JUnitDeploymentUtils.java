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
package org.vertx.java.test.junit;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.runner.Description;
import org.vertx.java.platform.PlatformManager;
import org.vertx.java.test.TestModule;
import org.vertx.java.test.TestModules;
import org.vertx.java.test.TestVerticle;
import org.vertx.java.test.TestVerticles;
import org.vertx.java.test.utils.DeploymentUtils;

/**
 * @author swilliams
 *
 */
public class JUnitDeploymentUtils {

  /**
   * @param manager
   * @param description
   * @return map
   */
  public static Map<Annotation, String> deploy(PlatformManager platformManager, File modDir, Description description, long timeout) {

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

    Map<Annotation, String> verticleDeployments = DeploymentUtils.deployVerticles(platformManager, modDir, verticles, timeout);
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

    Map<Annotation, String> modulesDeployments = DeploymentUtils.deployModules(platformManager, modDir, modules, timeout);
    deployments.putAll(modulesDeployments);

    // ------------------------------------------------------------------------------
    // return result
    return deployments;
  }

}
