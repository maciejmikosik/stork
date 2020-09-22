package com.mikosik.stork.tool.common;

import static com.mikosik.stork.common.Strings.reverse;
import static com.mikosik.stork.data.model.Application.application;
import static com.mikosik.stork.data.model.Integer.integer;
import static com.mikosik.stork.data.model.Variable.variable;

import java.math.BigInteger;

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
}
