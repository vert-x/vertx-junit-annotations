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
package org.vertx.java.test.junit.support;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;

/**
 * @author swilliams
 *
 * @param <T>
 */
public class QueueReplyHandler<T> implements Handler<Message<T>> {

  private final LinkedBlockingQueue<T> queue;

  private final long timeout;

  private final TimeUnit timeUnit;

  public QueueReplyHandler(LinkedBlockingQueue<T> queue) {
    this(queue, 5000L);
  }

  public QueueReplyHandler(LinkedBlockingQueue<T> queue, long timeout) {
    this(queue, timeout, TimeUnit.MILLISECONDS);
  }

  public QueueReplyHandler(LinkedBlockingQueue<T> queue, long timeout, TimeUnit timeUnit) {
    this.queue = queue;
    this.timeout = timeout;
    this.timeUnit = timeUnit;
  }

  @Override
  public void handle(Message<T> event) {

    try {
      if (event != null && event.body != null) {
        queue.offer(event.body, timeout, timeUnit);
      }

    } catch (InterruptedException e) {
      Assert.fail(e.getMessage());
    }
  }
}