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
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.junit.rules.TestRule;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import org.vertx.java.core.Vertx;
import org.vertx.java.core.impl.DefaultVertx;
import org.vertx.java.core.impl.VertxInternal;
import org.vertx.java.deploy.Container;
import org.vertx.java.deploy.Verticle;
import org.vertx.java.deploy.impl.VerticleManager;
import org.vertx.java.test.junit.annotations.VertxConfig;
import org.vertx.java.test.junit.support.VerticleManagerSupport;
import org.vertx.java.test.junit.support.VertxSupport;


/**
 * @author swilliams
 *
 */
public class VertxConfigurableJUnit4Runner extends BlockJUnit4ClassRunner {

  private final Set<String> classCache = new HashSet<String>();

  private VertxInternal vertx;

  private VerticleManager verticleManager;

  private Deployer deployer;

  private Verticle verticle;

  /**
   * @param klass
   * @throws InitializationError
   */
  public VertxConfigurableJUnit4Runner(Class<?> klass) throws InitializationError {
    super(klass);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run(RunNotifier notifier) {

    long timeout;
    VertxConfig vertxConfig = getDescription().getAnnotation(VertxConfig.class);
    if (vertxConfig != null) {
      timeout = vertxConfig.shutdownTimeoutSeconds();
      this.vertx = new DefaultVertx(vertxConfig.port(), vertxConfig.hostname());
    }
    else {
      timeout = 30L;
      this.vertx = new DefaultVertx();
    }

    this.verticleManager = new VerticleManager(vertx);

    String vertxMods = System.getProperty("vertx.mods");
    File currentModDir = new File(vertxMods);
    if (!currentModDir.exists()) {
      currentModDir.mkdirs();
    }

    this.deployer = new Deployer(verticleManager, currentModDir, timeout);
    deployer.deploy(getDescription());

    super.run(notifier);

    undeployAll();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected List<TestRule> getTestRules(Object target) {

    if (!classCache.contains(target.toString())) {
      configureTargetTestClass(target);
      classCache.add(target.toString());
    }

    List<TestRule> rules =  super.getTestRules(target);
    rules.add(new VertxRule(deployer));
    return rules;
  }

  /**
   * @param target
   */
  private void configureTargetTestClass(Object target) {

    if (target instanceof Verticle) {
      // TODO do something clever here...
      System.out.printf("Test class is a Verticle: %s %n", target.getClass().getName());
      this.verticle = (Verticle) target;
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

    // discover setVertx method
    try {
      Method setVertxMethod = target.getClass().getMethod("setVertx", Vertx.class);
      setVertxMethod.invoke(target, vertx);
    } catch (Exception e) {
      // we can ignore this if the method isn't present
    }

    // discover setVerticleManager method
    try {
      Method setVerticleManagerMethod = target.getClass().getMethod("setVerticleManager", VerticleManager.class);
      setVerticleManagerMethod.invoke(target, verticleManager);
    } catch (Exception e) {
      // we can ignore this if the method isn't present
    }

  }

  /**
   * 
   */
  private void undeployAll() {

    CountDownLatch latch = new CountDownLatch(1);
    verticleManager.undeployAll(new CountDownLatchDoneHandler<Void>(latch));

    try {
      latch.await();
    } catch (InterruptedException e) {
      //
    }

    if (verticle != null) {
      try {
        verticle.stop();
      } catch (Exception e) {
        throw new UnexpectedTestRunnerException(e);
      }
    }
  }

}
