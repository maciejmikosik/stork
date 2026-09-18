package com.mikosik.stork.problem.compile;

import static com.mikosik.stork.problem.compile.CompilerException.exception;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class Gatherer {
  private final List<CannotCompile> problems = new ArrayList<>();

  private Gatherer() {}

  public static Gatherer gatherer() {
    return new Gatherer();
  }

  public <T> T gather(Supplier<T> supplier) {
    try {
      return supplier.get();
    } catch (CompilerException exception) {
      problems.addAll(exception.problems);
      return null;
    }
  }

  public void verify() {
    if (!problems.isEmpty()) {
      throw exception(problems);
    }
  }
}
