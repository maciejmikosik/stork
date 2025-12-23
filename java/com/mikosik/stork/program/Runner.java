package com.mikosik.stork.program;

import static com.mikosik.stork.compute.ApplicationComputer.applicationComputer;
import static com.mikosik.stork.compute.CachingComputer.caching;
import static com.mikosik.stork.compute.Computation.computation;
import static com.mikosik.stork.compute.IntegerComputer.integerComputer;
import static com.mikosik.stork.compute.InterruptibleComputer.interruptible;
import static com.mikosik.stork.compute.LibraryComputer.computer;
import static com.mikosik.stork.compute.OperatorComputer.operatorComputer;
import static com.mikosik.stork.compute.Router.router;
import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.program.Stdin.stdin;
import static com.mikosik.stork.program.Stdout.CLOSE;
import static com.mikosik.stork.program.Stdout.writeStreamTo;

import com.mikosik.stork.compute.Computation;
import com.mikosik.stork.compute.Stack.Empty;
import com.mikosik.stork.model.Application;
import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Integer;
import com.mikosik.stork.model.Operator;

public class Runner {
  private Runner() {}

  public static Runner runner() {
    return new Runner();
  }

  public void run(Task task) {
    var computer = interruptible(caching(router()
        .route(Identifier.class, computer(task.program.library))
        .route(Operator.class, operatorComputer())
        .route(Application.class, applicationComputer())
        .route(Integer.class, integerComputer())));

    var computation = computation(
        application(
            writeStreamTo(task.terminal.output),
            application(
                task.program.main,
                stdin(task.terminal.input))));

    while (!isDone(computation)) {
      computation = computer.compute(computation);
    }
    task.terminal.output.flush();
  }

  private boolean isDone(Computation computation) {
    return computation.expression == CLOSE
        && computation.stack instanceof Empty;
  }
}
