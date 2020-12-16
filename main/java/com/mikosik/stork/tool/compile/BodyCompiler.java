package com.mikosik.stork.tool.compile;

import static com.mikosik.stork.common.Throwables.fail;

import com.mikosik.stork.common.PeekingInput;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Lambda;

public class BodyCompiler implements Compiler<Expression> {
  public Compiler<Lambda> lambda;
  public Compiler<Expression> scope;

  public Expression compile(PeekingInput input) {
    int character = input.peek();
    if (character == '(') {
      return lambda.compile(input);
    } else if (character == '{') {
      return scope.compile(input);
    } else {
      return fail("expected ( or { but was %c", character);
    }
  }
}
