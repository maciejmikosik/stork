package com.mikosik.stork.tool.compile;

import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.data.model.Application.application;

import com.mikosik.stork.common.Input;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Variable;

public class InvocationCompiler implements Compiler<Expression> {
  public Compiler<Void> whitespace;
  public Compiler<Variable> variable;
  public Compiler<Expression> expression;

  public Expression compile(Input input) {
    Expression result = variable.compile(input);
    whitespace.compile(input);
    while (input.peek() == '(') {
      input.read();
      whitespace.compile(input);
      Expression argument = expression.compile(input);
      whitespace.compile(input);
      check(input.read() == ')');
      result = application(result, argument);
      whitespace.compile(input);
    }
    return result;
  }
}
