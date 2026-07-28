package com.mikosik.stork.test;

import static com.mikosik.stork.common.text.Outline.outline;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.deepEquals;

import java.util.Arrays;

import com.mikosik.stork.common.text.Outline;
import com.mikosik.stork.problem.Describer;
import com.mikosik.stork.problem.compile.CompilerException;
import com.mikosik.stork.problem.compute.ComputerException;

public sealed interface Outcome {
  Outline describe();

  record NotCompiled(CompilerException exception) implements Outcome {
    public static Outcome outcome(CompilerException exception) {
      return new NotCompiled(exception);
    }

    public Outline describe() {
      return Describer.describe(exception.problem);
    }

    public boolean equals(Object object) {
      return object instanceof NotCompiled that
          && deepEquals(
              this.exception.problem,
              that.exception.problem);
    }

    public int hashCode() {
      return exception.problem.hashCode();
    }
  }

  record NotComputed(ComputerException exception) implements Outcome {
    public static Outcome outcome(ComputerException exception) {
      return new NotComputed(exception);
    }

    public Outline describe() {
      return Describer.describe(exception.problem);
    }

    public boolean equals(Object object) {
      return object instanceof NotComputed that
          && deepEquals(
              this.exception.problem,
              that.exception.problem);
    }

    public int hashCode() {
      return exception.problem.hashCode();
    }
  }

  record Printed(byte[] stdout) implements Outcome {
    public static Outcome outcome(byte[] stdout) {
      return new Printed(stdout);
    }

    public Outline describe() {
      return outline(format("[%s]", new String(stdout, UTF_8)));
    }

    public boolean equals(Object object) {
      return object instanceof Printed that
          && Arrays.equals(
              this.stdout,
              that.stdout);
    }

    public int hashCode() {
      return Arrays.hashCode(stdout);
    }
  }
}
