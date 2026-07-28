package com.mikosik.stork.test;

import static com.mikosik.stork.common.text.Outline.outline;
import static com.mikosik.stork.test.QuackeryHelper.assertException;
import static java.util.Objects.deepEquals;

import com.mikosik.stork.common.text.Outline;

public class Assertions {
  public static void assertMatch(Outline expected, Outline actual) {
    if (!deepEquals(expected, actual)) {
      throw assertException(outline("test failed because")
          .nest(outline("expected")
              .nest(expected))
          .nest(outline("actual")
              .nest(actual))
          .toString());
    }
  }

  public static void assertMatch(Outcome expected, Outcome actual) {
    if (!deepEquals(expected, actual)) {
      throw assertException(outline("test failed because")
          .nest(outline("expected")
              .nest(expected.describe()))
          .nest(outline("actual")
              .nest(actual.describe()))
          .toString());
    }
  }
}
