package com.mikosik.stork.test;

import static com.mikosik.stork.problem.Describe.describe;

import java.util.Arrays;
import java.util.Objects;

import com.mikosik.stork.problem.compile.CannotCompile;
import com.mikosik.stork.problem.compute.CannotCompute;

public class Outcome {
  public final Object object;

  private Outcome(Object object) {
    this.object = object;
  }

  public static Outcome outcome(Object object) {
    return new Outcome(object);
  }

  public static boolean areEqual(Outcome outcomeA, Outcome outcomeB) {
    return areEqual(outcomeA.object, outcomeB.object);
  }

  public static boolean areEqual(Object objectA, Object objectB) {
    if (objectA instanceof byte[] stdoutA
        && objectB instanceof byte[] stdoutB) {
      return Arrays.equals(stdoutA, stdoutB);
    } else if (objectA instanceof CannotCompile cannotCompileA
        && objectB instanceof CannotCompile cannotCompileB) {
      return Objects.equals(describe(cannotCompileA), describe(cannotCompileB));
    } else if (objectA instanceof CannotCompute cannotComputeA
        && objectB instanceof CannotCompute cannotComputeB) {
      return Objects.equals(describe(cannotComputeA), describe(cannotComputeB));
    } else {
      return false;
    }
  }
}
