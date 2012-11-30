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

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.vertx.java.test.TestModule;
import org.vertx.java.test.TestVerticle;

/**
 * @author swilliams
 *
 */
public class DeploymentRegistry {

  private final static ThreadLocal<Map<String, String>> deployments = new ThreadLocal<Map<String, String>>() {
    @Override
    protected Map<String, String> initialValue() {
      return new HashMap<String, String>();
    }
  };

  public static final void register(Map<Annotation, String> deployments) {
    Set<Entry<Annotation, String>> entrySet = deployments.entrySet();
    for (Entry<Annotation, String> e : entrySet) {
      String deploymentId = e.getValue();
      Annotation key = e.getKey();
      if (key instanceof TestModule) {
        DeploymentRegistry.register((TestModule) key, deploymentId);
      }
      if (key instanceof TestVerticle) {
        DeploymentRegistry.register((TestVerticle) key, deploymentId);
      }
    }
  }

  public static final void register(TestModule module, String deploymentId) {
    deployments.get().put(module.name(), deploymentId);
  }

  public static final String find(TestModule module) {
    return deployments.get().get(module.name());
  }

  public static final void register(TestVerticle verticle, String deploymentId) {
    deployments.get().put(verticle.main(), deploymentId);
  }

  public static final String find(TestVerticle module) {
    return deployments.get().get(module.main());
  }

  public static final String find(String name) {
    return deployments.get().get(name);
  }

  public static void clear() {
    deployments.remove();
  }

}
