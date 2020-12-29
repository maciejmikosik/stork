package com.mikosik.stork.tool.common;

import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Definition.definition;
import static com.mikosik.stork.model.Lambda.lambda;
import static com.mikosik.stork.model.Module.module;

import com.mikosik.stork.model.Application;
import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Lambda;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.model.Parameter;
import com.mikosik.stork.model.Quote;
import com.mikosik.stork.model.Variable;

public class Traverser {
  public Traverser() {}

  public Module traverse(Module module) {
    return module(module.definitions.map(this::traverse));
  }

  public Definition traverse(Definition definition) {
    return definition(
        traverseDefinitionName(definition.variable),
        traverse(definition.expression));
  }

  protected Variable traverseDefinitionName(Variable variable) {
    return variable;
  }

  public Expression traverse(Expression expression) {
    if (expression instanceof Application) {
      return traverse((Application) expression);
    } else if (expression instanceof Lambda) {
      return traverse((Lambda) expression);
    } else if (expression instanceof Variable) {
      return traverse((Variable) expression);
    } else if (expression instanceof Parameter) {
      return traverse((Parameter) expression);
    } else if (expression instanceof Quote) {
      return traverse((Quote) expression);
    } else {
      return expression;
    }
  }

  protected Expression traverse(Lambda lambda) {
    return lambda(lambda.parameter, traverse(lambda.body));
  }

  protected Expression traverse(Application application) {
    return application(
        traverse(application.function),
        traverse(application.argument));
  }

  protected Expression traverse(Variable variable) {
    return variable;
  }

  protected Expression traverse(Parameter parameter) {
    return parameter;
  }

  protected Expression traverse(Quote quote) {
    return quote;
  }
}
