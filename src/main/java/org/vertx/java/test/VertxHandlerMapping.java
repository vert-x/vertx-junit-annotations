package org.vertx.java.test;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;

public class VertxHandlerMapping<T, M extends Message<T>> {

  private final String address;

  private final Handler<?> handler;

  public VertxHandlerMapping(String address, Handler<M> handler) {
    this.address = address;
    this.handler = handler;
  }

  public String getAddress() {
    return address;
  }

  public Handler<?> getHandler() {
    return handler;
  }

}
