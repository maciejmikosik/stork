package com.mikosik.stork.testing;

import static com.mikosik.stork.tool.common.Scope.GLOBAL;
import static com.mikosik.stork.tool.decompile.Decompiler.decompiler;
import static java.lang.String.format;
import static java.util.Objects.deepEquals;

import org.quackery.report.AssertException;

import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.tool.decompile.Decompiler;

public class Assertions {
  public static boolean areEqual(Expression first, Expression second) {
    Decompiler decompiler = decompiler(GLOBAL);
    return deepEquals(
        decompiler.decompile(first),
        decompiler.decompile(second));
  }

  public static AssertException failEqual(
      Expression question,
      Expression answer,
      Expression questionComputed,
      Expression answerComputed) {
    Decompiler decompiler = decompiler(GLOBAL);
    return new AssertException(format(""
        + "expected that expression\n"
        + "  %s\n"
        + "is equal to\n"
        + "  %s\n"
        + "which computes to\n"
        + "  %s\n"
        + "but expression computed to\n"
        + "  %s\n",
        decompiler.decompile(question),
        decompiler.decompile(answer),
        decompiler.decompile(answerComputed),
        decompiler.decompile(questionComputed)));
  }
}
