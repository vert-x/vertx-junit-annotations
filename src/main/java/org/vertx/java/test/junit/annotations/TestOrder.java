package org.vertx.java.test.junit.annotations;

public @interface TestOrder {

  int value() default 0;

}
