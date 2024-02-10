package com.mikosik.stork.build.link;

import static com.mikosik.stork.common.Strings.reverse;
import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Integer.integer;

import java.math.BigInteger;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.mikosik.stork.common.TriFunction;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Instruction;
import com.mikosik.stork.model.Integer;

public class Bridge {
  public static final Identifier TRUE = identifier("lang.boolean.true");
  public static final Identifier FALSE = identifier("lang.boolean.false");
  public static final Identifier SOME = identifier("lang.stream.some");
  public static final Identifier NONE = identifier("lang.stream.none");

  public static Expression stork(String string) {
    Expression stream = NONE;
    for (char character : reverse(string).toCharArray()) {
      stream = some(integer(character), stream);
    }
    return stream;
  }

  public static Expression some(Expression head, Expression tail) {
    return application(SOME, head, tail);
  }

  public static Expression stork(Boolean value) {
    return value ? TRUE : FALSE;
  }

  public static Expression stork(BigInteger value) {
    return integer(value);
  }

  public static Instruction instruction(
      Function<Expression, Expression> function) {
    return x -> function.apply(x);
  }

  public static Instruction instruction(
      BiFunction<Expression, Expression, Expression> function) {
    return x -> (Instruction) y -> function.apply(x, y);
  }

  public static Instruction instruction(
      TriFunction<Expression, Expression, Expression, Expression> function) {
    return x -> (Instruction) y -> (Instruction) z -> function.apply(x, y, z);
  }

  public static BigInteger javaInteger(Expression expression) {
    return ((Integer) expression).value;
  }
}
