package org.vertx.java.test.junit;

import java.util.List;

import org.junit.rules.ExternalResource;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

abstract class JUnit4ClassRunnerAdapter extends BlockJUnit4ClassRunner {

  protected JUnit4ClassRunnerAdapter(Class<?> klass) throws InitializationError {
    super(klass);
  }

  @Override
  protected Statement methodInvoker(FrameworkMethod method, Object target) {
    injectResources(method, target);
    return super.methodInvoker(method, target);
  }

  @Override
  protected List<TestRule> classRules() {
    List<TestRule> rules = super.classRules();
    rules.add(new ExternalResource() {
      @Override
      protected void before() throws Throwable {
        beforeClass();
        super.before();
      }

      @Override
      protected void after() {
        afterClass();
        super.after();
      }
    });
    return rules;
  }

  @Override
  protected List<TestRule> getTestRules(final Object target) {

    injectResources(target);

    List<TestRule> rules = super.getTestRules(target);
    rules.add(new ExternalResource() {

      private Description description;

      @Override
      public Statement apply(Statement base, Description description) {
        this.description = description;
        return super.apply(base, description);
      }

      @Override
      protected void before() throws Throwable {
        beforeTest(description, target);
      }

      @Override
      protected void after() {
        afterTest(description, target);
      }
    });
    return rules;
  }

  @Override
  public void run(RunNotifier notifier) {
    beforeAll();
    super.run(notifier);
    afterAll();
  }

  protected abstract void beforeAll();

  protected abstract void injectResources(Object target);

  protected abstract void injectResources(FrameworkMethod method, Object target);

  protected abstract void beforeClass();

  protected abstract void beforeTest(Description description, Object target);

  protected abstract void afterTest(Description description, Object target);

  protected abstract void afterClass();

  protected abstract void afterAll();

}
