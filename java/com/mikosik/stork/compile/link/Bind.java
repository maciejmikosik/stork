package com.mikosik.stork.compile.link;

import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.change.Changes.deep;
import static com.mikosik.stork.model.change.Changes.ifIdentifier;
import static com.mikosik.stork.model.change.Changes.ifLambda;
import static com.mikosik.stork.model.change.Changes.ifVariable;

import java.util.function.Function;

import com.mikosik.stork.model.Expression;

public class Bind {
  public static final Function<Expression, Expression> bindLambdaParameter = ifLambda(
      lambda -> deep(ifVariable(
          variable -> variable.name.equals(lambda.parameter.name)
              ? lambda.parameter
              : variable))
                  .apply(lambda));

  public static Expression removeNamespaces(Expression expression) {
    return deep(ifIdentifier(identifier -> identifier(identifier.variable)))
        .apply(expression);
  }
}
