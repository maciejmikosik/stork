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
      Function<? super Definition, ? extends Definition> change) {
    return module -> module(module.definitions.stream()
        .map(change)
        .collect(toSequence()));
  }

  public static Function<Definition, Definition> onIdentifier(
      Function<? super Identifier, ? extends Identifier> change) {
    return definition -> definition(
        change.apply(definition.identifier),
        definition.body);
  }

  public static Function<Identifier, Identifier> onNamespace(
      Function<? super Namespace, ? extends Namespace> change) {
    return identifier -> identifier(
        change.apply(identifier.namespace),
        identifier.variable);
  }

  public static Function<Module, Module> inModule(
      Function<? super Expression, ? extends Expression> change) {
    return onEachDefinition(definition -> definition(
        definition.identifier,
        deep(change).apply(definition.body)));
  }

  public static Function<Expression, Expression> deep(
      Function<? super Expression, ? extends Expression> change) {
    return expression -> expression instanceof Lambda lambda
        ? change.apply(lambda(
            (Parameter) change.apply(lambda.parameter),
            deep(change).apply(lambda.body)))
        : expression instanceof Application application
            ? change.apply(application(
                deep(change).apply(application.function),
                deep(change).apply(application.argument)))
            : change.apply(expression);
  }

  public static Function<Expression, Expression> ifIdentifier(
      Function<? super Identifier, ? extends Expression> change) {
    return expression -> expression instanceof Identifier identifier
        ? change.apply(identifier)
        : expression;
  }

  public static Function<Expression, Expression> ifVariable(
      Function<? super Variable, ? extends Expression> change) {
    return expression -> expression instanceof Variable variable
        ? change.apply(variable)
        : expression;
  }

  public static Function<Expression, Expression> ifLambda(
      Function<? super Lambda, ? extends Expression> change) {
    return expression -> expression instanceof Lambda lambda
        ? change.apply(lambda)
        : expression;
  }

  public static Function<Expression, Expression> ifQuote(
      Function<? super Quote, ? extends Expression> change) {
    return expression -> expression instanceof Quote quote
        ? change.apply(quote)
        : expression;
  }
}
