package com.mikosik.stork.tool.run;

import static com.mikosik.stork.common.Throwables.throwing;
import static com.mikosik.stork.data.model.Running.running;
import static com.mikosik.stork.data.model.Switch.switchOn;

import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Running;
import com.mikosik.stork.tool.Binary;

public class Stepper implements Runner {
  @SuppressWarnings("unused")
  private final Binary binary;

  private Stepper(Binary binary) {
    this.binary = binary;
  }

  public static Runner stepper(Binary binary) {
    return new Stepper(binary);
  }

  public Expression run(Expression expression) {
    return switchOn(expression)
        .ifRunning(running -> run(running))
        .elseReturn(() -> running(expression));
  }

  private Expression run(Running running) {
    return running.stack.visit(
        (head, tail) -> head,
        () -> throwing(new RuntimeException()));
  }
}
