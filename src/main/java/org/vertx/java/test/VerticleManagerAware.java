package org.vertx.java.test;

import org.vertx.java.deploy.impl.VerticleManager;

public interface VerticleManagerAware {

  void setVerticleManager(VerticleManager verticleManager);

}
