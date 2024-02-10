package com.mikosik.stork.build.link.problem;

import static java.util.stream.Collectors.joining;

import java.util.List;

public class ProblemException extends RuntimeException {
  private ProblemException(String message) {
    super(message);
  }

  public static void report(List<? extends Problem> problems) {
    if (!problems.isEmpty()) {
      String message = problems.stream()
          .map(problem -> problem.toString())
          .collect(joining("\n", "\n", "\n"));
      throw new ProblemException(message);
    }
  }
}
