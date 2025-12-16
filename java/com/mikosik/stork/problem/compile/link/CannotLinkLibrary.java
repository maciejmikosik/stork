package com.mikosik.stork.problem.compile.link;

import static com.mikosik.stork.common.Sequence.sequenceOf;
import static com.mikosik.stork.common.Sequence.toSequence;
import static com.mikosik.stork.problem.Description.description;

import com.mikosik.stork.common.Sequence;
import com.mikosik.stork.problem.Description;

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

  public static CannotLinkLibrary cannotLinkLibrary(
      CannotLink... problems) {
    return cannotLinkLibrary(sequenceOf(problems));
  }

  public Description describe() {
    return description(
        "cannot link library",
        problems.stream()
            .map(CannotLink::describe)
            .collect(toSequence()));
  }
}
