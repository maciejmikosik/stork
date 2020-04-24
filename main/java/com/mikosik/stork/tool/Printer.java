package com.mikosik.stork.tool;

import static com.mikosik.stork.common.Chain.add;
import static com.mikosik.stork.common.Throwables.throwing;
import static com.mikosik.stork.data.model.Application.application;
import static com.mikosik.stork.data.model.Switch.switchOn;
import static com.mikosik.stork.data.model.Variable.variable;
import static com.mikosik.stork.tool.Expressions.ascend;
import static java.lang.String.format;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Definition;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Lambda;
import com.mikosik.stork.data.model.Library;

public class Printer {
  public static String print(Expression expression) {
    return switchOn(expression)
        .ifNoun(noun -> noun.object.toString())
        .ifVariable(variable -> variable.name)
        .ifParameter(parameter -> parameter.name)
        .ifApplication(application -> format("%s(%s)",
            print(application.function),
            print(application.argument)))
        .ifLambda(lambda -> lambda.body instanceof Lambda
            ? format("(%s)%s", print(lambda.parameter), print(lambda.body))
            : format("(%s){%s}", print(lambda.parameter), print(lambda.body)))
        .ifVerb(verb -> verb.toString())
        .ifRunning(running -> print(ascend(mark(running.stack))))
        .elseFail();
  }

  private static Chain<Expression> mark(Chain<Expression> stack) {
    return stack.visit(
        (head, tail) -> add(application(variable("@"), head), tail),
        () -> throwing(new RuntimeException()));
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
