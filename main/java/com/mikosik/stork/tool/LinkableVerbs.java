package com.mikosik.stork.tool;

import static com.mikosik.stork.data.model.Noun.noun;
import static com.mikosik.stork.data.model.Variable.variable;
import static com.mikosik.stork.data.model.Verbs.verb;

import java.math.BigInteger;

import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Noun;

class LinkableVerbs {
  public static Expression addIntegerInteger() {
    return verb("add", (argumentA, argumentB) -> {
      BigInteger numberA = (BigInteger) ((Noun) argumentA).object;
      BigInteger numberB = (BigInteger) ((Noun) argumentB).object;
      return noun(numberA.add(numberB));
    });
  }

  public static Expression negateInteger() {
    return verb("negate", (argument) -> {
      BigInteger number = (BigInteger) ((Noun) argument).object;
      return noun(number.negate());
    });
  }

  public static Expression equalIntegerInteger() {
    return verb("equal", (argumentA, argumentB) -> {
      BigInteger numberA = (BigInteger) ((Noun) argumentA).object;
      BigInteger numberB = (BigInteger) ((Noun) argumentB).object;
      return variable("" + numberA.equals(numberB));
    });
  }

  public static Expression moreThanIntegerInteger() {
    return verb("moreThan", (argumentA, argumentB) -> {
      BigInteger numberA = (BigInteger) ((Noun) argumentA).object;
      BigInteger numberB = (BigInteger) ((Noun) argumentB).object;
      return variable("" + (numberB.compareTo(numberA) > 0));
    });
  }
}
