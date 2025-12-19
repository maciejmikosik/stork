package com.mikosik.stork.problem.compile;

import static com.mikosik.stork.common.Sequence.sequenceOf;
import static com.mikosik.stork.common.Sequence.toSequence;
import static com.mikosik.stork.problem.Description.description;

import com.mikosik.stork.common.Sequence;
import com.mikosik.stork.problem.Description;

public class CannotCompileTree extends CannotCompile {
  public final String directory;
  public final Sequence<CannotCompile> problems;

  private CannotCompileTree(
      String directory,
      Sequence<CannotCompile> problems) {
    this.directory = directory;
    this.problems = problems;
  }

  public static CannotCompile cannotCompileTree(
      String directory,
      Sequence<CannotCompile> problems) {
    return problems.size() == 1
        ? problems.getFirst()
        : new CannotCompileTree(directory, problems);
  }

  public static CannotCompile cannotCompileTree(
      String directory,
      CannotCompile... problems) {
    return cannotCompileTree(directory, sequenceOf(problems));
  }

  public Description describe() {
    return description(
        "cannot compile tree [%s]".formatted(directory),
        problems.stream()
            .map(CannotCompile::describe)
            .collect(toSequence()));
  }
}
