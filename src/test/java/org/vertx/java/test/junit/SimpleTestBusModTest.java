package org.vertx.java.test.junit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.vertx.java.deploy.Verticle;
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
