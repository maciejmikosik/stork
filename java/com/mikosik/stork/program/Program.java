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

import com.mikosik.stork.common.io.Input;
import com.mikosik.stork.common.io.Output;
import com.mikosik.stork.compute.Computation;
import com.mikosik.stork.compute.Computer;
import com.mikosik.stork.compute.Stack.Empty;
import com.mikosik.stork.model.Application;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Integer;
import com.mikosik.stork.model.Library;
import com.mikosik.stork.model.Operator;

public class Program {
  private final Expression main;
  private final Computer computer;

  private Program(Expression main, Computer computer) {
    this.main = main;
    this.computer = computer;
  }

  public static Program program(Identifier main, Library library) {
    return new Program(main, buildComputer(library));
  }

  private static Computer buildComputer(Library library) {
    return interruptible(caching(router()
        .route(Identifier.class, computer(library))
        .route(Operator.class, operatorComputer())
        .route(Application.class, applicationComputer())
        .route(Integer.class, integerComputer())));
  }

  public void run(Input input, Output output) {
    var computation = computation(application(
        writeStreamTo(output),
        application(main, stdin(input))));

    while (!isDone(computation)) {
      computation = computer.compute(computation);
    }
    output.flush();
  }

  private boolean isDone(Computation computation) {
    return computation.expression == CLOSE
        && computation.stack instanceof Empty;
  }
}
