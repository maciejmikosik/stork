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
import com.mikosik.stork.tool.common.Morph;

public class Bind {
  public static Module bindParameters(Module module) {
    return bindParameters().in(module);
  }

  public static Expression bindParameters(Expression expression) {
    return bindParameters().in(expression);
  }

  private static Morph bindParameters() {
    return morphLambdas(lambda -> bind(lambda.parameter, lambda));
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
      module = bind(identifier).in(module);
    }
    return module;
  }

  public static Expression bindIdentifiers(Chain<Identifier> identifiers, Expression expression) {
    for (Identifier identifier : identifiers) {
      expression = bind(identifier).in(expression);
    }
    return expression;
  }

  private static Morph bind(Identifier identifier) {
    Variable variableToReplace = identifier.toVariable();
    return morphVariables(variable -> variable.name.equals(variableToReplace.name)
        ? identifier
        : variable);
  }
}
