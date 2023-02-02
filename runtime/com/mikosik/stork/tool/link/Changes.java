package com.mikosik.stork.tool.link;

import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Definition.definition;
import static com.mikosik.stork.model.Lambda.lambda;
import static com.mikosik.stork.model.Module.module;

import java.util.function.Function;

import com.mikosik.stork.model.Application;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Lambda;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.model.Parameter;
import com.mikosik.stork.model.Quote;

public class Changes {
  public static Function<Module, Module> inModule(Change<Expression> change) {
    return module -> module(module.definitions.map(definition -> definition(
        (Identifier) change.apply(definition.identifier),
        inExpression(change).apply(definition.body))));
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
