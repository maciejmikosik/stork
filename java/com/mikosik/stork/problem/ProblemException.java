package com.mikosik.stork.problem;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;

import java.util.List;

public class ProblemException extends RuntimeException {
  public final List<Problem> problems;

  private ProblemException(List<Problem> problems) {
    this.problems = problems;
  }

  public static ProblemException exception(List<Problem> problems) {
    return new ProblemException(problems);
  }

  public static ProblemException exception(Problem problem) {
    return exception(asList(problem));
  }

  public static void report(List<Problem> problems) {
    if (!problems.isEmpty()) {
      throw exception(problems);
    }
  }

  public String getMessage() {
    return problems.stream()
        .map(problem -> problem.description())
        .collect(joining("\n", "\n", "\n"));
  }
}
