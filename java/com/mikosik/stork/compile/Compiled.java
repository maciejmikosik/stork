package com.mikosik.stork.compile;

import static com.mikosik.stork.common.ImmutableList.cast;

import java.util.List;

import com.mikosik.stork.model.Definition;
import com.mikosik.stork.problem.compile.CannotCompile;

public abstract sealed class Compiled {
  public static Compiled compiled(
      List<Definition> definitions,
      Definition... erasure) {
    return new Returned(definitions);
  }

  public static final class Returned extends Compiled {
    public final List<Definition> definitions;

    private Returned(List<Definition> definitions) {
      this.definitions = definitions;
    }
  }

  public static Compiled compiled(
      List<? extends CannotCompile> problems,
      CannotCompile... erasure) {
    return new Thrown(cast(problems));
  }

  public static final class Thrown extends Compiled {
    public final List<CannotCompile> problems;

    private Thrown(List<CannotCompile> problems) {
      this.problems = problems;
    }
  }

  public List<Definition> getOrThrow() {
    return switch (this) {
      case Returned returned -> returned.definitions;
      case Thrown thrown -> throw thrown.problems.get(0);
    };
  }
}
