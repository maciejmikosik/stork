package com.mikosik.stork.tool.decompile;

import static com.mikosik.stork.common.Throwables.fail;
import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Computation.computation;
import static com.mikosik.stork.model.Variable.variable;
import static com.mikosik.stork.tool.common.Computations.abort;
import static java.nio.charset.StandardCharsets.US_ASCII;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.common.Output;
import com.mikosik.stork.model.Application;
import com.mikosik.stork.model.Computation;
import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Integer;
import com.mikosik.stork.model.Lambda;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.model.Parameter;
import com.mikosik.stork.model.Variable;
import com.mikosik.stork.tool.common.Scope;

public class Printer {
  private final Scope scope;
  private final Output output;

  private Printer(Scope scope, Output output) {
    this.scope = scope;
    this.output = output;
  }

  public static Printer printer(Scope scope, Output output) {
    return new Printer(scope, output);
  }

  public Printer print(Object code) {
    return code instanceof Expression
        ? print((Expression) code)
        : code instanceof Definition
            ? print((Definition) code)
            : code instanceof Module
                ? print((Module) code)
                : code instanceof Computation
                    ? print((Computation) code)
                    : fail("");
  }

  private Printer print(Expression expression) {
    return expression instanceof Integer
        ? print((Integer) expression)
        : expression instanceof Variable
            ? print((Variable) expression)
            : expression instanceof Parameter
                ? print((Parameter) expression)
                : expression instanceof Application
                    ? print((Application) expression)
                    : expression instanceof Lambda
                        ? print((Lambda) expression)
                        : print(expression.toString());
  }

  private Printer print(Integer integer) {
    return print(integer.value.toString());
  }

  private Printer print(Variable variable) {
    return print(scope.format(variable));
  }

  private Printer print(Parameter parameter) {
    return print(parameter.name);
  }

  private Printer print(Application application) {
    return this
        .print(application.function)
        .print('(')
        .print(application.argument)
        .print(')');
  }

  private Printer print(Lambda lambda) {
    return this
        .print('(')
        .print(lambda.parameter)
        .print(')')
        .printBody(lambda.body);
  }

  private Printer print(Definition definition) {
    return this
        .print(definition.variable)
        .printBody(definition.expression);
  }

  private Printer printBody(Expression expression) {
    boolean isLambda = expression instanceof Lambda;
    return this
        .print(isLambda ? "" : "{")
        .print(expression)
        .print(isLambda ? "" : "}");
  }

  private Printer print(Module module) {
    return print(module.definitions);
  }

  private Printer print(Chain<Definition> definitions) {
    return definitions.isEmpty()
        ? this
        : printNotEmpty(definitions);
  }

  private Printer printNotEmpty(Chain<Definition> definitions) {
    return this
        .print(definitions.head())
        .print(definitions.tail().isEmpty() ? "" : " ")
        .print(definitions.tail());
  }

  private Printer print(Computation computation) {
    return print(abort(mark(computation)));
  }

  private Printer print(char character) {
    output.write(character);
    return this;
  }

  private Printer print(String string) {
    output.write(string.getBytes(US_ASCII));
    return this;
  }

  private static Computation mark(Computation computation) {
    return computation(
        application(variable("@"), computation.expression),
        computation.stack);
  }
}
