package com.mikosik.stork.tool.compile;

import static com.mikosik.stork.model.Integer.integer;

import java.math.BigInteger;

import com.mikosik.stork.common.Input;
import com.mikosik.stork.model.Expression;

public class IntegerCompiler implements Compiler<Expression> {
  public Compiler<String> alphanumeric;

  public Expression compile(Input input) {
    return integer(new BigInteger(alphanumeric.compile(input)));
  }
}
