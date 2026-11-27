package com.mikosik.stork.compile;

import static com.mikosik.stork.common.col.Collections.filter;
import static com.mikosik.stork.common.col.ImmutableList.join;
import static com.mikosik.stork.compile.err.Miscompilations.duplicatedFunction;
import static com.mikosik.stork.compile.err.Miscompilations.unboundVariable;
import static com.mikosik.stork.compile.err.Miscompilations.undefinedFunction;
import static com.mikosik.stork.model.exp.Changes.walk;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

import java.util.List;

import com.mikosik.stork.compile.err.Miscompilation;
import com.mikosik.stork.model.exp.Definition;
import com.mikosik.stork.model.exp.Reference;
import com.mikosik.stork.model.exp.Variable;

public class VerifyLibrary {
  public static List<Miscompilation> findLinkingMiscompilations(List<Definition> library) {
    return join(
        findUnboundVariable(library),
        findUndefinedFunction(library),
        findDuplicatedFunction(library));
  }

  private static List<Miscompilation> findUnboundVariable(List<Definition> library) {
    return library.stream()
        .flatMap(definition -> walk(definition.body)
            .flatMap(filter(Variable.class))
            .map(variable -> unboundVariable()
                .in(definition.reference)
                .set("variable", variable)
                .build()))
        .toList();
  }

  private static List<Miscompilation> findUndefinedFunction(List<Definition> library) {
    var definedReferences = library.stream()
        .map(definition -> definition.reference)
        .collect(toSet());
    return library.stream()
        .flatMap(definition -> walk(definition.body)
            .flatMap(filter(Reference.class))
            .filter(reference -> !definedReferences.contains(reference))
            .map(reference -> undefinedFunction()
                .in(definition.reference)
                .set("function", reference)
                .build()))
        .toList();
  }

  private static List<Miscompilation> findDuplicatedFunction(List<Definition> library) {
    var histogram = library.stream()
        .map(definition -> definition.reference)
        .collect(groupingBy(identity(), counting()));
    return histogram.entrySet().stream()
        .filter(entry -> entry.getValue() > 1)
        .map(entry -> entry.getKey())
        .map(function -> duplicatedFunction()
            .in(function.namespace)
            .set("function", function.variable)
            .build())
        .toList();
  }
}
