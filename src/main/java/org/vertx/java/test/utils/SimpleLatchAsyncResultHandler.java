package org.vertx.java.test.utils;

import java.util.concurrent.CountDownLatch;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;

public class SimpleLatchAsyncResultHandler implements AsyncResultHandler<Void> {

  private final CountDownLatch latch;

  private volatile boolean succeeded;

  public SimpleLatchAsyncResultHandler(CountDownLatch latch) {
    this.latch = latch;
  }

  @Override
  public void handle(AsyncResult<Void> event) {
    this.succeeded = event.succeeded();
    latch.countDown();
  }

  public boolean isSucceeded() {
    return succeeded;
  }

}