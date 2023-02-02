package com.mikosik.stork.tool.link;

import static com.mikosik.stork.tool.link.Changes.changeIdentifier;
import static com.mikosik.stork.tool.link.Changes.changeLambda;
import static com.mikosik.stork.tool.link.Changes.inExpression;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Identifier;

public class Bind {
  public static final Change<Expression> bindLambdaParameter = changeLambda(
      lambda -> inExpression(changeIdentifier(
          identifier -> identifier.name.equals(lambda.parameter.name)
              ? lambda.parameter
              : identifier))
                  .apply(lambda));

  public static Change<Expression> globalizeIdentifier(Chain<Identifier> identifiers) {
    return changeIdentifier(identifier -> identifiers
        .stream()
        .filter(iIdentifier -> identifier.name.equals(iIdentifier.toLocal().name))
        .map(iIdentifier -> (Expression) iIdentifier)
        .findFirst()
        .orElse(identifier));
  }
}
