package com.mikosik.stork.build;

import static java.util.stream.Collectors.joining;

import java.util.List;

import com.mikosik.stork.model.Problem;

public class BuildException extends RuntimeException {
  public final List<? extends Problem> problems;

  private BuildException(List<? extends Problem> problems) {
    this.problems = problems;
  }

  public static BuildException cannotBuild(List<? extends Problem> problems) {
    return new BuildException(problems);
  }

  public static void report(List<? extends Problem> problems) {
    if (!problems.isEmpty()) {
      throw cannotBuild(problems);
    }
  }

  public String getMessage() {
    return problems.stream()
        .map(problem -> problem.description())
        .collect(joining("\n", "\n", "\n"));
  }
}
