package com.mikosik.stork.problem.compute;

public class ComputerException extends RuntimeException {
  public final CannotCompute problem;

  private ComputerException(CannotCompute problem) {
    this.problem = problem;
  }

  public static ComputerException exception(CannotCompute problem) {
    return new ComputerException(problem);
  }
}
