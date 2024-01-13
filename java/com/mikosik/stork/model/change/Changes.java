package com.mikosik.stork.model.change;

import static com.mikosik.stork.common.Sequence.toSequence;
import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Definition.definition;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Lambda.lambda;
import static com.mikosik.stork.model.Module.module;
import static java.util.stream.Stream.concat;

import java.util.function.Function;
import java.util.stream.Stream;

import com.mikosik.stork.model.Application;
import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.EagerInstruction;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Lambda;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.model.NamedInstruction;
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

  public static Function<Definition, Definition> onBody(
      Function<? super Expression, ? extends Expression> change) {
    return definition -> definition(
        definition.identifier,
        change.apply(definition.body));
  }

  public static Function<Identifier, Identifier> onNamespace(
      Function<? super Namespace, ? extends Namespace> change) {
    return identifier -> identifier(
        change.apply(identifier.namespace),
        identifier.variable);
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

  public static Stream<Expression> walk(Expression expression) {
    return concat(
        walkChildren(expression),
        Stream.of(expression));
  }

  private static Stream<Expression> walkChildren(Expression expression) {
    if (expression instanceof Lambda lambda) {
      return concat(
          Stream.of(lambda.parameter),
          walk(lambda.body));
    } else if (expression instanceof Application application) {
      return concat(
          walk(application.function),
          walk(application.argument));
    } else if (expression instanceof EagerInstruction instruction) {
      return concat(
          walk(instruction.instruction),
          Stream.of(instruction));
    } else if (expression instanceof NamedInstruction instruction) {
      return concat(
          walk(instruction.instruction),
          Stream.of(instruction));
    } else {
      return Stream.of();
    }
  }
}
