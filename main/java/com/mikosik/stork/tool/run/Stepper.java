package com.mikosik.stork.tool.run;

import static com.mikosik.stork.common.Chain.add;
import static com.mikosik.stork.common.Throwables.fail;
import static com.mikosik.stork.common.Throwables.throwing;
import static com.mikosik.stork.data.model.Running.running;
import static com.mikosik.stork.data.model.Switch.switchOn;
import static com.mikosik.stork.tool.Expressions.ascend;
import static com.mikosik.stork.tool.Expressions.substitute;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Running;
import com.mikosik.stork.tool.Binary;

public class Stepper implements Runner {
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
        (head, tail) -> run(head, tail),
        () -> throwing(new RuntimeException()));
  }

  private Expression run(Expression expression, Chain<Expression> stack) {
    return switchOn(expression)
        .ifVariable(variable -> running(add(binary.table.get(variable.name), stack)))
        .ifApplication(application -> switchOn(application.function)
            .ifCore(core -> switchOn(application.argument)
                .ifVariable(argument -> running(add(argument, add(expression, stack))))
                .ifApplication(argument -> running(add(argument, add(expression, stack))))
                .elseReturn(() -> running(add(core.apply(application.argument), stack))))
            .ifVariable(function -> running(add(function, add(expression, stack))))
            .ifApplication(function -> running(add(function, add(expression, stack))))
            .ifPrimitive(primitive -> fail("cannot apply primitive " + primitive))
            .ifLambda(lambda -> running(add(substitute(lambda, application.argument), stack)))
            .elseFail())
        .elseReturn(() -> stack.visit(
            (head, tail) -> running(add(ascend(expression, head), tail)),
            () -> expression));
  }
}
