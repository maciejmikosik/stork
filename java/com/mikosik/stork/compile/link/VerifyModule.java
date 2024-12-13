package com.mikosik.stork.compile.link;

import static com.mikosik.stork.common.Collections.filter;
import static com.mikosik.stork.common.Collections.flatten;
import static com.mikosik.stork.model.change.Changes.walk;
import static com.mikosik.stork.problem.ProblemException.report;
import static com.mikosik.stork.problem.compile.link.UndefinedImport.undefinedImport;
import static com.mikosik.stork.problem.compile.link.UndefinedVariable.undefinedVariable;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

import java.util.List;

import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.model.Variable;
import com.mikosik.stork.problem.compile.link.CannotLink;
import com.mikosik.stork.problem.compile.link.DuplicatedDefinition;

public class VerifyModule {
  public static Module verify(Module module) {
    report(flatten(List.of(
        undefinedVariables(module),
        undefinedIdentifiers(module),
        duplicatedDefinitions(module))));
    return module;
  }

  private static List<CannotLink> undefinedVariables(Module module) {
    return module.definitions.stream()
        .flatMap(definition -> walk(definition.body)
            .flatMap(filter(Variable.class))
            .map(variable -> undefinedVariable(definition, variable)))
        .map(problem -> (CannotLink) problem)
        .toList();
  }

  private static List<CannotLink> undefinedIdentifiers(Module module) {
    var definedIdentifiers = module.definitions.stream()
        .map(definition -> definition.identifier)
        .collect(toSet());
    return module.definitions.stream()
        .flatMap(definition -> walk(definition.body)
            .flatMap(filter(Identifier.class))
            .filter(identifier -> !definedIdentifiers.contains(identifier))
            .map(identifier -> undefinedImport(definition, identifier)))
        .map(problem -> (CannotLink) problem)
        .toList();
  }

  private static List<CannotLink> duplicatedDefinitions(Module module) {
    var histogram = module.definitions.stream()
        .map(definition -> definition.identifier)
        .collect(groupingBy(identity(), counting()));
    return histogram.entrySet().stream()
        .filter(entry -> entry.getValue() > 1)
        .map(entry -> entry.getKey())
        .map(DuplicatedDefinition::duplicatedDefinition)
        .map(problem -> (CannotLink) problem)
        .toList();
  }
}
