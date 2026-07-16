package com.mikosik.stork.problem.compile;

public class CompilerException extends RuntimeException {
  public final CannotCompile problem;

  private CompilerException(CannotCompile problem) {
    this.problem = problem;
  }

  public static CompilerException exception(CannotCompile problem) {
    return new CompilerException(problem);
  }
}
