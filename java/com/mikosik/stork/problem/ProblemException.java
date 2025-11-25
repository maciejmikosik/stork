package com.mikosik.stork.problem;

public class ProblemException extends RuntimeException {
  public final Problem problem;

  private ProblemException(Problem problem) {
    this.problem = problem;
  }

  public static ProblemException exception(Problem problem) {
    return new ProblemException(problem);
  }

  public String getMessage() {
    return problem.description();
  }
}
