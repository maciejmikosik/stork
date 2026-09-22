package com.mikosik.stork.problem.compute;

import static java.util.Objects.deepEquals;
import static java.util.Objects.hash;

import com.mikosik.stork.model.exp.Identifier;

public class FunctionMissing extends CannotCompute {
  public final Identifier function;

  private FunctionMissing(Identifier function) {
    this.function = function;
  }

  public static FunctionMissing functionMissing(Identifier function) {
    return new FunctionMissing(function);
  }

  public boolean equals(Object that) {
    return that instanceof FunctionMissing functionMissing
        && equals(functionMissing);
  }

  private boolean equals(FunctionMissing that) {
    return deepEquals(this.function, that.function);
  }

  public int hashCode() {
    return hash(function);
  }
}
