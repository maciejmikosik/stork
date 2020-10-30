package com.mikosik.stork.testing;

import static com.mikosik.stork.data.model.comp.Computation.computation;
import static com.mikosik.stork.tool.common.Computations.abort;
import static com.mikosik.stork.tool.compile.Decompiler.decompiler;
import static com.mikosik.stork.tool.compile.Modeler.modelExpression;
import static com.mikosik.stork.tool.compile.Parser.parse;
import static java.lang.String.format;
import static java.util.Objects.deepEquals;

import org.quackery.report.AssertException;

import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.tool.compute.Computer;

public class Asserter {
  private final Computer computer;

  private Asserter(Computer computer) {
    this.computer = computer;
  }

  public static Asserter asserter(Computer computer) {
    return new Asserter(computer);
  }

  public void assertEqual(String question, String answer) {
    String computedQuestion = compute(question);
    String computedAnswer = compute(answer);

    if (!deepEquals(computedQuestion, computedAnswer)) {
      throw new AssertException(format(""
          + "expected that expression\n"
          + "  %s\n"
          + "is equal to\n"
          + "  %s\n"
          + "which computes to\n"
          + "  %s\n"
          + "but expression computed to\n"
          + "  %s\n",
          question,
          answer,
          computedAnswer,
          computedQuestion));
    }
  }

  private String compute(String code) {
    return decompiler().decompile(compute(compile(code)));
  }

  private Expression compile(String code) {
    return modelExpression(parse(code));
  }

  private Expression compute(Expression expression) {
    return abort(computer.compute(computation(expression)));
  }
}
