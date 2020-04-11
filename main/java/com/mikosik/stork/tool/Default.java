package com.mikosik.stork.tool;

import static com.mikosik.stork.tool.Modeler.modelDefinition;
import static com.mikosik.stork.tool.Modeler.modelExpression;
import static com.mikosik.stork.tool.Parser.parse;

import com.mikosik.stork.data.model.Definition;
import com.mikosik.stork.data.model.Expression;

public class Default {
  public static Definition compileDefinition(String source) {
    return modelDefinition(parse(source));
  }

  public static Expression compileExpression(String source) {
    return modelExpression(parse(source));
  }
}
