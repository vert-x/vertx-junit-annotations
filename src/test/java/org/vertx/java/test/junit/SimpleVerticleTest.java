package org.vertx.java.test.junit;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.vertx.java.deploy.impl.VerticleManager;
import org.vertx.java.test.TestVerticle;
import org.vertx.java.test.TestVerticles;
import org.vertx.java.test.VerticleManagerAware;
import org.vertx.java.test.VertxConfiguration;

@RunWith(VertxJUnit4ClassRunner.class)
@VertxConfiguration
@TestVerticle(main="test_verticle0.js")
public class SimpleVerticleTest implements VerticleManagerAware {

  private volatile VerticleManager verticleManager;

  @Override
  public void setVerticleManager(VerticleManager verticleManager) {
    this.verticleManager = verticleManager;
  }

  @Test
  public void test0() {
    int instances = verticleManager.listInstances().size();
    System.out.println("test.test0 " + instances);
    Assert.assertEquals(instances, 1);
  }

  @Test
  @TestVerticles({
    @TestVerticle(main="test_verticle1.js"),
    @TestVerticle(main="test_verticle2.js"),
    @TestVerticle(main="test_verticle3.js")
  })
  public void test1() {
    int instances = verticleManager.listInstances().size();
    System.out.println("test.test1 " + instances);
    Assert.assertEquals(instances, 4);
  }

  @Test
  @TestVerticle(main="test_verticle4.js")
  public void test2() {
    int instances = verticleManager.listInstances().size();
    System.out.println("test.test2 " + instances);
    Assert.assertEquals(instances, 2);
  }

  @Test
  @TestVerticle(main="test_verticle5.js", instances=3)
  public void test3() {
    int instances = verticleManager.listInstances().size();
    System.out.println("test.test3 " + instances);
    Assert.assertEquals(instances, 2);
  }

}
