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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.vertx.java.platform.Verticle;
import org.vertx.java.test.VertxConfiguration;

@RunWith(VertxJUnit4ClassRunner.class)
@VertxConfiguration
public class SimpleTestBusModTest extends Verticle {

  private boolean started;

  @Override
  public void start() throws Exception {
    System.out.println("verticle.start");
    this.started = true;
  }

  @Before
  public void before() throws Exception {
    System.out.println("verticle.before");
    Assert.assertTrue(started);
  }

  @Test
  public void test() throws Exception {
    System.out.println("verticle.test");
  }

  @After
  public void after() throws Exception {
    System.out.println("verticle.after");
  }

  @Override
  public void stop() throws Exception {
    System.out.println("verticle.stop");
    this.started = false;
    super.stop();
  }

}
