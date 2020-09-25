package com.mikosik.stork.tool.common;

import static com.mikosik.stork.data.model.Switch.switchOn;
import static java.lang.String.format;

import com.mikosik.stork.data.model.Definition;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Lambda;
import com.mikosik.stork.data.model.Module;

public class Expressions {
  public static String print(Expression expression) {
    return switchOn(expression)
        .ifInteger(integer -> integer.value.toString())
        .ifVariable(variable -> variable.name)
        .ifParameter(parameter -> parameter.name)
        .ifApplication(application -> format("%s(%s)",
            print(application.function),
            print(application.argument)))
        .ifLambda(lambda -> lambda.body instanceof Lambda
            ? format("(%s)%s", print(lambda.parameter), print(lambda.body))
            : format("(%s){%s}", print(lambda.parameter), print(lambda.body)))
        .elseReturn(() -> String.valueOf(expression));
  }

  public static String print(Definition definition) {
    return definition.expression instanceof Lambda
        ? print(definition.variable) + print(definition.expression)
        : print(definition.variable) + "{" + print(definition.expression) + "}";
  }

  public static String print(Module module) {
    StringBuilder builder = new StringBuilder();
    for (Definition definition : module.definitions) {
      builder.append(print(definition)).append("\n\n");
    }
    return builder.toString();
  }
}
