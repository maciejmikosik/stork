package com.mikosik.stork.front.core;

import static com.mikosik.stork.common.Chain.chainFrom;
import static com.mikosik.stork.model.Computation.computation;
import static com.mikosik.stork.model.Definition.definition;
import static com.mikosik.stork.model.Integer.integer;
import static com.mikosik.stork.model.Module.module;
import static com.mikosik.stork.model.Variable.variable;
import static com.mikosik.stork.tool.common.Aliens.computeArguments;
import static com.mikosik.stork.tool.common.Aliens.rename;
import static com.mikosik.stork.tool.common.Translate.asStorkBoolean;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.mikosik.stork.model.Alien;
import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Module;

public class JavaModule {
  public static Module javaModule() {
    List<Definition> definitions = new LinkedList<Definition>();
    definitions.add(define("stork.java.math.BigInteger.negate", 1,
        alienize(thiz -> integer(thiz.negate()))));
    definitions.add(define("stork.java.math.BigInteger.add", 2,
        alienize((arg, thiz) -> integer(thiz.add(arg)))));
    definitions.add(define("stork.java.math.BigInteger.equals", 2,
        alienize((arg, thiz) -> asStorkBoolean(thiz.equals(arg)))));
    definitions.add(define("stork.java.math.BigInteger.compareTo", 2,
        alienize((arg, thiz) -> integer(thiz.compareTo(arg)))));
    return module(chainFrom(definitions));
  }

  private static Definition define(String name, int arguments, Alien body) {
    return definition(
        variable(name),
        computeArguments(arguments, rename(name, body)));
  }

  private static Alien alienize(Function<BigInteger, Expression> logic) {
    return stack -> {
      return computation(
          logic.apply(stack.argumentIntegerJava()),
          stack.pop());
    };
  }

  private static Alien alienize(BiFunction<BigInteger, BigInteger, Expression> logic) {
    return stack -> {
      return computation(
          logic.apply(
              stack.argumentIntegerJava(),
              stack.pop().argumentIntegerJava()),
          stack.pop().pop());
    };
  }
}
