package com.mikosik.stork.compute.err;

import static com.mikosik.stork.common.col.ImmutableList.join;
import static com.mikosik.stork.common.col.ImmutableList.single;
import static com.mikosik.stork.common.text.Outline.outline;
import static java.lang.String.join;
import static java.util.Objects.deepEquals;
import static java.util.Objects.hash;

import com.mikosik.stork.common.text.Outline;
import com.mikosik.stork.model.exp.Reference;

public class FunctionMissing extends CannotCompute {
  public final Reference function;

  private FunctionMissing(Reference function) {
    this.function = function;
  }

  public static FunctionMissing functionMissing(Reference function) {
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

  public Outline toOutline() {
    return outline("FunctionMissing")
        .nest(format(function));
  }

  // TODO create utils for formatting and parsing
  private String format(Reference reference) {
    return join("/", join(
        reference.namespace.components,
        single(reference.variable.name)));
  }
}
