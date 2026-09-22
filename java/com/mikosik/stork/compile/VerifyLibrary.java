package com.mikosik.stork.compile;

import static com.mikosik.stork.common.Collections.filter;
import static com.mikosik.stork.common.ImmutableList.join;
import static com.mikosik.stork.compile.err.Problems.duplicatedFunction;
import static com.mikosik.stork.compile.err.Problems.undefinedFunction;
import static com.mikosik.stork.model.exp.Changes.walk;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

import java.util.List;

import com.mikosik.stork.compile.err.Problem;
import com.mikosik.stork.compile.err.Problems;
import com.mikosik.stork.model.exp.Definition;
import com.mikosik.stork.model.exp.Identifier;
import com.mikosik.stork.model.exp.Variable;

public class VerifyLibrary {
  public static List<Problem> findLinkingProblems(List<Definition> library) {
    return join(
        findUnboundVariable(library),
        findUndefinedFunction(library),
        findDuplicatedFunction(library));
  }

  private static List<Problem> findUnboundVariable(List<Definition> library) {
    return library.stream()
        .flatMap(definition -> walk(definition.body)
            .flatMap(filter(Variable.class))
            .map(variable -> Problems.unboundVariable()
                .location(definition.identifier)
                .object(variable)
                .build()))
        .toList();
  }

  private static List<Problem> findUndefinedFunction(List<Definition> library) {
    var definedIdentifiers = library.stream()
        .map(definition -> definition.identifier)
        .collect(toSet());
    return library.stream()
        .flatMap(definition -> walk(definition.body)
            .flatMap(filter(Identifier.class))
            .filter(identifier -> !definedIdentifiers.contains(identifier))
            .map(identifier -> undefinedFunction()
                .location(definition.identifier)
                .object(identifier)
                .build()))
        .toList();
  }

  private static List<Problem> findDuplicatedFunction(List<Definition> library) {
    var histogram = library.stream()
        .map(definition -> definition.identifier)
        .collect(groupingBy(identity(), counting()));
    return histogram.entrySet().stream()
        .filter(entry -> entry.getValue() > 1)
        .map(entry -> entry.getKey())
        .map(function -> duplicatedFunction()
            .location(function.namespace)
            .object(function.variable)
            .build())
        .toList();
  }
}
