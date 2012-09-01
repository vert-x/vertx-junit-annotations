package org.vertx.java.test.junit;

public class UnexpectedTestRunnerException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public UnexpectedTestRunnerException(Exception e) {
    super(e);
  }

}
