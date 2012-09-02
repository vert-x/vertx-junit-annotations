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
package org.vertx.java.test.junit;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.manipulation.Sorter;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

/**
 * @author swilliams
 *
 */
public class VertxConfigurationJUnit4Runner extends BlockJUnit4ClassRunner {

  private final Set<String> classCache = new HashSet<String>();

  private final VertxTestRunnerDelegate delegate;

  /**
   * @param klass
   * @throws InitializationError
   */
  public VertxConfigurationJUnit4Runner(Class<?> klass) throws InitializationError {
    super(klass);
    this.delegate = new VertxTestRunnerDelegate();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run(RunNotifier notifier) {

    Comparator<Description> comparator = new OrderAnnotationComparator();
    super.sort(new Sorter(comparator));

    delegate.runBeforeTest(getDescription());
    super.run(notifier);
    delegate.runAfterTest();
  }

  @Override
  protected List<TestRule> getTestRules(Object target) {

    List<TestRule> rules = super.getTestRules(target);

    if (!classCache.contains(target.toString())) {
      delegate.runBeforeClass(target);
      classCache.add(target.toString());
    }
    else {
      TestRule rule = delegate.runBeforeEachTest(target);
      rules.add(rule);
    }

    return rules;
  }
}
