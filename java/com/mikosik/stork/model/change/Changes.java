package com.mikosik.stork.model.change;

import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Definition.definition;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Lambda.lambda;
import static java.util.stream.Stream.concat;

import java.util.stream.Stream;

import com.mikosik.stork.common.func.Functions.Faa;
import com.mikosik.stork.common.func.Functions.Fab;
import com.mikosik.stork.model.Application;
import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Lambda;
import com.mikosik.stork.model.Namespace;
import com.mikosik.stork.model.Parameter;
import com.mikosik.stork.model.Quote;
import com.mikosik.stork.model.Variable;

public class Changes {
  public static Faa<Definition> onIdentifier(Faa<Identifier> change) {
    return definition -> definition(
        change.apply(definition.identifier),
        definition.body);
  }

  public static Faa<Definition> onBody(Faa<Expression> change) {
    return definition -> definition(
        definition.identifier,
        change.apply(definition.body));
  }

  public static Faa<Identifier> onNamespace(Faa<Namespace> change) {
    return identifier -> identifier(
        change.apply(identifier.namespace),
        identifier.variable);
  }

  public static Faa<Expression> deep(Faa<Expression> change) {
    return expression -> switch (expression) {
      case Lambda lambda -> change.apply(lambda(
          (Parameter) change.apply(lambda.parameter),
          deep(change).apply(lambda.body)));
      case Application application -> change.apply(application(
          deep(change).apply(application.function),
          deep(change).apply(application.argument)));
      default -> change.apply(expression);
    };
  }

  public static Faa<Expression> ifIdentifier(Fab<Identifier, Expression> change) {
    return expression -> switch (expression) {
      case Identifier identifier -> change.apply(identifier);
      default -> expression;
    };
  }

  public static Faa<Expression> ifVariable(Fab<Variable, Expression> change) {
    return expression -> switch (expression) {
      case Variable variable -> change.apply(variable);
      default -> expression;
    };
  }

  public static Faa<Expression> ifLambda(Fab<Lambda, Expression> change) {
    return expression -> switch (expression) {
      case Lambda lambda -> change.apply(lambda);
      default -> expression;
    };
  }

  public static Faa<Expression> ifQuote(Fab<Quote, Expression> change) {
    return expression -> switch (expression) {
      case Quote quote -> change.apply(quote);
      default -> expression;
    };
  }

  public static Stream<Expression> walk(Expression expression) {
    return concat(
        walkChildren(expression),
        Stream.of(expression));
  }

  private static Stream<Expression> walkChildren(Expression expression) {
    return switch (expression) {
      case Lambda lambda -> concat(
          Stream.of(lambda.parameter),
          walk(lambda.body));
      case Application application -> concat(
          walk(application.function),
          walk(application.argument));
      default -> Stream.of();
    };
  }
}
