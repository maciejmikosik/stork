package com.mikosik.stork.tool.compile;

import static com.mikosik.stork.data.model.Variable.variable;

import com.mikosik.stork.common.PeekingInput;
import com.mikosik.stork.data.model.Variable;

public class VariableCompiler implements Compiler<Variable> {
  public Compiler<String> alphanumeric;

  public Variable compile(PeekingInput input) {
    return variable(alphanumeric.compile(input));
  }
}
