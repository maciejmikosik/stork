package com.mikosik.stork.build;

import static java.util.stream.Collectors.joining;

import java.util.List;

public class BuildException extends RuntimeException {
  private BuildException(String message) {
    super(message);
  }

  public static void report(List<? extends CannotBuild> problems) {
    if (!problems.isEmpty()) {
      String message = problems.stream()
          .map(problem -> problem.toString())
          .collect(joining("\n", "\n", "\n"));
      throw new BuildException(message);
    }
  }
}
