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

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.vertx.java.test.junit.VertxConfigurableJUnit4Runner;
import org.vertx.java.test.junit.annotations.TestVerticle;
import org.vertx.java.test.junit.annotations.TestVerticles;
import org.vertx.java.test.junit.support.QueueReplyHandler;
import org.vertx.java.test.junit.support.VertxTestBase;


/**
 * @author swilliams
 *
 */
@RunWith(VertxConfigurableJUnit4Runner.class)
@TestVerticle(main="test_verticle0.js")
public class VerticleAnnotationTest extends VertxTestBase {

  private long timeout = 10L;

  @Before
  public void setup() {
    this.timeout = Long.parseLong(System.getProperty("vertx.test.timeout", "15L"));
  }

  @Test
  public void testVerticle0() {
    String QUESTION = "I say, anyone for cricket?";

    final LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();

    getVertx().eventBus().send("vertx.test.echo0", QUESTION, new QueueReplyHandler<String>(queue, timeout));
    
    try {
      String answer = queue.poll(timeout, TimeUnit.SECONDS);
      System.out.println("answer: " + answer);
      Assert.assertTrue(QUESTION.equals(answer));

    } catch (InterruptedException e) {
      //
    }
  }

  @Test
  @TestVerticle(main="test_verticle1.js")
  public void testVerticle1() {
    String QUESTION = "Oh no. Not penalties again...";

    final LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();

    getVertx().eventBus().send("vertx.test.echo1", QUESTION, new QueueReplyHandler<String>(queue, timeout));
    
    try {
      String answer = queue.poll(timeout, TimeUnit.SECONDS);
      System.out.println("answer: " + answer);
      Assert.assertTrue(QUESTION.equals(answer));

    } catch (InterruptedException e) {
      //
    }
  }

  @Test
  @TestVerticles({
    @TestVerticle(main="test_verticle2.js")
  })
  public void testVerticles2() {
    String QUESTION = "Smashing fun, what!";

    final LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();

    getVertx().eventBus().send("vertx.test.echo2", QUESTION, new QueueReplyHandler<String>(queue, timeout));
    
    try {
      String answer = queue.poll(timeout, TimeUnit.SECONDS);
      System.out.println("answer: " + answer);
      Assert.assertTrue(QUESTION.equals(answer));

    } catch (InterruptedException e) {
      //
    }
  }

}
