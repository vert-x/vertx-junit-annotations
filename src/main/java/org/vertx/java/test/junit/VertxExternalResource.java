package org.vertx.java.test.junit;

import org.junit.rules.ExternalResource;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class VertxExternalResource extends ExternalResource {

  @Override
  public Statement apply(Statement base, Description description) {
    System.out.printf("VertxExternalResource.apply(%s,%s)%n", base, description);
    return super.apply(base, description);
  }

  @Override
  protected void before() throws Throwable {
    System.out.printf("VertxExternalResource.before()%n");
    super.before();
  }

  @Override
  protected void after() {
    System.out.printf("VertxExternalResource.after()%n");
    super.after();
  }

}
