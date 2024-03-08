package com.mikosik.stork.build.link;

import static com.mikosik.stork.common.Collections.flatten;
import static com.mikosik.stork.common.Collections.instanceOf;
import static com.mikosik.stork.common.Sequence.sequence;
import static com.mikosik.stork.common.Sequence.toSequence;
import static com.mikosik.stork.model.change.Changes.walk;
import static com.mikosik.stork.problem.ProblemException.report;
import static com.mikosik.stork.problem.build.link.UndefinedImport.undefinedImport;
import static com.mikosik.stork.problem.build.link.UndefinedVariable.undefinedVariable;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.List;

import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.model.Variable;
import com.mikosik.stork.problem.build.link.DuplicatedDefinition;
import com.mikosik.stork.problem.build.link.UndefinedImport;
import com.mikosik.stork.problem.build.link.UndefinedVariable;

public class VerifyModule {
  public static Module verify(Module module) {
    report(flatten(sequence(
        undefinedVariables(module),
        undefinedIdentifiers(module),
        duplicatedDefinitions(module))));
    return module;
  }

  private static List<UndefinedVariable> undefinedVariables(Module module) {
    return module.definitions.stream()
        .flatMap(definition -> walk(definition.body)
            .flatMap(instanceOf(Variable.class))
            .map(variable -> undefinedVariable(definition, variable)))
        .collect(toList());
  }

  private static List<UndefinedImport> undefinedIdentifiers(Module module) {
    var definedIdentifiers = module.definitions.stream()
        .map(definition -> definition.identifier)
        .collect(toSet());
    return module.definitions.stream()
        .flatMap(definition -> walk(definition.body)
            .flatMap(instanceOf(Identifier.class))
            .filter(identifier -> !definedIdentifiers.contains(identifier))
            .map(identifier -> undefinedImport(definition, identifier)))
        .collect(toList());
  }

  private static List<DuplicatedDefinition> duplicatedDefinitions(Module module) {
    var histogram = module.definitions.stream()
        .map(definition -> definition.identifier)
        .collect(groupingBy(identity(), counting()));
    return histogram.entrySet().stream()
        .filter(entry -> entry.getValue() > 1)
        .map(entry -> entry.getKey())
        .map(DuplicatedDefinition::duplicatedDefinition)
        .collect(toSequence());
  }
}
