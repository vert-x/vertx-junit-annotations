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
package org.vertx.testing.support;

import org.vertx.java.core.Vertx;
import org.vertx.java.deploy.impl.VerticleManager;


/**
 * @author swilliams
 *
 */
public class VertxTestBase implements VertxSupport, VerticleManagerSupport {

  private Vertx vertx;

  private VerticleManager manager;

  public void setVertx(Vertx vertx) {
    this.vertx = vertx;
  }

  public Vertx getVertx() {
    return vertx;
  }

  public VerticleManager getManager() {
    return manager;
  }

  public void setManager(VerticleManager manager) {
    this.manager = manager;
  }

}
