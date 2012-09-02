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
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.runner.Description;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.deploy.impl.VerticleManager;
import org.vertx.java.test.junit.annotations.TestModule;
import org.vertx.java.test.junit.annotations.TestModules;
import org.vertx.java.test.junit.annotations.TestVerticle;
import org.vertx.java.test.junit.annotations.TestVerticles;
import org.vertx.java.test.junit.support.DeploymentUtils;


/**
 * @author swilliams
 *
 */
public class Deployer {

  private final AtomicInteger deploymentCounter = new AtomicInteger(0);

  private final File modDir;

  private final VerticleManager manager;

  private long shutdownTimeout;

  Deployer(VerticleManager manager, File modDir, long shutdownTimeout) {
    this.manager = manager;
    this.modDir = modDir;
    this.shutdownTimeout = shutdownTimeout;
  }

  public void deploy(Description description) {
    deployModules(description.getAnnotation(TestModules.class));
    deployModule(description.getAnnotation(TestModule.class));
    deployVerticles(description.getAnnotation(TestVerticles.class));
    deployVerticle(description.getAnnotation(TestVerticle.class));
  }


  private void deployVerticles(TestVerticles verticles) {

    if (verticles == null) {
      return;
    }

    final CountDownLatch latch = new CountDownLatch(verticles.value().length);
    for (TestVerticle v : verticles.value()) {
      JsonObject config = DeploymentUtils.getJsonConfig(v.jsonConfig());

      URL[] urls = DeploymentUtils.findVerticleURLs(v);
      manager.deployVerticle(v.worker(), v.main(), config, urls, v.instances(), modDir, new CountDownLatchDoneHandler<String>(latch));
      deploymentCounter.incrementAndGet();
    }

    await(latch);
  }

  private void deployVerticle(TestVerticle v) {
    if (v == null) {
      return;
    }

    final CountDownLatch latch = new CountDownLatch(1);
    JsonObject config = DeploymentUtils.getJsonConfig(v.jsonConfig());
    URL[] urls = DeploymentUtils.findVerticleURLs(v);

    manager.deployVerticle(v.worker(), v.main(), config, urls, v.instances(), modDir, new CountDownLatchDoneHandler<String>(latch));
    deploymentCounter.incrementAndGet();

    await(latch);
  }

  private void deployModules(TestModules amodules) {
    if (amodules == null) {
      return;
    }

    final CountDownLatch latch = new CountDownLatch(amodules.value().length);

    for (TestModule m : amodules.value()) {
      JsonObject config = DeploymentUtils.getJsonConfig(m.jsonConfig());
      manager.deployMod(m.name(), config, m.instances(), modDir, new CountDownLatchDoneHandler<String>(latch));
      deploymentCounter.incrementAndGet();
    }

    await(latch);
  }

  private void deployModule(TestModule m) {
    if (m == null) {
      return;
    }

    final CountDownLatch latch = new CountDownLatch(1);

    JsonObject config = DeploymentUtils.getJsonConfig(m.jsonConfig());
    manager.deployMod(m.name(), config, m.instances(), modDir, new CountDownLatchDoneHandler<String>(latch));
    deploymentCounter.incrementAndGet();

    await(latch);
  }

  private void await(final CountDownLatch latch) {
    try {
      latch.await(shutdownTimeout, TimeUnit.SECONDS);

    } catch (InterruptedException e) {
      // TODO log something here
      e.printStackTrace();
    }
  }

  public int getDeploymentCount() {
    return deploymentCounter.get();
  }

}
