package com.mikosik.stork.test;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.deepEquals;

import java.util.Arrays;

import com.mikosik.stork.problem.Problem;

public class Outcome {
  public static Outcome printed(byte[] stdout) {
    return new Printed(stdout);
  }

  private static class Printed extends Outcome {
    private final byte[] bytes;

    private Printed(byte[] bytes) {
      this.bytes = bytes;
    }

    public boolean equals(Object object) {
      return object instanceof Printed printed
          && equals(printed);
    }

    private boolean equals(Printed printed) {
      return Arrays.equals(bytes, printed.bytes);
    }

    public int hashCode() {
      return Arrays.hashCode(bytes);
    }

    public String toString() {
      return "stdout [%s]".formatted(new String(bytes, UTF_8));
    }
  }

  public static Outcome failed(Problem problem) {
    return new Failed(problem);
  }

  private static class Failed extends Outcome {
    private final Problem problem;

    private Failed(Problem problem) {
      this.problem = problem;
    }

    public boolean equals(Object object) {
      return object instanceof Failed failed
          && equals(failed);
    }

    private boolean equals(Failed failed) {
      return deepEquals(
          problem.description(),
          failed.problem.description());
    }

    public int hashCode() {
      return problem.description().hashCode();
    }

    public String toString() {
      return problem.description();
    }
  }
}
