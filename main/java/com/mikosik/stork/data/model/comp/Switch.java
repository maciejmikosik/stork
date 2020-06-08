package com.mikosik.stork.data.model.comp;

import static java.lang.String.format;

import java.util.Optional;
import java.util.function.Supplier;

public class Switch {
  private final Stack stack;
  private final Optional<Object> result;

  public Switch(Stack stack, Optional<Object> result) {
    this.stack = stack;
    this.result = result;
  }

  public static Switch switchOn(Stack stack) {
    return new Switch(stack, Optional.empty());
  }

  public Switch ifEmpty(Handler<Empty> handler) {
    return ifType(Empty.class, handler);
  }

  public Switch ifFunction(Handler<Function> handler) {
    return ifType(Function.class, handler);
  }

  public Switch ifArgument(Handler<Argument> handler) {
    return ifType(Argument.class, handler);
  }

  private <E> Switch ifType(Class<E> type, Handler<E> handler) {
    return !result.isPresent() && type.isInstance(stack)
        ? new Switch(stack, Optional.of(handler.handle((E) stack)))
        : this;
  }

  public <R> R elseReturn(Supplier<R> handler) {
    return (R) result.orElseGet(handler);
  }

  public <R> R elseFail() {
    return (R) result.orElseThrow(() -> new RuntimeException(
        format("cannot handle stack '%s' of type %s",
            stack,
            stack.getClass().getName())));
  }

  public static interface Handler<E> {
    Object handle(E element);
  }
}
