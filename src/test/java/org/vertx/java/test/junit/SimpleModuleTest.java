package org.vertx.java.test.junit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.vertx.java.deploy.impl.VerticleManager;
import org.vertx.java.test.TestVerticle;
import org.vertx.java.test.VerticleManagerAware;
import org.vertx.java.test.VertxConfiguration;

@RunWith(VertxJUnit4ClassRunner.class)
@VertxConfiguration
@TestVerticle(main="test_verticle0.js")
public class SimpleModuleTest implements VerticleManagerAware {

  private volatile VerticleManager verticleManager;

  @Override
  public void setVerticleManager(VerticleManager verticleManager) {
    this.verticleManager = verticleManager;
  }


  @BeforeClass
  @TestVerticle(main="test_verticle1.js")
  public static void beforeClass() {
    System.out.println("test.beforeClass");
  }

  @Before
  @TestVerticle(main="test_verticle2.js")
  public void before() {
    System.out.println("test.before");
  }

  @Test
  @TestVerticle(main="test_verticle3.js")
  public void test1() {
    System.out.println("test.test1");
  }

  @Test
  @TestVerticle(main="test_verticle4.js")
  public void test2() {
    System.out.println("test.test2");
  }

  @Test
  @TestVerticle(main="test_verticle5.js")
  public void test3() {
    System.out.println("test.test3");
  }

  @Test
  @TestVerticle(main="test_verticle6.js")
  public void test4() {
    System.out.println("test.test4");
  }

  @After
  public void after() {
    System.out.println("test.after");
  }

  @AfterClass
  public static void afterClass() {
    System.out.println("test.afterClass");
  }

}
