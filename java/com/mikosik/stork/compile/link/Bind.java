package com.mikosik.stork.compile.link;

import static com.mikosik.stork.common.Logic.constant;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.change.Changes.deep;
import static com.mikosik.stork.model.change.Changes.ifIdentifier;
import static com.mikosik.stork.model.change.Changes.ifLambda;
import static com.mikosik.stork.model.change.Changes.ifVariable;
import static com.mikosik.stork.model.change.Changes.onBody;
import static com.mikosik.stork.model.change.Changes.onEachDefinition;
import static com.mikosik.stork.model.change.Changes.onIdentifier;
import static com.mikosik.stork.model.change.Changes.onNamespace;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.util.Map;
import java.util.function.Function;

import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Library;
import com.mikosik.stork.model.Linkage;
import com.mikosik.stork.model.Namespace;
import com.mikosik.stork.model.Variable;

public class Bind {
  public static final Function<Expression, Expression> bindLambdaParameter = ifLambda(
      lambda -> deep(ifVariable(
          variable -> variable.name.equals(lambda.parameter.name)
              ? lambda.parameter
              : variable))
                  .apply(lambda));

  public static Function<Variable, Expression> linking(Linkage linkage) {
    Map<Variable, Expression> map = linkage.links.stream()
        .collect(toMap(
            link -> link.variable,
            link -> link.identifier));
    return variable -> map.getOrDefault(variable, variable);
  }

  public static Function<Library, Library> export(Namespace namespace) {
    return library -> {
      var variables = library.definitions.stream()
          .map(definition -> definition.identifier.variable)
          .collect(toSet());
      return onEachDefinition(onIdentifier(onNamespace(constant(namespace))))
          .andThen(onEachDefinition(onBody(deep(
              ifVariable(variable -> variables.contains(variable)
                  ? identifier(namespace, variable)
                  : variable)))))
          .apply(library);
    };
  }

  public static Expression removeNamespaces(Expression expression) {
    return deep(ifIdentifier(identifier -> identifier(identifier.variable)))
        .apply(expression);
  }
}
