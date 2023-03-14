package com.mikosik.stork.compile;

import static com.mikosik.stork.common.Logic.constant;
import static com.mikosik.stork.common.Sequence.toSequence;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Module.module;
import static com.mikosik.stork.model.change.Changes.changeLambda;
import static com.mikosik.stork.model.change.Changes.changeVariable;
import static com.mikosik.stork.model.change.Changes.inExpression;
import static com.mikosik.stork.model.change.Changes.inModule;
import static com.mikosik.stork.model.change.Changes.onEachDefinition;
import static com.mikosik.stork.model.change.Changes.onIdentifier;
import static com.mikosik.stork.model.change.Changes.onNamespace;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.util.List;
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
    Map<String, Expression> map = linkage.links.stream()
        .collect(toMap(
            link -> link.variable.name,
            link -> link.identifier));
    return variable -> map.getOrDefault(variable.name, variable);
  }

  public static Function<Module, Module> export(Namespace namespace) {
    return module -> {
      var names = module.definitions.stream()
          .map(definition -> definition.identifier.variable.name)
          .collect(toSet());
      return onEachDefinition(onIdentifier(onNamespace(constant(namespace))))
          .andThen(inModule(changeVariable(variable -> names.contains(variable.name)
              ? identifier(namespace, variable)
              : variable)))
          .apply(module);
    };
  }

  public static Module join(List<Module> modules) {
    return module(modules.stream()
        .flatMap(module -> module.definitions.stream())
        .collect(toSequence()));
  }
}
