package com.mikosik.stork.tool;

import static com.mikosik.stork.common.Throwables.fail;
import static com.mikosik.stork.data.model.Application.application;
import static com.mikosik.stork.data.model.Lambda.lambda;
import static com.mikosik.stork.data.model.Switch.switchOn;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Application;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Lambda;
import com.mikosik.stork.data.model.Parameter;

public class Expressions {
  /**
   * When running {@link Application} that applies {@link Lambda} on given argument
   * {@link Expression} it is required to replace each occurrence of lambda's parameter in lambda's
   * body by given argument. This method performs this.
   *
   * @param expression
   *          expression to perform substitution on (e.g. lambda's body)
   * @param parameter
   *          parameter in lambda's body to be substituted by argument
   * @param argument
   *          expression to replace parameter
   * @return expression after performing substitution
   */
  public static Expression substitute(
      Expression expression,
      Parameter parameter,
      Expression argument) {
    return switchOn(expression)
        .ifApplication(application -> application(
            substitute(application.function, parameter, argument),
            substitute(application.argument, parameter, argument)))
        .ifVariable(variable -> variable)
        .ifLambda(lambda -> lambda.parameter == parameter
            ? expression // TODO test shadowing
            : lambda(
                lambda.parameter,
                substitute(lambda.body, parameter, argument)))
        .ifParameter(foundParameter -> foundParameter == parameter
            ? argument
            : foundParameter)
        .ifNoun(noun -> noun)
        .elseFail();
  }

  public static Expression substitute(Lambda lambda, Expression argument) {
    return substitute(lambda.body, lambda.parameter, argument);
  }

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
            .ifCore(core -> application(application.function, child))
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
