package com.mikosik.stork.tool;

import static com.mikosik.stork.data.model.ExpressionSwitcher.expressionSwitcherReturning;
import static java.lang.String.format;

import com.mikosik.stork.data.model.Definition;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Lambda;
import com.mikosik.stork.data.model.Library;

public class Printer {
  public static String print(Expression expression) {
    return expressionSwitcherReturning(String.class)
        .ifPrimitive(primitive -> primitive.object.toString())
        .ifVariable(variable -> variable.name)
        .ifParameter(parameter -> parameter.name)
        .ifApplication(application -> format("%s(%s)",
            print(application.function),
            print(application.argument)))
        .ifLambda(lambda -> lambda.body instanceof Lambda
            ? format("(%s)%s", print(lambda.parameter), print(lambda.body))
            : format("(%s){%s}", print(lambda.parameter), print(lambda.body)))
        .ifCore(core -> core.toString())
        .apply(expression);
  }

  public static String print(Definition definition) {
    return definition.expression instanceof Lambda
        ? definition.name + print(definition.expression)
        : definition.name + "{" + print(definition.expression) + "}";
  }

  public static String print(Library library) {
    StringBuilder builder = new StringBuilder();
    for (Definition definition : library.definitions) {
      builder.append(print(definition)).append("\n\n");
    }
    return builder.toString();
  }
}
