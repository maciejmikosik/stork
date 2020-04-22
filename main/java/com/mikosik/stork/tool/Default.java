package com.mikosik.stork.tool;

import static com.mikosik.stork.common.Chains.chainFrom;
import static com.mikosik.stork.data.model.Library.library;
import static com.mikosik.stork.tool.Modeler.modelDefinition;
import static com.mikosik.stork.tool.Modeler.modelExpression;
import static com.mikosik.stork.tool.Parser.parse;
import static com.mikosik.stork.tool.run.ExhaustedRunner.exhausted;
import static com.mikosik.stork.tool.run.Stepper.stepper;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

import java.util.List;

import com.mikosik.stork.common.Strings;
import com.mikosik.stork.data.model.Definition;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Library;
import com.mikosik.stork.tool.run.Runner;

public class Default {
  // TODO create library modeler that uses separator
  public static Library compileLibrary(String source) {
    List<Definition> definitions = stream(source.split("\n\n"))
        .map(String::trim)
        .filter(Strings::isNotEmpty)
        .map(Default::compileDefinition)
        .collect(toList());
    return library(chainFrom(definitions));
  }

  public static Definition compileDefinition(String source) {
    return modelDefinition(parse(source));
  }

  public static Expression compileExpression(String source) {
    return modelExpression(parse(source));
  }

  public static Runner defaultRunner(Binary binary) {
    return exhausted(stepper(binary));
  }
}
