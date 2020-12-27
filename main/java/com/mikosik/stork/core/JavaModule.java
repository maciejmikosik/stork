package com.mikosik.stork.core;

import static com.mikosik.stork.common.Chain.chainFrom;
import static com.mikosik.stork.data.model.Definition.definition;
import static com.mikosik.stork.data.model.Integer.integer;
import static com.mikosik.stork.data.model.Module.module;
import static com.mikosik.stork.data.model.Variable.variable;
import static com.mikosik.stork.data.model.comp.Computation.computation;
import static com.mikosik.stork.tool.common.Aliens.computeArguments;
import static com.mikosik.stork.tool.common.Aliens.rename;
import static com.mikosik.stork.tool.common.Translate.asStorkBoolean;
import static com.mikosik.stork.tool.compute.Operands.operands;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.mikosik.stork.data.model.Alien;
import com.mikosik.stork.data.model.Definition;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Module;
import com.mikosik.stork.tool.compute.Operands;

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
        alienize((arg, thiz) -> integer(BigInteger.valueOf(thiz.compareTo(arg))))));
    return module(chainFrom(definitions));
  }

  private static Definition define(String name, int arguments, Alien body) {
    return definition(
        variable(name),
        computeArguments(arguments, rename(name, body)));
  }

  private static Alien alienize(Function<BigInteger, Expression> logic) {
    return stack -> {
      Operands operands = operands(stack);
      return computation(
          logic.apply(operands.nextJavaBigInteger()),
          operands.stack());
    };
  }

  private static Alien alienize(BiFunction<BigInteger, BigInteger, Expression> logic) {
    return stack -> {
      Operands operands = operands(stack);
      return computation(
          logic.apply(
              operands.nextJavaBigInteger(),
              operands.nextJavaBigInteger()),
          operands.stack());
    };
  }
}
