package com.mikosik.stork.problem.compute;

import com.mikosik.stork.problem.Problem;

public class CannotCompute extends Problem {
  public static CannotCompute cannotCompute() {
    return new CannotCompute();
  }

  public String getMessage() {
    return "cannot compute";
  }
}
