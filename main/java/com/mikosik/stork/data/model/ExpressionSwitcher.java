package com.mikosik.stork.data.model;

import static java.lang.String.format;

import java.util.function.Function;

public class ExpressionSwitcher<R> {
  private Function<Variable, R> variableOption;
  private Function<Primitive, R> primitiveOption;
  private Function<Core, R> coreOption;
  private Function<Application, R> applicationOption;
  private Function<Lambda, R> lambdaOption;
  private Function<Parameter, R> parameterOption;

  private ExpressionSwitcher() {}

  public static <R> ExpressionSwitcher<R> expressionSwitcherReturning(Class<R> resultType) {
    return new ExpressionSwitcher<R>();
  }

  public ExpressionSwitcher<R> ifVariable(Function<Variable, R> option) {
    this.variableOption = option;
    return this;
  }

  public ExpressionSwitcher<R> ifPrimitive(Function<Primitive, R> option) {
    this.primitiveOption = option;
    return this;
  }

  public ExpressionSwitcher<R> ifCore(Function<Core, R> option) {
    this.coreOption = option;
    return this;
  }

  public ExpressionSwitcher<R> ifApplication(Function<Application, R> option) {
    this.applicationOption = option;
    return this;
  }

  public ExpressionSwitcher<R> ifLambda(Function<Lambda, R> option) {
    this.lambdaOption = option;
    return this;
  }

  public ExpressionSwitcher<R> ifParameter(Function<Parameter, R> option) {
    this.parameterOption = option;
    return this;
  }

  public R apply(Expression expression) {
    if (expression instanceof Variable) {
      return variableOption.apply((Variable) expression);
    } else if (expression instanceof Primitive) {
      return primitiveOption.apply((Primitive) expression);
    } else if (expression instanceof Core) {
      return coreOption.apply((Core) expression);
    } else if (expression instanceof Application) {
      return applicationOption.apply((Application) expression);
    } else if (expression instanceof Lambda) {
      return lambdaOption.apply((Lambda) expression);
    } else if (expression instanceof Parameter) {
      return parameterOption.apply((Parameter) expression);
    } else {
      throw new RuntimeException(format("unhandled expression: %s", expression));
    }
  }
}
