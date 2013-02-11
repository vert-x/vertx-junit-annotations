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

import org.vertx.java.core.Vertx;
import org.vertx.java.platform.PlatformManager;
import org.vertx.java.test.PlatformManagerAware;
import org.vertx.java.test.VertxAware;

/**
 * @author swilliams
 *
 */
public class InjectionUtils {

  public static void inject(Vertx vertx, Object target) {
    if (target instanceof VertxAware) {
      VertxAware aware = (VertxAware) target;
      aware.setVertx(vertx);
    }
  }

  public static void inject(PlatformManager platformManager, Object target) {
    if (target instanceof PlatformManagerAware) {
      PlatformManagerAware aware = (PlatformManagerAware) target;
      aware.setPlatformManager(platformManager);
    }
  }

}
