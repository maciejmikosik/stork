package com.mikosik.stork.test.suites.unit;

import static com.mikosik.stork.common.col.ImmutableList.list;
import static com.mikosik.stork.common.col.ImmutableList.single;
import static com.mikosik.stork.common.text.Outline.outline;
import static com.mikosik.stork.compile.err.CompilerException.exception;
import static com.mikosik.stork.compile.err.Describer.describe;
import static com.mikosik.stork.test.Assertions.assertMatch;
import static com.mikosik.stork.test.Outcome.NotCompiled.outcome;
import static com.mikosik.stork.test.Outcome.Printed.outcome;
import static com.mikosik.stork.test.QuackeryHelper.assertException;
import static java.util.Objects.deepEquals;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;

import org.quackery.Suite;
import org.quackery.Test;

import com.mikosik.stork.compile.err.Miscompilation;
import com.mikosik.stork.test.Outcome;

//TODO add tests for stdout outcome and miscomputations
public class TestOutcome {
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
        .add(newCase("single miscompilation", () -> {
          assertMatch(
              describe(exception(miscompilation(1))),
              outcome(exception(miscompilation(1)))
                  .describe());
        }))
        .add(newCase("many miscompilations", () -> {
          assertMatch(
              describe(exception(list(
                  miscompilation(1),
                  miscompilation(2),
                  miscompilation(3)))),
              outcome(exception(list(
                  miscompilation(1),
                  miscompilation(2),
                  miscompilation(3))))
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
        .add(suite("compiler miscompilations")
            .add(newCase("accepts same miscompilation", () -> {
              assertEqual(
                  outcome(exception(miscompilation(1))),
                  outcome(exception(miscompilation(1))));
            }))
            .add(newCase("rejects different miscompilation", () -> {
              assertNotEqual(
                  outcome(exception(miscompilation(1))),
                  outcome(exception(miscompilation(2))));
            })))
        .add(newCase("rejects different count", () -> {
          assertNotEqual(
              outcome(exception(list(miscompilation(1), miscompilation(1)))),
              outcome(exception(single(miscompilation(1)))));
          assertNotEqual(
              outcome(exception(single(miscompilation(1)))),
              outcome(exception(list(miscompilation(1), miscompilation(1)))));
        }))
        .add(newCase("accepts different order", () -> {
          assertEqual(
              outcome(exception(list(miscompilation(1), miscompilation(2), miscompilation(3)))),
              outcome(exception(list(miscompilation(3), miscompilation(2), miscompilation(1)))));
        }))
        .add(newCase("rejects different cardinality", () -> {
          assertNotEqual(
              outcome(exception(list(miscompilation(1), miscompilation(1), miscompilation(2)))),
              outcome(exception(list(miscompilation(1), miscompilation(2), miscompilation(2)))));
          assertNotEqual(
              outcome(exception(list(miscompilation(1), miscompilation(2), miscompilation(2)))),
              outcome(exception(list(miscompilation(1), miscompilation(1), miscompilation(2)))));
        }));
  }

  private static Miscompilation miscompilation(int id) {
    return Miscompilation.miscompilation(Integer.toString(id)).build();
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
