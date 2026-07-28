package com.mikosik.stork.problem.compile;

import static com.mikosik.stork.common.ImmutableList.cast;
import static com.mikosik.stork.common.ImmutableList.single;

import java.util.List;

public class CompilerException extends RuntimeException {
  public final List<CannotCompile> problems;

  private CompilerException(List<CannotCompile> problems) {
    this.problems = problems;
  }

  public static CompilerException exception(
      List<? extends CannotCompile> problems) {
    return new CompilerException(cast(problems));
  }

  public static CompilerException exception(CannotCompile problem) {
    return new CompilerException(single(problem));
  }
}
