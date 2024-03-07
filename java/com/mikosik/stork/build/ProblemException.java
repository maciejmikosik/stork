package com.mikosik.stork.build;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;

import java.util.List;

import com.mikosik.stork.model.Problem;

public class ProblemException extends RuntimeException {
  public final List<? extends Problem> problems;

  private ProblemException(List<? extends Problem> problems) {
    this.problems = problems;
  }

  public static ProblemException exception(List<? extends Problem> problems) {
    return new ProblemException(problems);
  }

  public static ProblemException exception(Problem problem) {
    return exception(asList(problem));
  }

  public static void report(List<? extends Problem> problems) {
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
