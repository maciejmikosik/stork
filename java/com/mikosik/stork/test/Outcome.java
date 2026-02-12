package com.mikosik.stork.test;

import static com.mikosik.stork.problem.Describe.describe;

import java.util.Arrays;
import java.util.Objects;

import com.mikosik.stork.problem.Problem;

public sealed abstract class Outcome {
  public static Outcome printed(byte[] stdout) {
    return new Printed(stdout);
  }

  public static final class Printed extends Outcome {
    public final byte[] bytes;

    private Printed(byte[] bytes) {
      this.bytes = bytes;
    }
  }

  public static Outcome failed(Problem problem) {
    return new Failed(problem);
  }

  public static final class Failed extends Outcome {
    public final Problem problem;

    private Failed(Problem problem) {
      this.problem = problem;
    }
  }

  public static boolean areEqual(Outcome outcomeA, Outcome outcomeB) {
    return switch (outcomeA) {
      case Printed printedA -> switch (outcomeB) {
        case Printed printedB -> Arrays.equals(
            printedA.bytes,
            printedB.bytes);
        default -> false;
      };
      case Failed failedA -> switch (outcomeB) {
        case Failed failedB -> Objects.equals(
            describe(failedA.problem),
            describe(failedB.problem));
        default -> false;
      };
    };
  }
}
