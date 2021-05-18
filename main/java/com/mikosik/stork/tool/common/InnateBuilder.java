package com.mikosik.stork.tool.common;

import static com.mikosik.stork.model.Computation.computation;
import static com.mikosik.stork.model.Definition.definition;
import static com.mikosik.stork.model.Integer.integer;
import static com.mikosik.stork.model.Variable.variable;
import static com.mikosik.stork.tool.common.Constants.FALSE;
import static com.mikosik.stork.tool.common.Constants.TRUE;
import static com.mikosik.stork.tool.common.Eager.eager;
import static java.util.Objects.requireNonNull;

import java.math.BigInteger;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.mikosik.stork.model.Computation;
import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Innate;
import com.mikosik.stork.model.Stack;

public class InnateBuilder {
  private String name;
  private int arguments = 0;
  private Innate logic;

  private InnateBuilder() {}

  public static InnateBuilder innate() {
    return new InnateBuilder();
  }

  public InnateBuilder name(String name) {
    this.name = name;
    return this;
  }

  public InnateBuilder arguments(int arguments) {
    this.arguments = arguments;
    return this;
  }

  public InnateBuilder logic(Innate logic) {
    this.logic = logic;
    return this;
  }

  public InnateBuilder logicIntInt(Function<BigInteger, BigInteger> logic) {
    logic(stack -> computation(
        integer(logic.apply(stack.argumentIntegerJava())),
        stack.pop()));
    return arguments(1);
  }

  public InnateBuilder logicIntIntInt(BiFunction<BigInteger, BigInteger, BigInteger> logic) {
    logic(stack -> {
      BigInteger argumentA = stack.argumentIntegerJava();
      stack = stack.pop();
      BigInteger argumentB = stack.argumentIntegerJava();
      stack = stack.pop();
      return computation(
          integer(logic.apply(argumentA, argumentB)),
          stack);
    });
    return arguments(2);
  }

  public InnateBuilder logicIntIntBool(BiFunction<BigInteger, BigInteger, Boolean> logic) {
    logic(stack -> {
      BigInteger argumentA = stack.argumentIntegerJava();
      stack = stack.pop();
      BigInteger argumentB = stack.argumentIntegerJava();
      stack = stack.pop();
      return computation(
          logic.apply(argumentA, argumentB) ? TRUE : FALSE,
          stack);
    });
    return arguments(2);
  }

  public Expression build() {
    requireNonNull(name);
    requireNonNull(logic);
    return eager(arguments, innate(name, logic));
  }

  private static Innate innate(String name, Innate logic) {
    return new Innate() {
      public Computation compute(Stack stack) {
        return logic.compute(stack);
      }

      public String toString() {
        return name;
      }
    };
  }

  public Definition defineAs(String globalName) {
    return definition(variable(globalName), build());
  }
}
