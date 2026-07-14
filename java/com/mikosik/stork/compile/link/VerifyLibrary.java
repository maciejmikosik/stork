package com.mikosik.stork.compile.link;

import static com.mikosik.stork.common.Collections.filter;
import static com.mikosik.stork.common.ImmutableList.join;
import static com.mikosik.stork.model.change.Changes.walk;
import static com.mikosik.stork.problem.compile.link.FunctionNotDefined.functionNotDefined;
import static com.mikosik.stork.problem.compile.link.VariableCannotBeBound.variableCannotBeBound;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

import java.util.List;

import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Variable;
import com.mikosik.stork.problem.compile.link.CannotLink;
import com.mikosik.stork.problem.compile.link.DuplicatedFunction;
import com.mikosik.stork.problem.compile.link.FunctionNotDefined;
import com.mikosik.stork.problem.compile.link.VariableCannotBeBound;

public class VerifyLibrary {
  public static List<CannotLink> findLinkingProblems(List<Definition> library) {
    return join(
        findVariableCannotBeFound(library),
        findFunctionNotDefined(library),
        findDuplicatedFunction(library));
  }

  private static List<VariableCannotBeBound> findVariableCannotBeFound(
      List<Definition> library) {
    return library.stream()
        .flatMap(definition -> walk(definition.body)
            .flatMap(filter(Variable.class))
            .map(variable -> variableCannotBeBound(definition.identifier, variable)))
        .toList();
  }

  private static List<FunctionNotDefined> findFunctionNotDefined(
      List<Definition> library) {
    var definedIdentifiers = library.stream()
        .map(definition -> definition.identifier)
        .collect(toSet());
    return library.stream()
        .flatMap(definition -> walk(definition.body)
            .flatMap(filter(Identifier.class))
            .filter(identifier -> !definedIdentifiers.contains(identifier))
            .map(identifier -> functionNotDefined(definition.identifier, identifier)))
        .toList();
  }

  private static List<DuplicatedFunction> findDuplicatedFunction(
      List<Definition> library) {
    var histogram = library.stream()
        .map(definition -> definition.identifier)
        .collect(groupingBy(identity(), counting()));
    return histogram.entrySet().stream()
        .filter(entry -> entry.getValue() > 1)
        .map(entry -> entry.getKey())
        .map(DuplicatedFunction::duplicatedFunction)
        .toList();
  }
}
