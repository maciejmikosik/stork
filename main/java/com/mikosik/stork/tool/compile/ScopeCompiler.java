package com.mikosik.stork.tool.compile;

import static com.mikosik.stork.common.Check.check;

import com.mikosik.stork.common.PeekingInput;
import com.mikosik.stork.data.model.Expression;

public class ScopeCompiler implements Compiler<Expression> {
  public Compiler<Void> whitespace;
  public Compiler<Expression> expression;

  public Expression compile(PeekingInput input) {
    check(input.read() == '{');
    whitespace.compile(input);
    Expression body = expression.compile(input);
    whitespace.compile(input);
    check(input.read() == '}');
    return body;
  }
}
