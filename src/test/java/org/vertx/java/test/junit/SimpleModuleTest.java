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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.vertx.java.deploy.impl.VerticleManager;
import org.vertx.java.test.TestModule;
import org.vertx.java.test.TestModules;
import org.vertx.java.test.VerticleManagerAware;
import org.vertx.java.test.VertxConfiguration;

@RunWith(VertxJUnit4ClassRunner.class)
@VertxConfiguration(modsDir="src/test/mods")
@TestModule(name="test.echo0-v1.0")
public class SimpleModuleTest implements VerticleManagerAware {

  private volatile VerticleManager verticleManager;

  @Override
  public void setVerticleManager(VerticleManager verticleManager) {
    this.verticleManager = verticleManager;
  }

  @Before
  @TestModule(name="test.echo0-v1.0")
  public void before() {
    System.out.printf("test.before %s %n", verticleManager.listInstances());
  }

  @Test
  @TestModules({
    @TestModule(name="test.echo0-v1.0"),
    @TestModule(name="test.echo1-v1.0"),
    @TestModule(name="test.echo2-v1.0")
  })
  public void test1() {
    int instances = verticleManager.listInstances().size();
    System.out.printf("test.test1 %s %n", verticleManager.listInstances());
    Assert.assertEquals(instances, 4);
  }

  @Test
  @TestModule(name="test.echo1-v1.0", instances=3)
  public void test2() {
    int instances = verticleManager.listInstances().size();
    System.out.printf("test.test2 %s %n", verticleManager.listInstances());
    Assert.assertEquals(instances, 2);
  }

  @Test
  @TestModule(name="test.echo2-v1.0", jsonConfig="{\"port\":8091}")
  public void test3() {
    int instances = verticleManager.listInstances().size();
    System.out.printf("test.test3 %s %n", verticleManager.listInstances());
    Assert.assertEquals(instances, 2);
  }

  @Test
  @TestModule(name="test.echo0-v1.0")
  public void test4() {
    int instances = verticleManager.listInstances().size();
    System.out.printf("test.test4 %s %n", verticleManager.listInstances());
    Assert.assertEquals(instances, 2);
  }

  @After
  public void after() {
    System.out.printf("test.after %s %n", verticleManager.listInstances());
  }
}
