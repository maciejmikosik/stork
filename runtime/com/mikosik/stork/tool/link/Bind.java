package com.mikosik.stork.tool.link;

import static com.mikosik.stork.tool.link.Changes.changeLambda;
import static com.mikosik.stork.tool.link.Changes.changeVariable;
import static com.mikosik.stork.tool.link.Changes.inExpression;
import static com.mikosik.stork.tool.link.Changes.inModule;
import static com.mikosik.stork.tool.link.Modules.changeDefinitionName;
import static com.mikosik.stork.tool.link.Modules.each;

import java.util.function.Function;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Module;

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
        .filter(identifier -> variable.name.equals(identifier.toLocal().name))
        .map(identifier -> (Expression) identifier)
        .findFirst()
        .orElse(variable));
  }

  public static Function<Module, Module> export(String namespace) {
    return module -> {
      var globalModule = each(changeDefinitionName(name -> namespace + name)).apply(module);
      var globalNames = globalModule.definitions.map(definition -> definition.identifier);
      return inModule(identifyVariables(globalNames)).apply(globalModule);
    };
  }
}
