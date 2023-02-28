package com.mikosik.stork.tool.common;

import static com.mikosik.stork.common.Strings.reverse;
import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Integer.integer;

import java.math.BigInteger;

import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Integer;

public class Bridge {
  public static final Identifier TRUE = identifier("stork.boolean.true");
  public static final Identifier FALSE = identifier("stork.boolean.false");
  public static final Identifier SOME = identifier("stork.stream.some");
  public static final Identifier NONE = identifier("stork.stream.none");

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

  public static BigInteger javaInteger(Expression expression) {
    return ((Integer) expression).value;
  }
}
