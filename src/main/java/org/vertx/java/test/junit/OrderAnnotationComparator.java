package org.vertx.java.test.junit;

import java.util.Comparator;

import org.junit.runner.Description;
import org.vertx.java.test.junit.annotations.TestOrder;

public class OrderAnnotationComparator implements Comparator<Description> {

  @Override
  public int compare(Description one, Description two) {

    TestOrder order1 = one.getAnnotation(TestOrder.class);
    TestOrder order2 = two.getAnnotation(TestOrder.class);

    int order = 0;
    if (order1 != null && order2 != null) {
      order = order1.value() - order2.value();
    }
    else if (order1 != null && order2 == null) {
      return order1.value();
    }
    else if (order1 == null && order2 != null) {
      return order2.value();
    }

    if (order == 0) {
      order = one.getMethodName().compareTo(two.getMethodName());
    }

    return order;
  }

}
