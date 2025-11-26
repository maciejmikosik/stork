package com.mikosik.stork.compile.link;

import static com.mikosik.stork.common.Collections.filter;
import static com.mikosik.stork.common.Sequence.flatten;
import static com.mikosik.stork.common.Sequence.toSequence;
import static com.mikosik.stork.model.change.Changes.walk;
import static com.mikosik.stork.problem.ProblemException.exception;
import static com.mikosik.stork.problem.compile.link.CannotLinkLibrary.cannotLinkLibrary;
import static com.mikosik.stork.problem.compile.link.FunctionNotDefined.functionNotDefined;
import static com.mikosik.stork.problem.compile.link.VariableCannotBeBound.variableCannotBeBound;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

import com.mikosik.stork.common.Sequence;
import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Library;
import com.mikosik.stork.model.Variable;
import com.mikosik.stork.problem.compile.link.FunctionDefinedMoreThanOnce;
import com.mikosik.stork.problem.compile.link.FunctionNotDefined;
import com.mikosik.stork.problem.compile.link.VariableCannotBeBound;

public class VerifyLibrary {
  public static Library verify(Library library) {
    var problems = flatten(
        findVariableCannotBeFound(library),
        findFunctionNotDefined(library),
        findFunctionDefinedMoreThanOnce(library));
    if (!problems.isEmpty()) {
      throw exception(cannotLinkLibrary(problems));
    }
    return library;
  }

  private static Sequence<VariableCannotBeBound> findVariableCannotBeFound(Library library) {
    return library.definitions.stream()
        .flatMap(definition -> walk(definition.body)
            .flatMap(filter(Variable.class))
            .map(variable -> variableCannotBeBound(definition.identifier, variable)))
        .collect(toSequence());
  }

  private static Sequence<FunctionNotDefined> findFunctionNotDefined(Library library) {
    var definedIdentifiers = library.definitions.stream()
        .map(definition -> definition.identifier)
        .collect(toSet());
    return library.definitions.stream()
        .flatMap(definition -> walk(definition.body)
            .flatMap(filter(Identifier.class))
            .filter(identifier -> !definedIdentifiers.contains(identifier))
            .map(identifier -> functionNotDefined(definition.identifier, identifier)))
        .collect(toSequence());
  }

  private static Sequence<FunctionDefinedMoreThanOnce> findFunctionDefinedMoreThanOnce(Library library) {
    var histogram = library.definitions.stream()
        .map(definition -> definition.identifier)
        .collect(groupingBy(identity(), counting()));
    return histogram.entrySet().stream()
        .filter(entry -> entry.getValue() > 1)
        .map(entry -> entry.getKey())
        .map(FunctionDefinedMoreThanOnce::functionDefinedMoreThanOnce)
        .collect(toSequence());
  }
}
