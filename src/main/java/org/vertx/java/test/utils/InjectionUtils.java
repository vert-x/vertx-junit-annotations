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
package org.vertx.java.test.utils;

import org.vertx.java.core.impl.VertxInternal;
import org.vertx.java.deploy.impl.VerticleManager;
import org.vertx.java.test.VerticleManagerAware;
import org.vertx.java.test.VertxAware;

/**
 * @author swilliams
 *
 */
public class InjectionUtils {

  public static void inject(VertxInternal vertx, Object target) {
    if (target instanceof VertxAware) {
      VertxAware aware = (VertxAware) target;
      aware.setVertx(vertx);
    }
  }

  public static void inject(VerticleManager verticleManager, Object target) {
    if (target instanceof VerticleManagerAware) {
      VerticleManagerAware aware = (VerticleManagerAware) target;
      aware.setVerticleManager(verticleManager);
    }
  }

}
