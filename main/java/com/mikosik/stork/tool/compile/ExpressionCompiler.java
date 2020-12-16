package com.mikosik.stork.tool.compile;

import static com.mikosik.stork.common.Ascii.DOUBLE_QUOTE;
import static com.mikosik.stork.common.Ascii.isLetter;
import static com.mikosik.stork.common.Ascii.isNumeric;
import static com.mikosik.stork.common.Throwables.fail;

import com.mikosik.stork.common.Input;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Lambda;

public class ExpressionCompiler implements Compiler<Expression> {
  public Compiler<Expression> integer;
  public Compiler<Lambda> lambda;
  public Compiler<Expression> quote;
  public Compiler<Expression> invocation;

  public Expression compile(Input input) {
    int character = input.peek();
    if (isNumeric(character)) {
      return integer.compile(input);
    } else if (isLetter(character)) {
      return invocation.compile(input);
    } else if (character == DOUBLE_QUOTE) {
      return quote.compile(input);
    } else if (character == '(') {
      return lambda.compile(input);
    } else {
      return fail("unexpected character %c", character);
    }
  }
}
