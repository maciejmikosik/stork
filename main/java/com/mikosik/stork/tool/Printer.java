package com.mikosik.stork.tool;

import static com.mikosik.stork.common.Throwables.throwing;
import static com.mikosik.stork.data.model.Application.application;
import static com.mikosik.stork.data.model.Switch.switchOn;
import static com.mikosik.stork.data.model.Variable.variable;
import static java.lang.String.format;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Definition;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Lambda;
import com.mikosik.stork.data.model.Library;

public class Printer {
  public static String print(Expression expression) {
    return switchOn(expression)
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
        .ifRunning(running -> print(flatten(running.stack)))
        .elseFail();
  }

  private static Expression flatten(Chain<Expression> stack) {
    return stack.visit(
        (head, tail) -> flatten(application(variable("@"), head), tail),
        () -> throwing(new RuntimeException()));
  }

  private static Expression flatten(Expression expression, Chain<Expression> stack) {
    for (Expression frame : stack) {
      expression = join(expression, frame);
    }
    return expression;
  }

  private static Expression join(Expression child, Expression parent) {
    return switchOn(parent)
        .ifApplication(application -> switchOn(application.function)
            .ifCore(core -> application(application.function, child))
            .elseReturn(() -> application(child, application.argument)))
        .elseFail();
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
