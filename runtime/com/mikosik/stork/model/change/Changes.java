package com.mikosik.stork.model.change;

import static com.mikosik.stork.common.Sequence.toSequence;
import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Definition.definition;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Lambda.lambda;
import static com.mikosik.stork.model.Module.module;

import java.util.function.Function;

import com.mikosik.stork.model.Application;
import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Lambda;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.model.Namespace;
import com.mikosik.stork.model.Parameter;
import com.mikosik.stork.model.Quote;
import com.mikosik.stork.model.Variable;

public class Changes {
  public static Function<Module, Module> onEachDefinition(
      Function<? super Definition, Definition> transform) {
    return module -> module(module.definitions.stream()
        .map(transform)
        .collect(toSequence()));
  }

  public static Function<Definition, Definition> onIdentifier(
      Function<? super Identifier, Identifier> change) {
    return definition -> definition(
        change.apply(definition.identifier),
        definition.body);
  }

  public static Function<Identifier, Identifier> onNamespace(
      Function<? super Namespace, Namespace> change) {
    return identifier -> identifier(
        change.apply(identifier.namespace),
        identifier.variable);
  }

  public static Function<Module, Module> inModule(Change<Expression> change) {
    return onEachDefinition(definition -> definition(
        (Identifier) change.apply(definition.identifier),
        inExpression(change).apply(definition.body)));
  }

  public static Change<Expression> inExpression(Change<Expression> change) {
    return expression -> expression instanceof Lambda lambda
        ? change.apply(lambda(
            (Parameter) change.apply(lambda.parameter),
            inExpression(change).apply(lambda.body)))
        : expression instanceof Application application
            ? change.apply(application(
                inExpression(change).apply(application.function),
                inExpression(change).apply(application.argument)))
            : change.apply(expression);
  }

  public static Change<Expression> changeIdentifier(Change<Identifier> change) {
    return expression -> expression instanceof Identifier identifier
        ? change.apply(identifier)
        : expression;
  }

  public static Change<Expression> changeVariable(Change<Variable> change) {
    return expression -> expression instanceof Variable variable
        ? change.apply(variable)
        : expression;
  }

  public static Change<Expression> changeLambda(Change<Lambda> change) {
    return expression -> expression instanceof Lambda lambda
        ? change.apply(lambda)
        : expression;
  }

  public static Change<Expression> changeQuote(Change<Quote> change) {
    return expression -> expression instanceof Quote quote
        ? change.apply(quote)
        : expression;
  }
}