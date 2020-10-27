package com.mikosik.stork.tool.compile;

import static com.mikosik.stork.tool.compile.Modeler.modelDefinition;
import static com.mikosik.stork.tool.compile.Modeler.modelExpression;
import static com.mikosik.stork.tool.compile.Modeler.modelModule;
import static com.mikosik.stork.tool.compile.Parser.parse;

import com.mikosik.stork.data.model.Definition;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Module;

public class Default {
  public static Module compileModule(String source) {
    return modelModule(parse(source));
  }

  public static Definition compileDefinition(String source) {
    return modelDefinition(parse(source));
  }

  public static Expression compileExpression(String source) {
    return modelExpression(parse(source));
  }
}
