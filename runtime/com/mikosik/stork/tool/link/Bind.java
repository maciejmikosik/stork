package com.mikosik.stork.tool.link;

import static com.mikosik.stork.tool.link.Changes.changeIdentifier;
import static com.mikosik.stork.tool.link.Changes.changeLambda;
import static com.mikosik.stork.tool.link.Changes.inExpression;
import static com.mikosik.stork.tool.link.Changes.inModule;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Lambda;
import com.mikosik.stork.model.Module;

public class Bind {
  public static Module bindParameters(Module module) {
    return inModule(Bind::bindParameters)
        .apply(module);
  }

  public static Expression bindParameters(Expression expression) {
    return inExpression(changeLambda(Bind::bindParameterOf))
        .apply(expression);
  }

  private static Expression bindParameterOf(Lambda lambda) {
    return inExpression(changeIdentifier(
        identifier -> identifier.name.equals(lambda.parameter.name)
            ? lambda.parameter
            : identifier))
                .apply(lambda);
  }

  public static Module bindIdentifiers(Chain<Identifier> identifiers, Module module) {
    return inModule(changeVariablesTo(identifiers)).apply(module);
  }

  public static Expression bindIdentifiers(Chain<Identifier> identifiers, Expression expression) {
    return changeVariablesTo(identifiers).apply(expression);
  }

  private static Change<Expression> changeVariablesTo(Chain<Identifier> identifiers) {
    return inExpression(changeIdentifier(identifier -> identifiers
        .stream()
        .filter(iIdentifier -> identifier.name.equals(iIdentifier.toLocal().name))
        .map(iIdentifier -> (Expression) iIdentifier)
        .findFirst()
        .orElse(identifier)));
  }
}
