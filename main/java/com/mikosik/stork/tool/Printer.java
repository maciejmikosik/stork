package com.mikosik.stork.tool;

import static com.mikosik.stork.data.model.ExpressionSwitcher.expressionSwitcherReturning;
import static java.lang.String.format;

import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Lambda;

public class Printer {
  private Printer() {}

  public static Printer printer() {
    return new Printer();
  }

  public String print(Expression expression) {
    return expressionSwitcherReturning(String.class)
        .ifPrimitive(primitive -> primitive.object.toString())
        .ifVariable(variable -> variable.name)
        .ifApplication(application -> format("%s(%s)",
            print(application.function),
            print(application.argument)))
        .ifLambda(lambda -> lambda.body instanceof Lambda
            ? format("(%s)%s", lambda.parameter, print(lambda.body))
            : format("(%s){ %s }", lambda.parameter, print(lambda.body)))
        .apply(expression);
  }
}
