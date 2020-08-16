package com.mikosik.stork.tool;

import static com.mikosik.stork.data.model.Application.application;
import static com.mikosik.stork.data.model.Switch.switchOn;
import static com.mikosik.stork.data.model.Variable.variable;
import static com.mikosik.stork.data.model.comp.Computation.computation;
import static com.mikosik.stork.tool.common.Computations.abort;
import static java.lang.String.format;

import com.mikosik.stork.data.model.Definition;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Lambda;
import com.mikosik.stork.data.model.Module;
import com.mikosik.stork.data.model.comp.Computation;

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
        .ifComputation(computation -> print(abort(mark(computation))))
        .elseFail();
  }

  private static Computation mark(Computation computation) {
    return computation(
        application(variable("@"), computation.expression),
        computation.stack);
  }

  public static String print(Definition definition) {
    return definition.expression instanceof Lambda
        ? definition.name + print(definition.expression)
        : definition.name + "{" + print(definition.expression) + "}";
  }

  public static String print(Module module) {
    StringBuilder builder = new StringBuilder();
    for (Definition definition : module.definitions) {
      builder.append(print(definition)).append("\n\n");
    }
    return builder.toString();
  }
}
