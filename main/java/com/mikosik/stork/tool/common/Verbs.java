package com.mikosik.stork.tool.common;

import static com.mikosik.stork.tool.common.Translate.asJavaBigInteger;
import static java.lang.String.format;

import java.math.BigInteger;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Verb;

public class Verbs {
  public static Expression opInt(String name, Function<BigInteger, Expression> handler) {
    return new Verb() {
      public String toString() {
        return format("$%s", name);
      }

      public Expression apply(Expression argument) {
        return handler.apply(asJavaBigInteger(argument));
      }
    };
  }

  public static Expression opIntInt(
      String name,
      BiFunction<BigInteger, BigInteger, Expression> handler) {
    return new Verb() {
      public String toString() {
        return format("$%s", name);
      }

      public Expression apply(Expression argumentA) {
        BigInteger numberA = asJavaBigInteger(argumentA);
        return new Verb() {
          public String toString() {
            return format("$%s$%s", name, numberA);
          }

          public Expression apply(Expression argumentB) {
            BigInteger numberB = asJavaBigInteger(argumentB);
            return handler.apply(numberA, numberB);
          }
        };
      }
    };
  }
}
