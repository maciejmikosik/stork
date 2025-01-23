package com.mikosik.stork.compile.link;

import static com.mikosik.stork.common.Collections.filter;
import static com.mikosik.stork.common.Sequence.flatten;
import static com.mikosik.stork.common.Sequence.toSequence;
import static com.mikosik.stork.model.change.Changes.walk;
import static com.mikosik.stork.problem.ProblemException.report;
import static com.mikosik.stork.problem.compile.link.UndefinedImport.undefinedImport;
import static com.mikosik.stork.problem.compile.link.UndefinedVariable.undefinedVariable;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

import com.mikosik.stork.common.Sequence;
import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Library;
import com.mikosik.stork.model.Variable;
import com.mikosik.stork.problem.compile.link.DuplicatedDefinition;
import com.mikosik.stork.problem.compile.link.UndefinedImport;
import com.mikosik.stork.problem.compile.link.UndefinedVariable;

public class VerifyLibrary {
  public static Library verify(Library library) {
    report(flatten(
        undefinedVariables(library),
        undefinedIdentifiers(library),
        duplicatedDefinitions(library)));
    return library;
  }

  private static Sequence<UndefinedVariable> undefinedVariables(Library library) {
    return library.definitions.stream()
        .flatMap(definition -> walk(definition.body)
            .flatMap(filter(Variable.class))
            .map(variable -> undefinedVariable(definition, variable)))
        .collect(toSequence());
  }

  private static Sequence<UndefinedImport> undefinedIdentifiers(Library library) {
    var definedIdentifiers = library.definitions.stream()
        .map(definition -> definition.identifier)
        .collect(toSet());
    return library.definitions.stream()
        .flatMap(definition -> walk(definition.body)
            .flatMap(filter(Identifier.class))
            .filter(identifier -> !definedIdentifiers.contains(identifier))
            .map(identifier -> undefinedImport(definition, identifier)))
        .collect(toSequence());
  }

  private static Sequence<DuplicatedDefinition> duplicatedDefinitions(Library library) {
    var histogram = library.definitions.stream()
        .map(definition -> definition.identifier)
        .collect(groupingBy(identity(), counting()));
    return histogram.entrySet().stream()
        .filter(entry -> entry.getValue() > 1)
        .map(entry -> entry.getKey())
        .map(DuplicatedDefinition::duplicatedDefinition)
        .collect(toSequence());
  }
}
