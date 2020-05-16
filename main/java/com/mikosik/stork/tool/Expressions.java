package com.mikosik.stork.tool;

import static com.mikosik.stork.common.Throwables.fail;
import static com.mikosik.stork.data.model.Application.application;
import static com.mikosik.stork.data.model.Switch.switchOn;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Expression;

public class Expressions {
  /**
   * Stack trace of execution contains chain of expressions, all applications except youngest on
   * stack. Runner, while descending, adds smaller and smaller parts of top expression onto stack.
   * Depending on application details, algorithm descends into function or argument of application.
   * When ascending, computed child expression needs to be injected into parent expression based on
   * path runner descended. This method does the injection.
   */
  public static Expression ascend(Expression child, Expression parent) {
    return switchOn(parent)
        .ifApplication(application -> switchOn(application.function)
            .ifVerb(verb -> application(application.function, child))
            .elseReturn(() -> application(child, application.argument)))
        .elseFail();
  }

  public static Expression ascend(Chain<Expression> stack) {
    return stack.visit(
        (head, tail) -> ascend(head, tail),
        () -> fail("empty stack"));
  }

  private static Expression ascend(Expression expression, Chain<Expression> stack) {
    for (Expression frame : stack) {
      expression = ascend(expression, frame);
    }
    return expression;
  }
}
