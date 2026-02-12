package com.mikosik.stork.problem.compile.link;

import static com.mikosik.stork.common.Description.description;
import static com.mikosik.stork.common.ImmutableList.toList;

import java.util.List;

import com.mikosik.stork.common.Description;

public class CannotLinkLibrary extends CannotLink {
  public final List<CannotLink> problems;

  private CannotLinkLibrary(List<CannotLink> problems) {
    this.problems = problems;
  }

  @SuppressWarnings("unchecked")
  public static CannotLinkLibrary cannotLinkLibrary(
      List<? extends CannotLink> problems) {
    return new CannotLinkLibrary((List<CannotLink>) problems);
  }

  public static CannotLinkLibrary cannotLinkLibrary(
      CannotLink... problems) {
    return cannotLinkLibrary(toList(problems));
  }

  public Description describe() {
    return description(
        "cannot link library",
        problems.stream()
            .map(CannotLink::describe)
            .toList());
  }
}
