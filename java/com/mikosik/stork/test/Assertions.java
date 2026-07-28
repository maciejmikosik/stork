package com.mikosik.stork.test;

import static com.mikosik.stork.common.text.Outline.outline;
import static java.util.Objects.deepEquals;

import java.util.Objects;

import org.quackery.report.AssertException;

import com.mikosik.stork.common.text.Outline;

public class Assertions {
  public static void assertMatch(Outline expected, Outline actual) {
    if (!Objects.equals(expected, actual)) {
      throw new AssertException(outline("test failed because")
          .nest(outline("expected")
              .nest(expected))
          .nest(outline("found")
              .nest(actual))
          .toString());
    }
  }

  public static void assertMatch(Outcome expected, Outcome actual) {
    if (!deepEquals(expected, actual)) {
      throw new AssertException(failureMessage(expected, actual).toString());
    }
  }

  private static Outline failureMessage(Outcome expected, Outcome actual) {
    return outline("test failed because")
        .nest(outline("expected")
            .nest(expected.describe()))
        .nest(outline("found")
            .nest(actual.describe()));
  }
}
