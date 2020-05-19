package com.mikosik.stork.tool.common;

import static com.mikosik.stork.common.Strings.reverse;
import static com.mikosik.stork.data.model.Application.application;
import static com.mikosik.stork.data.model.Noun.noun;
import static com.mikosik.stork.data.model.Variable.variable;

import java.math.BigInteger;

import com.mikosik.stork.data.model.Expression;

public class StringExpression {
  /**
   * Builds expression which is stream of integers. Each integer is unicode codepoint of consecutive
   * character in string. String must contain only ASCII characters. If string is
   * <code>"abc"</code>, returns expression <code>some(97)(some(98)(some(99)(none)))</code>
   */
  public static Expression stringExpression(String ascii) {
    Expression some = variable("some");
    Expression stream = variable("none");
    for (char asciiCharacter : reverse(ascii).toCharArray()) {
      Expression integer = noun(BigInteger.valueOf(asciiCharacter));
      stream = application(application(some, integer), stream);
    }
    return stream;
  }
}
