package com.mikosik.stork.data.model;

import static java.lang.String.format;

import java.util.function.BiFunction;
import java.util.function.Function;

public class Verbs {
  public static Expression verb(
      String name,
      Function<Expression, Expression> handler) {
    return new Verb() {
      public Expression apply(Expression argument) {
        return handler.apply(argument);
      }

      public String toString() {
        return format("<%s>", name);
      }
    };
  }

  public static Expression verb(
      String name,
      BiFunction<Expression, Expression, Expression> handler) {
    return new Verb() {
      public Expression apply(Expression firstArgument) {
        return verb(
            format("%s(%s)", name, firstArgument),
            (secondArgument) -> handler.apply(firstArgument, secondArgument));
      }

      public String toString() {
        return format("<%s>", name);
      }
    };
  }
}
