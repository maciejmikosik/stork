package com.mikosik.stork.tool.compile;

import static com.mikosik.stork.model.Variable.variable;

import com.mikosik.stork.common.Input;
import com.mikosik.stork.model.Variable;

public class VariableCompiler implements Compiler<Variable> {
  public Compiler<String> alphanumeric;

  public Variable compile(Input input) {
    return variable(alphanumeric.compile(input));
  }
}
