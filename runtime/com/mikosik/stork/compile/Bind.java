package com.mikosik.stork.compile;

import static com.mikosik.stork.common.Logic.constant;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.change.Changes.changeLambda;
import static com.mikosik.stork.model.change.Changes.changeVariable;
import static com.mikosik.stork.model.change.Changes.inExpression;
import static com.mikosik.stork.model.change.Changes.inModule;
import static com.mikosik.stork.model.change.Changes.onEachDefinition;
import static com.mikosik.stork.model.change.Changes.onIdentifier;
import static com.mikosik.stork.model.change.Changes.onNamespace;

import java.util.Map;
import java.util.function.Function;

import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Linkage;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.model.Namespace;
import com.mikosik.stork.model.Variable;
import com.mikosik.stork.model.change.Change;

public class Bind {
  public static final Change<Expression> bindLambdaParameter = changeLambda(
      lambda -> inExpression(changeVariable(
          variable -> variable.name.equals(lambda.parameter.name)
              ? lambda.parameter
              : variable))
                  .apply(lambda));

  public static Change<Variable> linking(Linkage linkage) {
    Map<String, Expression> map = linkage.links.toHashMap(
        identifier -> identifier.variable.name,
        identifier -> identifier);
    return variable -> map.getOrDefault(variable.name, variable);
  }

  public static Function<Module, Module> export(Namespace namespace) {
    return module -> {
      var names = module.definitions
          .map(definition -> definition.identifier.variable.name)
          .toHashSet();
      return onEachDefinition(onIdentifier(onNamespace(constant(namespace))))
          .andThen(inModule(changeVariable(variable -> names.contains(variable.name)
              ? identifier(namespace, variable)
              : variable)))
          .apply(module);
    };
  }
}
