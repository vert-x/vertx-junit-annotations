package org.vertx.java.test.junit;

import org.vertx.java.core.impl.DefaultVertx;

public class VertxBuilder {

  private String address;

  private int port;

  public VertxBuilder setAddress(String address) {
    this.address = address;
    return this;
  }

  public VertxBuilder setPort(int port) {
    this.port = port;
    return this;
  }

  public DefaultVertx build() {
    try {
      DefaultVertx vertx;
      if (port > -1 && address != null) {
        vertx = new DefaultVertx(port, address);
      }
      else if (address != null) {
        vertx = new DefaultVertx(address);
      }
      else {
        vertx = new DefaultVertx();
      }
      return vertx;
    }
    finally {
      this.address = null;
      this.port = -1;
    }
  }

}
