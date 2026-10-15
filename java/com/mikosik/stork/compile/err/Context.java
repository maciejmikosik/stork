package com.mikosik.stork.compile.err;

import static com.mikosik.stork.compile.err.CompilerException.exception;

import java.util.function.Supplier;

import com.mikosik.stork.common.func.Functions.Faa;
import com.mikosik.stork.common.func.Functions.Fab;
import com.mikosik.stork.model.exp.Namespace;
import com.mikosik.stork.model.exp.Variable;

public class Context {
  private final Faa<Problem> modifier;

  private Context(Faa<Problem> modifier) {
    this.modifier = modifier;
  }

  public static Context in(Namespace namespace) {
    return new Context(problem -> problem
        .edit()
        .in(namespace)
        .build());
  }

  public static Context in(Variable variable) {
    return new Context(problem -> problem
        .edit()
        .in(variable)
        .build());
  }

  public <A, B> Fab<A, B> wrap(Fab<A, B> function) {
    return argument -> {
      try {
        return function.apply(argument);
      } catch (CompilerException exception) {
        throw addContext(exception);
      }
    };
  }

  public <E> E run(Supplier<E> supplier) {
    try {
      return supplier.get();
    } catch (CompilerException exception) {
      throw addContext(exception);
    }
  }

  private CompilerException addContext(CompilerException exception) {
    return exception(exception.problems.stream()
        .map(modifier)
        .toList());
  }
}
