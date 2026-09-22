package com.mikosik.stork.compile.err;

import static com.mikosik.stork.compile.err.CompilerException.verifyNoProblems;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class Gatherer {
  private final List<Problem> problems = new ArrayList<>();

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
    verifyNoProblems(problems);
  }
}
