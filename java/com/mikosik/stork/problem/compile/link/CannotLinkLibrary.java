package com.mikosik.stork.problem.compile.link;

import static java.util.stream.Collectors.joining;

import com.mikosik.stork.common.Sequence;

public class CannotLinkLibrary extends CannotLink {
  public final Sequence<CannotLink> problems;

  private CannotLinkLibrary(Sequence<CannotLink> problems) {
    this.problems = problems;
  }

  @SuppressWarnings("unchecked")
  public static CannotLinkLibrary cannotLinkLibrary(
      Sequence<? extends CannotLink> problems) {
    return new CannotLinkLibrary((Sequence<CannotLink>) problems);
  }

  public String getMessage() {
    return problems.stream()
        .map(problem -> problem.getMessage())
        .collect(joining("\n"));
  }
}
