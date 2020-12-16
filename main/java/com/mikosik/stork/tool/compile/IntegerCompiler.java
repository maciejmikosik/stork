package com.mikosik.stork.tool.compile;

import static com.mikosik.stork.data.model.Integer.integer;

import java.math.BigInteger;

import com.mikosik.stork.common.PeekingInput;
import com.mikosik.stork.data.model.Expression;

public class IntegerCompiler implements Compiler<Expression> {
  public Compiler<String> alphanumeric;

  public Expression compile(PeekingInput input) {
    return integer(new BigInteger(alphanumeric.compile(input)));
  }
}
