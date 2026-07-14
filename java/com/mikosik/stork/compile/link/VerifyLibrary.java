package com.mikosik.stork.compile.link;

import static com.mikosik.stork.common.Collections.filter;
import static com.mikosik.stork.common.ImmutableList.join;
import static com.mikosik.stork.model.change.Changes.walk;
import static com.mikosik.stork.problem.compile.link.UndefinedFunction.undefinedFunction;
import static com.mikosik.stork.problem.compile.link.UnboundVariable.unboundVariable;
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
import com.mikosik.stork.problem.compile.link.UndefinedFunction;
import com.mikosik.stork.problem.compile.link.UnboundVariable;

public class VerifyLibrary {
  public static List<CannotLink> findLinkingProblems(List<Definition> library) {
    return join(
        findUnboundVariable(library),
        findUndefinedFunction(library),
        findDuplicatedFunction(library));
  }

  private static List<UnboundVariable> findUnboundVariable(
      List<Definition> library) {
    return library.stream()
        .flatMap(definition -> walk(definition.body)
            .flatMap(filter(Variable.class))
            .map(variable -> unboundVariable(definition.identifier, variable)))
        .toList();
  }

  private static List<UndefinedFunction> findUndefinedFunction(
      List<Definition> library) {
    var definedIdentifiers = library.stream()
        .map(definition -> definition.identifier)
        .collect(toSet());
    return library.stream()
        .flatMap(definition -> walk(definition.body)
            .flatMap(filter(Identifier.class))
            .filter(identifier -> !definedIdentifiers.contains(identifier))
            .map(identifier -> undefinedFunction(definition.identifier, identifier)))
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
