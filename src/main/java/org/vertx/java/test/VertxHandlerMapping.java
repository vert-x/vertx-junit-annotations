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
package org.vertx.java.test;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;

/**
 * @author swilliams
 *
 * @param <T>
 * @param <M>
 */
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
