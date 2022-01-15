package com.mikosik.stork.tool.link;

import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.tool.common.Morph.morphIdentifiers;
import static com.mikosik.stork.tool.common.Morph.morphLambdas;
import static com.mikosik.stork.tool.common.Morph.morphVariables;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Model;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.model.Parameter;
import com.mikosik.stork.model.Variable;

public class Bind {
  public static Module bindParameters(Module module) {
    return morphLambdas(lambda -> bind(lambda.parameter, lambda)).in(module);
  }

  private static Expression bind(Parameter parameter, Expression expression) {
    return morphVariables(variable -> bind(parameter, variable)).in(expression);
  }

  private static Model bind(Parameter parameter, Variable variable) {
    return variable.name.equals(parameter.name)
        ? parameter
        : variable;
  }

  public static Module bindNamespace(String namespace, Module module) {
    return morphIdentifiers(identifier -> bindNamespace(namespace, identifier)).in(module);
  }

  private static Identifier bindNamespace(String namespace, Identifier identifier) {
    return identifier(namespace + identifier.name);
  }

  public static Module bindDefinitions(Module module) {
    Chain<Identifier> identifiers = module.definitions
        .map(definition -> definition.identifier);
    return bindIdentifiers(identifiers, module);
  }

  public static Module bindIdentifiers(Chain<Identifier> identifiers, Module module) {
    for (Identifier identifier : identifiers) {
      module = bind(identifier, module);
    }
    return module;
  }

  private static Module bind(Identifier identifier, Module module) {
    Variable variableToReplace = identifier.toVariable();
    return morphVariables(variable -> variable.name.equals(variableToReplace.name)
        ? identifier
        : variable)
            .in(module);
  }
}
