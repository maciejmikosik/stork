package com.mikosik.stork.test;

import com.mikosik.stork.problem.compile.CannotCompile;
import com.mikosik.stork.problem.compute.CannotCompute;

public class Outcome {
  public final Object object;

  private Outcome(Object object) {
    this.object = object;
  }

  public static Outcome outcome(CannotCompile cannotCompile) {
    return new Outcome(cannotCompile);
  }

  public static Outcome outcome(CannotCompute cannotCompute) {
    return new Outcome(cannotCompute);
  }

  public static Outcome outcome(byte[] stdout) {
    return new Outcome(stdout);
  }
}
