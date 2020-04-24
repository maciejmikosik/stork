package com.mikosik.stork.data.model;

import static java.lang.String.format;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class Switch {
  private final Expression expression;
  private final Optional<Object> result;

  public Switch(Expression expression, Optional<Object> result) {
    this.expression = expression;
    this.result = result;
  }

  public static Switch switchOn(Expression expression) {
    return new Switch(expression, Optional.empty());
  }

  public Switch ifVariable(Function<Variable, ?> handler) {
    return ifType(Variable.class, handler);
  }

  public Switch ifNoun(Function<Noun, ?> handler) {
    return ifType(Noun.class, handler);
  }

  public Switch ifCore(Function<Core, ?> handler) {
    return ifType(Core.class, handler);
  }

  public Switch ifApplication(Function<Application, ?> handler) {
    return ifType(Application.class, handler);
  }

  public Switch ifLambda(Function<Lambda, ?> handler) {
    return ifType(Lambda.class, handler);
  }

  public Switch ifParameter(Function<Parameter, ?> handler) {
    return ifType(Parameter.class, handler);
  }

  public Switch ifRunning(Function<Running, ?> handler) {
    return ifType(Running.class, handler);
  }

  private <E> Switch ifType(Class<E> type, Function<E, ?> handler) {
    return !result.isPresent() && type.isInstance(expression)
        ? new Switch(expression, Optional.of(handler.apply((E) expression)))
        : this;
  }

  public <R> R elseReturn(Supplier<R> handler) {
    return (R) result.orElseGet(handler);
  }

  public <R> R elseFail() {
    return (R) result.orElseThrow(() -> new RuntimeException(
        format("cannot handle expression '%s' of type %s",
            expression,
            expression.getClass().getName())));
  }
}
