package com.mikosik.stork.compile;

import static com.mikosik.stork.common.Logic.constant;
import static com.mikosik.stork.model.change.Changes.changeLambda;
import static com.mikosik.stork.model.change.Changes.changeVariable;
import static com.mikosik.stork.model.change.Changes.inExpression;
import static com.mikosik.stork.model.change.Changes.inModule;
import static com.mikosik.stork.model.change.Changes.onEachDefinition;
import static com.mikosik.stork.model.change.Changes.onIdentifier;
import static com.mikosik.stork.model.change.Changes.onNamespace;

import java.util.function.Function;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.model.Namespace;
import com.mikosik.stork.model.change.Change;

public class Bind {
  public static final Change<Expression> bindLambdaParameter = changeLambda(
      lambda -> inExpression(changeVariable(
          variable -> variable.name.equals(lambda.parameter.name)
              ? lambda.parameter
              : variable))
                  .apply(lambda));

  public static Change<Expression> identifyVariables(Chain<Identifier> identifiers) {
    return changeVariable(variable -> identifiers
        .stream()
        .filter(identifier -> variable.name.equals(identifier.variable.name))
        .map(identifier -> (Expression) identifier)
        .findFirst()
        .orElse(variable));
  }

  public static Function<Module, Module> export(Namespace namespace) {
    return module -> {
      var globalModule = onEachDefinition(onIdentifier(onNamespace(constant(namespace))))
          .apply(module);
      var globalNames = globalModule.definitions.map(definition -> definition.identifier);
      return inModule(identifyVariables(globalNames)).apply(globalModule);
    };
  }
}
