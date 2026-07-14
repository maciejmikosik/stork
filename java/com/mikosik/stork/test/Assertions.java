package com.mikosik.stork.test;

import static com.mikosik.stork.common.Description.description;
import static com.mikosik.stork.common.Throwables.runtimeException;
import static com.mikosik.stork.problem.Describe.describe;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.Arrays;
import java.util.Objects;

import org.quackery.report.AssertException;

import com.mikosik.stork.common.Description;
import com.mikosik.stork.problem.compile.CannotCompile;
import com.mikosik.stork.problem.compute.CannotCompute;

public class Assertions {
  public static void assertMatch(Outcome expected, Outcome actual) {
    if (!areEqual(expected, actual)) {
      throw new AssertException(failureMessage(expected, actual).toString());
    }
  }

  private static boolean areEqual(Outcome outcomeA, Outcome outcomeB) {
    return areEqual(outcomeA.object, outcomeB.object);
  }

  private static boolean areEqual(Object objectA, Object objectB) {
    if (objectA instanceof byte[] stdoutA
        && objectB instanceof byte[] stdoutB) {
      return Arrays.equals(stdoutA, stdoutB);
    } else if (objectA instanceof CannotCompile cannotCompileA
        && objectB instanceof CannotCompile cannotCompileB) {
      return Objects.equals(
          describe(cannotCompileA),
          describe(cannotCompileB));
    } else if (objectA instanceof CannotCompute cannotComputeA
        && objectB instanceof CannotCompute cannotComputeB) {
      return Objects.equals(
          describe(cannotComputeA),
          describe(cannotComputeB));
    } else {
      return false;
    }
  }

  private static Description failureMessage(Outcome expected, Outcome actual) {
    return description("test failed because")
        .child(description("expected " + nameOf(expected))
            .child(describeOutcome(expected)))
        .child(description("found " + nameOf(actual))
            .child(describeOutcome(actual)));
  }

  private static String nameOf(Outcome outcome) {
    return switch (outcome.object) {
      case byte[] b -> "stdout";
      case CannotCompile cannotCompile -> "problem";
      case CannotCompute cannotCompute -> "problem";
      default -> throw runtimeException("" + outcome.object);
    };
  }

  private static Description describeOutcome(Outcome outcome) {
    return switch (outcome.object) {
      case byte[] stdout -> description(format(stdout));
      case CannotCompile cannotCompile -> describe(cannotCompile);
      case CannotCompute cannotCompute -> describe(cannotCompute);
      default -> throw runtimeException("" + outcome.object);
    };
  }

  private static String format(byte[] bytes) {
    return "[" + new String(bytes, UTF_8) + "]";
  }
}
