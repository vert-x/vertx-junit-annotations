package org.vertx.java.test.utils;

import org.vertx.java.core.impl.VertxInternal;
import org.vertx.java.deploy.impl.VerticleManager;
import org.vertx.java.test.VerticleManagerAware;
import org.vertx.java.test.VertxAware;

public class InjectionUtils {

  public static void inject(VertxInternal vertx, Object target) {
    if (target instanceof VertxAware) {
      VertxAware aware = (VertxAware) target;
      aware.setVertx(vertx);
    }
  }

  public static void inject(VerticleManager verticleManager, Object target) {
    if (target instanceof VerticleManagerAware) {
      VerticleManagerAware aware = (VerticleManagerAware) target;
      aware.setVerticleManager(verticleManager);
    }
  }

}
