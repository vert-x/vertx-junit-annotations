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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.junit.rules.TestRule;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import org.vertx.java.core.impl.DefaultVertx;
import org.vertx.java.core.impl.VertxInternal;
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

  public VertxConfigurableJUnit4Runner(Class<?> klass) throws InitializationError {
    super(klass);
  }

  @Override
  public void run(RunNotifier notifier) {

    VertxConfig vertxConfig = getDescription().getAnnotation(VertxConfig.class);
    if (vertxConfig != null) {
      this.vertx = new DefaultVertx(vertxConfig.port(), vertxConfig.hostname());
    }
    else {
      this.vertx = new DefaultVertx();
    }

    this.verticleManager = new VerticleManager(vertx);

    String vertxMods = System.getProperty("vertx.mods");
    File currentModDir = new File(vertxMods);
    if (!currentModDir.exists()) {
      throw new RuntimeException("vert.mods dir doesn't exist! " + vertxMods);
    }

    this.deployer = new Deployer(verticleManager, currentModDir);
    deployer.deploy(getDescription());

    super.run(notifier);

    undeployAll();
  }


  @Override
  protected List<TestRule> getTestRules(Object target) {
    
    if (!classCache.contains(target.toString())) {
      configureTarget(target);
      classCache.add(target.toString());
    }

    List<TestRule> rules =  super.getTestRules(target);
    rules.add(new VertxRule(deployer));
    return rules;
  }

  private void configureTarget(Object target) {

    if (target instanceof VertxSupport) {
      VertxSupport support = (VertxSupport) target;
      support.setVertx(vertx);
    }

    if (target instanceof VerticleManagerSupport) {
      VerticleManagerSupport support = (VerticleManagerSupport) target;
      support.setManager(verticleManager);
    }

  }

  private void undeployAll() {
    CountDownLatch latch = new CountDownLatch(1);
    verticleManager.undeployAll(new CountDownLatchDoneHandler<Void>(latch));

    try {
      latch.await();
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}
