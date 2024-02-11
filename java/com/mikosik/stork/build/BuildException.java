package com.mikosik.stork.build;

import static java.util.stream.Collectors.joining;

import java.util.List;

public class BuildException extends RuntimeException {
  public final List<? extends CannotBuild> problems;

  private BuildException(List<? extends CannotBuild> problems) {
    this.problems = problems;
  }

  public static BuildException cannotBuild(List<? extends CannotBuild> problems) {
    return new BuildException(problems);
  }

  public static void report(List<? extends CannotBuild> problems) {
    if (!problems.isEmpty()) {
      throw cannotBuild(problems);
    }
  }

  public String getMessage() {
    return problems.stream()
        .map(problem -> problem.toString())
        .collect(joining("\n", "\n", "\n"));
  }
}
