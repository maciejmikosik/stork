package com.mikosik.stork.tool.compile;

import static com.mikosik.stork.common.Check.check;

import com.mikosik.stork.common.Input;
import com.mikosik.stork.model.Expression;

public class ScopeCompiler implements Compiler<Expression> {
  public Compiler<Void> whitespace;
  public Compiler<Expression> expression;

  public Expression compile(Input input) {
    check(input.read() == '{');
    whitespace.compile(input);
    Expression body = expression.compile(input);
    whitespace.compile(input);
    check(input.read() == '}');
    return body;
  }
}
