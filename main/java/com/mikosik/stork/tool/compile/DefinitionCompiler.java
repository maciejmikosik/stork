package com.mikosik.stork.tool.compile;

import static com.mikosik.stork.data.model.Definition.definition;

import com.mikosik.stork.common.Input;
import com.mikosik.stork.data.model.Definition;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Variable;

public class DefinitionCompiler implements Compiler<Definition> {
  public Compiler<Variable> variable;
  public Compiler<Void> whitespace;
  public Compiler<Expression> body;

  public Definition compile(Input input) {
    Variable name = variable.compile(input);
    whitespace.compile(input);
    return definition(name, body.compile(input));
  }
}
