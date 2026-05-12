package com.mikosik.stork.problem.compute;

public class CannotCompute extends RuntimeException {
  public static CannotCompute cannotCompute() {
    return new CannotCompute();
  }
}
