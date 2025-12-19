package com.mikosik.stork.problem.compile;

import static com.mikosik.stork.common.Sequence.sequenceOf;
import static com.mikosik.stork.common.Sequence.toSequence;
import static com.mikosik.stork.problem.Description.description;

import com.mikosik.stork.common.Sequence;
import com.mikosik.stork.problem.Description;

public class CannotCompileDirectory extends CannotCompile {
  public final String directory;
  public final Sequence<CannotCompile> problems;

  private CannotCompileDirectory(
      String directory,
      Sequence<CannotCompile> problems) {
    this.directory = directory;
    this.problems = problems;
  }

  public static CannotCompileDirectory cannotCompileDirectory(
      String directory,
      Sequence<CannotCompile> problems) {
    return new CannotCompileDirectory(directory, problems);
  }

  public static CannotCompileDirectory cannotCompileDirectory(
      String directory,
      CannotCompile... problems) {
    return new CannotCompileDirectory(directory, sequenceOf(problems));
  }

  public Description describe() {
    return description(
        "cannot compile directory [%s]".formatted(directory),
        problems.stream()
            .map(CannotCompile::describe)
            .collect(toSequence()));
  }
}
