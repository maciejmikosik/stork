package com.mikosik.stork.tool.compile;

import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.model.Lambda.lambda;
import static com.mikosik.stork.model.Parameter.parameter;

import com.mikosik.stork.common.Input;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Lambda;
import com.mikosik.stork.model.Parameter;
import com.mikosik.stork.model.Variable;
import com.mikosik.stork.tool.common.Traverser;

public class LambdaCompiler implements Compiler<Lambda> {
  public Compiler<Void> whitespace;
  public Compiler<String> alphanumeric;
  public Compiler<Expression> body;

  public Lambda compile(Input input) {
    check(input.read() == '(');
    whitespace.compile(input);
    Parameter parameter = parameter(alphanumeric.compile(input));
    whitespace.compile(input);
    check(input.read() == ')');
    whitespace.compile(input);
    return lambda(parameter, bind(parameter, body.compile(input)));
  }

  private static Expression bind(Parameter parameter, Expression expression) {
    return new Traverser() {
      protected Expression traverse(Variable variable) {
        return variable.name.equals(parameter.name)
            ? parameter
            : variable;
      }

      protected Expression traverse(Lambda lambda) {
        return lambda.parameter.name.equals(parameter.name)
            ? lambda // TODO test shadowing
            : super.traverse(lambda);
      }
    }.traverse(expression);
  }
}
