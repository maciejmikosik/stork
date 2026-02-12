package com.mikosik.stork.problem.compute;

import static com.mikosik.stork.common.Description.description;

import com.mikosik.stork.common.Description;
import com.mikosik.stork.problem.Problem;

public class CannotCompute extends Problem {
  public static CannotCompute cannotCompute() {
    return new CannotCompute();
  }

  public Description describe() {
    return description("cannot compute");
  }
}
