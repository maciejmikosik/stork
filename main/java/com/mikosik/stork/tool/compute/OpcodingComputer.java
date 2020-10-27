package com.mikosik.stork.tool.compute;

import static com.mikosik.stork.data.model.Integer.integer;
import static com.mikosik.stork.data.model.Variable.variable;
import static com.mikosik.stork.data.model.comp.Computation.computation;
import static com.mikosik.stork.data.model.comp.Function.function;
import static com.mikosik.stork.tool.common.Translate.asStorkBoolean;
import static com.mikosik.stork.tool.compute.Operands.operands;

import java.math.BigInteger;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Variable;
import com.mikosik.stork.data.model.comp.Computation;
import com.mikosik.stork.data.model.comp.Stack;

public class OpcodingComputer implements Computer {
  private static final Variable opArg = variable("opArg");
  private static final Variable opNegate = variable("opNegate");
  private static final Variable opAdd = variable("opAdd");
  private static final Variable opEqual = variable("opEqual");
  private static final Variable opMoreThan = variable("opMoreThan");

  private final Computer computer;

  private OpcodingComputer(Computer computer) {
    this.computer = computer;
  }

  public static Computer opcoding(Computer computer) {
    return new OpcodingComputer(computer);
  }

  public Computation compute(Computation computation) {
    Operands operands = operands(computation.stack);
    if (computation.expression instanceof Variable) {
      Variable variable = (Variable) computation.expression;
      if (variable.name.equals(opArg.name)) {
        return handleOpArg(operands);
      } else if (variable.name.equals(opNegate.name)) {
        return handle(operands, x -> integer(x.negate()));
      } else if (variable.name.equals(opAdd.name)) {
        return handle(operands, (x, y) -> integer(x.add(y)));
      } else if (variable.name.equals(opEqual.name)) {
        return handle(operands, (x, y) -> asStorkBoolean(x.equals(y)));
      } else if (variable.name.equals(opMoreThan.name)) {
        return handle(operands, (x, y) -> asStorkBoolean(y.compareTo(x) > 0));
      }
    }
    return computer.compute(computation);
  }

  private Computation handleOpArg(Operands operands) {
    Expression function = operands.next();
    Expression argument = operands.next();
    Stack stack = operands.stack();
    return computation(
        argument,
        function(
            function,
            stack));
  }

  private static Computation handle(
      Operands operands,
      Function<BigInteger, Expression> logic) {
    BigInteger x = operands.nextJavaBigInteger();
    Stack stack = operands.stack();
    return computation(
        logic.apply(x),
        stack);
  }

  private static Computation handle(
      Operands operands,
      BiFunction<BigInteger, BigInteger, Expression> logic) {
    BigInteger x = operands.nextJavaBigInteger();
    BigInteger y = operands.nextJavaBigInteger();
    Stack stack = operands.stack();
    return computation(
        logic.apply(x, y),
        stack);
  }
}
