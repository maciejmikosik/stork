package com.mikosik.stork.tool.comp;

import static com.mikosik.stork.common.Chain.add;
import static com.mikosik.stork.common.Chain.empty;
import static com.mikosik.stork.common.Chains.stream;
import static com.mikosik.stork.tool.common.Translate.asJavaBigInteger;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.tool.common.Expressions;

public class Opcode implements Expression {
  public final String name;
  public final Chain<Expression> arguments;
  public final int nMissingArguments;
  public final Function<Chain<Expression>, Expression> logic;

  private Opcode(
      String name,
      Chain<Expression> arguments,
      int nMissingArguments,
      Function<Chain<Expression>, Expression> logic) {
    this.name = name;
    this.arguments = arguments;
    this.nMissingArguments = nMissingArguments;
    this.logic = logic;
  }

  public static Opcode opcode() {
    return new Opcode(null, empty(), 0, null);
  }

  public Opcode name(String name) {
    return new Opcode(
        name,
        arguments,
        nMissingArguments,
        logic);
  }

  public Opcode nArguments(int nArguments) {
    return new Opcode(
        name,
        arguments,
        nArguments,
        logic);
  }

  public Opcode logic(Function<Chain<Expression>, Expression> logic) {
    return new Opcode(
        name,
        arguments,
        nMissingArguments,
        logic);
  }

  public Opcode operatorInt(Function<BigInteger, Expression> logic) {
    return logic(arguments -> {
      Iterator<Expression> iterator = arguments.iterator();
      Expression argument = iterator.next();
      return logic.apply(asJavaBigInteger(argument));
    });
  }

  public Opcode operatorIntInt(BiFunction<BigInteger, BigInteger, Expression> logic) {
    return logic(arguments -> {
      Iterator<Expression> iterator = arguments.iterator();
      Expression argumentB = iterator.next();
      Expression argumentA = iterator.next();
      return logic.apply(
          asJavaBigInteger(argumentA),
          asJavaBigInteger(argumentB));
    });
  }

  public Opcode apply(Expression argument) {
    return new Opcode(
        name,
        add(argument, arguments),
        nMissingArguments - 1,
        logic);
  }

  public Expression invokeIfReady() {
    return nMissingArguments == 0
        ? logic.apply(arguments)
        : this;
  }

  public String toString() {
    return stream(arguments)
        .map(Expressions::print)
        .map(argument -> format("(%s)", argument))
        .collect(joining("", format("$opcode(%s", name), ")"));
  }
}
