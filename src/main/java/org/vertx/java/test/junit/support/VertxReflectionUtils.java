package org.vertx.java.test.junit.support;

import org.vertx.java.core.Vertx;
import org.vertx.java.deploy.Container;
import org.vertx.java.deploy.Verticle;
import org.vertx.java.deploy.impl.VerticleManager;
import org.vertx.java.test.junit.UnexpectedTestRunnerException;

public class VertxReflectionUtils {

  /**
   * @param target
   */
  public static Verticle configureTargetTestClass(Vertx vertx, VerticleManager verticleManager, Object target) {

    Verticle verticle = null;
    if (target instanceof Verticle) {
      // TODO do something clever here...
      System.out.printf("Test class is a Verticle: %s %n", target.getClass().getName());
      verticle = (Verticle) target;
      verticle.setVertx(vertx);
      verticle.setContainer(new Container(verticleManager));
      try {
        verticle.start();
      } catch (Exception e) {
        throw new UnexpectedTestRunnerException(e);
      }
    }

    // discover VertxSupport
    if (target instanceof VertxSupport) {
      VertxSupport support = (VertxSupport) target;
      support.setVertx(vertx);
    }

    // discover VerticleManager support
    if (target instanceof VerticleManagerSupport) {
      VerticleManagerSupport support = (VerticleManagerSupport) target;
      support.setManager(verticleManager);
    }

//    // discover setVertx method
//    try {
//      Method setVertxMethod = target.getClass().getMethod("setVertx", Vertx.class);
//      if (setVertxMethod.isAnnotationPresent(Resource.class)) {
//        setVertxMethod.invoke(target, vertx);
//      }
//    } catch (Exception e) {
//      // we can ignore this if the method isn't present
//    }
//
//    // discover setVerticleManager method
//    try {
//      Method setVerticleManagerMethod = target.getClass().getMethod("setVerticleManager", VerticleManager.class);
//      if (setVerticleManagerMethod.isAnnotationPresent(Resource.class)) {
//        setVerticleManagerMethod.invoke(target, verticleManager);
//      }
//    } catch (Exception e) {
//      // we can ignore this if the method isn't present
//    }

    return verticle;
  }

}
