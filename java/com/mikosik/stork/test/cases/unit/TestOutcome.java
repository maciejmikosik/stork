package com.mikosik.stork.test.cases.unit;

import static com.mikosik.stork.common.text.Outline.outline;
import static com.mikosik.stork.problem.Describer.describe;
import static com.mikosik.stork.problem.compile.CompilerException.exception;
import static com.mikosik.stork.test.Assertions.assertMatch;
import static com.mikosik.stork.test.Outcome.NotCompiled.outcome;
import static com.mikosik.stork.test.Outcome.Printed.outcome;
import static com.mikosik.stork.test.QuackeryHelper.assertException;
import static com.mikosik.stork.test.cases.unit.TestOutcome.Problem.problem;
import static java.util.Objects.deepEquals;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;

import org.quackery.Suite;
import org.quackery.Test;

import com.mikosik.stork.problem.compile.CannotCompile;
import com.mikosik.stork.test.Outcome;

//TODO add tests for stdout outcome and computer problems
public class TestOutcome {
  public static class Problem extends CannotCompile {
    public final int value;

    private Problem(int value) {
      this.value = value;
    }

    public static Problem problem(int value) {
      return new Problem(value);
    }
  }

  public static Test testOutcome() {
    return suite("outcome")
        .add(testDescribe())
        .add(testEquals());
  }

  private static Test testDescribe() {
    return suite("describes")
        .add(newCase("stdout", () -> {
          assertMatch(
              outline("[abc]"),
              outcome("abc".getBytes())
                  .describe());
        }))
        .add(newCase("single problem", () -> {
          assertMatch(
              describe(problem(1)),
              outcome(exception(problem(1)))
                  .describe());
        }));
  }

  private static Suite testEquals() {
    return suite("equals")
        .add(suite("stdout")
            .add(newCase("accepts same stdout", () -> {
              assertEqual(
                  outcome(new byte[] { 1, 2, 3 }),
                  outcome(new byte[] { 1, 2, 3 }));
            }))
            .add(newCase("rejects different stdout", () -> {
              assertNotEqual(
                  outcome(new byte[] { 1, 2, 3 }),
                  outcome(new byte[] { 1, 0, 3 }));
            })))
        .add(suite("compiler problems")
            .add(newCase("accepts same problem", () -> {
              assertEqual(
                  outcome(exception(problem(1))),
                  outcome(exception(problem(1))));
            }))
            .add(newCase("rejects different problem", () -> {
              assertNotEqual(
                  outcome(exception(problem(1))),
                  outcome(exception(problem(2))));
            })));
  }

  private static void assertEqual(Outcome outcomeA, Outcome outcomeB) {
    if (!deepEquals(outcomeA, outcomeB)) {
      throw assertException("not equal");
    }
    if (!deepEquals(outcomeA.hashCode(), outcomeB.hashCode())) {
      throw assertException("equal but different hashes");
    }
  }

  private static void assertNotEqual(Outcome outcomeA, Outcome outcomeB) {
    if (deepEquals(outcomeA, outcomeB)) {
      throw assertException("equal");
    }
  }
}
