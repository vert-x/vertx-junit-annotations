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

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.vertx.java.core.Vertx;
import org.vertx.java.test.TestVerticle;
import org.vertx.java.test.VertxAware;
import org.vertx.java.test.VertxConfiguration;
import org.vertx.java.test.utils.CountDownLatchHandler;

@RunWith(VertxJUnit4ClassRunner.class)
@VertxConfiguration
public class JsonConfigVerticleTest implements VertxAware {

  private volatile Vertx vertx;

  @Override
  public void setVertx(Vertx vertx) {
    this.vertx = vertx;
  }

  @Test
  @TestVerticle(main="test_json0.js", jsonConfig="{ \"foo\": \"bar0\"}")
  public void testAnnotationJsonConfigInline() {
    CountDownLatchHandler<String> handler = new CountDownLatchHandler<>(1);
    vertx.eventBus().send("vertx.test.echo0", "getFoo", handler);
    boolean found = handler.waitFor();
    Assert.assertTrue(found);
    Assert.assertEquals("bar0", handler.poll());
  }

  @Test
  @TestVerticle(main="test_json0.js", jsonConfig="file:test1.json")
  public void testAnnotationJsonConfigWithFile() {
    CountDownLatchHandler<String> handler = new CountDownLatchHandler<>(1);
    vertx.eventBus().send("vertx.test.echo0", "getFoo", handler);
    boolean found = handler.waitFor();
    Assert.assertTrue(found);
    Assert.assertEquals("bar1", handler.poll());
  }

}
