package com.mikosik.stork.test;

import static com.mikosik.stork.common.Throwables.runtimeException;
import static com.mikosik.stork.common.text.Outline.outline;
import static com.mikosik.stork.problem.Describer.describe;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.deepEquals;

import java.util.Objects;

import org.quackery.report.AssertException;

import com.mikosik.stork.common.text.Outline;
import com.mikosik.stork.problem.compile.CannotCompile;
import com.mikosik.stork.problem.compute.CannotCompute;

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
        .nest(outline("expected " + nameOf(expected))
            .nest(describeOutcome(expected)))
        .nest(outline("found " + nameOf(actual))
            .nest(describeOutcome(actual)));
  }

  private static String nameOf(Outcome outcome) {
    return switch (outcome.object) {
      case byte[] b -> "stdout";
      case CannotCompile cannotCompile -> "problem";
      case CannotCompute cannotCompute -> "problem";
      default -> throw runtimeException("" + outcome.object);
    };
  }

  private static Outline describeOutcome(Outcome outcome) {
    return switch (outcome.object) {
      case byte[] stdout -> outline(format(stdout));
      case CannotCompile cannotCompile -> describe(cannotCompile);
      case CannotCompute cannotCompute -> describe(cannotCompute);
      default -> throw runtimeException("" + outcome.object);
    };
  }

  private static String format(byte[] bytes) {
    return "[" + new String(bytes, UTF_8) + "]";
  }
}
