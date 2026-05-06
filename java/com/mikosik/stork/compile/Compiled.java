package com.mikosik.stork.compile;

import static com.mikosik.stork.common.ImmutableList.cast;

import java.util.List;

import com.mikosik.stork.problem.compile.CannotCompile;

public abstract sealed class Compiled<R> {
  public static <R> Compiled<R> compiled(R result, Object... erasure) {
    return new Returned<R>(result);
  }

  public static final class Returned<R> extends Compiled<R> {
    public final R result;

    private Returned(R result) {
      this.result = result;
    }
  }

  public static <R> Compiled<R> compiled(
      List<? extends CannotCompile> problems,
      CannotCompile... erasure) {
    return new Thrown<R>(cast(problems));
  }

  public static final class Thrown<R> extends Compiled<R> {
    public final List<CannotCompile> problems;

    private Thrown(List<CannotCompile> problems) {
      this.problems = problems;
    }
  }

  public R getOrThrow() {
    return switch (this) {
      case Returned<R> returned -> returned.result;
      case Thrown<R> thrown -> throw thrown.problems.get(0);
    };
  }
}
