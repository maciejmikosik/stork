package com.mikosik.stork.tool.common;

import static com.mikosik.stork.common.Strings.reverse;
import static com.mikosik.stork.data.model.Application.application;
import static com.mikosik.stork.data.model.Integer.integer;
import static com.mikosik.stork.data.model.Variable.variable;
import static com.mikosik.stork.tool.common.Invocation.asInvocation;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.Iterator;

import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Integer;

public class Translate {
  public static Expression asStorkBoolean(boolean bool) {
    return variable(Boolean.toString(bool));
  }

  public static Expression asStorkInteger(BigInteger value) {
    return integer(value);
  }

  /**
   * Builds expression which is stream of integers. Each integer is unicode codepoint of consecutive
   * character in string. String must contain only ASCII characters. If string is
   * <code>"abc"</code>, returns expression <code>some(97)(some(98)(some(99)(none)))</code>
   */
  public static Expression asStorkStream(String ascii) {
    Expression some = variable("some");
    Expression stream = variable("none");
    for (char asciiCharacter : reverse(ascii).toCharArray()) {
      Expression integer = integer(BigInteger.valueOf(asciiCharacter));
      stream = application(application(some, integer), stream);
    }
    return stream;
  }

  public static BigInteger asJavaBigInteger(Expression expression) {
    Integer integer = (Integer) expression;
    return integer.value;
  }

  public static String asJavaString(Expression expression) {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    writeTo(buffer, expression);
    return new String(buffer.toByteArray());
  }

  private static void writeTo(ByteArrayOutputStream buffer, Expression expression) {
    Invocation invocation = asInvocation(expression);
    if (invocation.function.name.equals("some")) {
      Iterator<Expression> iterator = invocation.arguments.iterator();
      BigInteger oneByte = asJavaBigInteger(iterator.next());
      Expression tail = iterator.next();
      buffer.write(oneByte.intValueExact());
      writeTo(buffer, tail);
    } else if (invocation.function.name.equals("none")) {
      return;
    } else {
      throw new RuntimeException();
    }
  }
}
