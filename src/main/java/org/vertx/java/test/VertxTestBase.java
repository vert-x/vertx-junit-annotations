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
package org.vertx.java.test;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.deploy.impl.VerticleManager;
import org.vertx.java.test.utils.QueueReplyHandler;
import org.vertx.java.test.utils.SimpleLatchAsyncResultHandler;


/**
 * @author swilliams
 *
 */
public abstract class VertxTestBase implements VertxAware, VerticleManagerAware {

  private Vertx vertx;

  private VerticleManager manager;

  private static long AWAIT_TIMEOUT = 5000L;

  protected VertxTestBase() {
    super();
  }

  @Override
  public void setVertx(Vertx vertx) {
    this.vertx = vertx;
  }

  @Override
  public void setVerticleManager(VerticleManager manager) {
    this.manager = manager;
  }

  protected Vertx getVertx() {
    return vertx;
  }

  protected EventBus getEventBus() {
    return vertx.eventBus();
  }

  protected VerticleManager getManager() {
    return manager;
  }

  protected static final void setAwaitTimeout(long awaitTimeout) {
    AWAIT_TIMEOUT = awaitTimeout;
  }

  protected static final void lightSleep(long timeout) {
    try {
      Thread.sleep(timeout);
    } catch (InterruptedException e) {
      //
    }
  }

  protected final void testMessageEcho(String address, String message) throws Exception {

    final TimeUnit timeUnit = TimeUnit.MILLISECONDS;
    final LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();

    getVertx().eventBus().send(address, message, new QueueReplyHandler<String>(queue, AWAIT_TIMEOUT, timeUnit));

    try {
      String answer = queue.poll(AWAIT_TIMEOUT, timeUnit);
      System.out.printf("For %s Q:%s A:%s %n", address, message, message.equals(answer));
      Assert.assertTrue(message.equals(answer));

    } catch (InterruptedException e) {
      //
    }
  }

  protected final <T, M extends Message<T>> String registerHandler(String address, Handler<M> handler) {
    final CountDownLatch latch = new CountDownLatch(1);
    String handlerId = vertx.eventBus().registerHandler(address, handler, new SimpleLatchAsyncResultHandler(latch));
    await(latch);
    return handlerId;
  }

  protected final <T, M extends Message<T>> String registerLocalHandler(String address, Handler<M> handler) {
    final CountDownLatch latch = new CountDownLatch(1);
    String handlerId = vertx.eventBus().registerLocalHandler(address, handler);
    await(latch);
    return handlerId;
  }

  protected final void unregisterHandlers(String... handlers) {
    unregisterHandlers(Arrays.asList(handlers));
  }

  protected final void unregisterHandlers(Iterable<String> iterable) {
    for (String handler : iterable) {
      vertx.eventBus().unregisterHandler(handler);
    }
  }

  protected static final void await(CountDownLatch latch) {
    await(latch, AWAIT_TIMEOUT);
  }

  protected static final void await(CountDownLatch latch, long timeout) {
    await(latch, timeout, TimeUnit.MILLISECONDS);
  }

  protected static final void await(CountDownLatch latch, long timeout, TimeUnit timeUnit) {
    try {
      latch.await(timeout, timeUnit);
    }
    catch (InterruptedException e) {
      //
    }
  }

}
