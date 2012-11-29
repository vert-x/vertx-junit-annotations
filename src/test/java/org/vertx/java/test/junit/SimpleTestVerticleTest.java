package org.vertx.java.test.junit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.vertx.java.busmods.BusModBase;
import org.vertx.java.test.VertxConfiguration;

@RunWith(VertxJUnit4ClassRunner.class)
@VertxConfiguration
public class SimpleTestVerticleTest extends BusModBase {

  private boolean started;

  @Override
  public void start() {
    System.out.println("busmod.start");
    this.started = true;
  }

  @Before
  public void before() throws Exception {
    System.out.println("busmod.before");
    Assert.assertTrue(started);
  }

  @Test
  public void test() throws Exception {
    System.out.println("busmod.test");
  }

  @After
  public void after() throws Exception {
    System.out.println("busmod.after");
  }

  @Override
  public void stop() throws Exception {
    System.out.println("busmod.stop");
    started = false;
    super.stop();
  }

}
