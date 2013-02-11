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
import java.util.Map;

import org.junit.rules.ExternalResource;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.vertx.java.platform.PlatformManager;
import org.vertx.java.test.utils.DeploymentRegistry;

/**
 * @author swilliams
 *
 */
public class VertxExternalResource extends ExternalResource {

  private File modDir;

  private PlatformManager platformManager;

  private Description description;

  private Map<Annotation, String> methodDeployments;

  public VertxExternalResource(PlatformManager platformManager) {
    this.platformManager = platformManager;
  }

  @Override
  public Statement apply(Statement base, Description description) {
    this.description = description;  // TODO check this is entirely safe

    String vertxMods = System.getProperty("vertx.mods");
    this.modDir = new File(vertxMods);
    if (!modDir.exists()) {
      modDir.mkdirs();
    }
    return super.apply(base, description);
  }

  @Override
  protected void before() throws Throwable {
    long timeout = Long.getLong("vertx.test.timeout", 15000L);
    this.methodDeployments = JUnitDeploymentUtils.deploy(platformManager, modDir, description, timeout);
    DeploymentRegistry.register(methodDeployments);
    super.before();
  }

  @Override
  protected void after() {
    if (methodDeployments.size() > 0) {
      // DeploymentUtils.undeploy(container, methodDeployments);
    }
    super.after();
    DeploymentRegistry.clear();
  }

}
