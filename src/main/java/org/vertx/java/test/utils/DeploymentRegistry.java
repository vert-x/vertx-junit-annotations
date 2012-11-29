package org.vertx.java.test.utils;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.vertx.java.test.TestModule;
import org.vertx.java.test.TestVerticle;

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
