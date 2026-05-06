package com.mikosik.stork.compile;

import static com.mikosik.stork.common.ImmutableList.cast;
import static com.mikosik.stork.common.ImmutableList.single;

import java.util.List;
import java.util.function.Function;

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

  public static <R> Compiled<R> compiled(
      CannotCompile problem) {
    return new Thrown<R>(single(problem));
  }

  public static final class Thrown<R> extends Compiled<R> {
    public final List<CannotCompile> problems;

    private Thrown(List<CannotCompile> problems) {
      this.problems = problems;
    }
  }

  public Compiled<R> then(Function<R, R> function) {
    return switch (this) {
      case Returned<R> returned -> compiled(function.apply(returned.result));
      default -> this;
    };
  }

  public Compiled<R> thenTry(Function<R, Compiled<R>> function) {
    return switch (this) {
      case Returned<R> returned -> function.apply(returned.result);
      default -> this;
    };
  }

  public R getOrThrow() {
    return switch (this) {
      case Returned<R> returned -> returned.result;
      case Thrown<R> thrown -> throw thrown.problems.get(0);
    };
  }
}
