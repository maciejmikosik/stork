package com.mikosik.stork.tool.link;

import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Lambda.lambda;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Lambda;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.model.Parameter;
import com.mikosik.stork.model.Variable;
import com.mikosik.stork.tool.common.Traverser;

public class Bind {
  public static Module bindParameters(Module module) {
    return new Traverser() {
      protected Expression traverse(Lambda lambda) {
        return lambda(lambda.parameter, bind(lambda.parameter, traverse(lambda.body)));
      }
    }.traverse(module);
  }

  public static Expression bind(Parameter parameter, Expression expression) {
    return new Traverser() {
      protected Expression traverse(Variable variable) {
        return variable.name.equals(parameter.name)
            ? parameter
            : variable;
      }
    }.traverse(expression);
  }

  public static Module bindNamespace(String namespace, Module module) {
    return new Traverser() {
      protected Identifier traverse(Identifier identifier) {
        return identifier(namespace + identifier.name);
      }
    }.traverse(module);
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

  public static Module bind(Identifier identifier, Module module) {
    Variable variableToReplace = identifier.toVariable();
    return new Traverser() {
      protected Expression traverse(Variable variable) {
        return variable.name.equals(variableToReplace.name)
            ? identifier
            : variable;
      }
    }.traverse(module);
  }
}
