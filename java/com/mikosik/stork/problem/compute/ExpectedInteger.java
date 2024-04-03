package com.mikosik.stork.problem.compute;

import static com.mikosik.stork.common.io.Serializables.ascii;
import static com.mikosik.stork.debug.Decompiler.decompile;

import com.mikosik.stork.model.Expression;

public class ExpectedInteger implements CannotCompute {
  public final Expression expression;

  private ExpectedInteger(Expression expression) {
    this.expression = expression;
  }

  public static ExpectedInteger expectedInteger(Expression expression) {
    return new ExpectedInteger(expression);
  }

  public String description() {
    return """
          wrong type
            expected: integer
            but was : %s
        """.formatted(ascii(decompile(expression)));
  }
}
