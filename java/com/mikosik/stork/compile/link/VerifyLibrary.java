package com.mikosik.stork.compile.link;

import static com.mikosik.stork.common.Collections.filter;
import static com.mikosik.stork.common.ImmutableList.cast;
import static com.mikosik.stork.common.ImmutableList.join;
import static com.mikosik.stork.model.change.Changes.walk;
import static com.mikosik.stork.problem.compile.InNamespace.in;
import static com.mikosik.stork.problem.compile.link.DuplicatedFunction.duplicatedFunction;
import static com.mikosik.stork.problem.compile.link.UnboundVariable.unboundVariable;
import static com.mikosik.stork.problem.compile.link.UndefinedFunction.undefinedFunction;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

import java.util.List;

import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Variable;
import com.mikosik.stork.problem.compile.CannotCompile;
import com.mikosik.stork.problem.compile.link.UnboundVariable;
import com.mikosik.stork.problem.compile.link.UndefinedFunction;

public class VerifyLibrary {
  public static List<CannotCompile> findLinkingProblems(List<Definition> library) {
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

  private static List<CannotCompile> findDuplicatedFunction(
      List<Definition> library) {
    var histogram = library.stream()
        .map(definition -> definition.identifier)
        .collect(groupingBy(identity(), counting()));
    return cast(histogram.entrySet().stream()
        .filter(entry -> entry.getValue() > 1)
        .map(entry -> entry.getKey())
        .map(function -> in(function.namespace,
            duplicatedFunction(function.variable)))
        .toList());
  }
}
