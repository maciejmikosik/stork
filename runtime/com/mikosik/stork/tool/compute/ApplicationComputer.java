package com.mikosik.stork.tool.compute;

import static com.mikosik.stork.model.Computation.computation;

import com.mikosik.stork.model.Application;
import com.mikosik.stork.model.Computation;
import com.mikosik.stork.model.Stack;

public class ApplicationComputer implements Computer {
  private ApplicationComputer() {}

  public static Computer applicationComputer() {
    return new ApplicationComputer();
  }

  public Computation compute(Computation computation) {
    return computation.expression instanceof Application
        ? compute((Application) computation.expression, computation.stack)
        : computation;
  }

  private static Computation compute(Application application, Stack stack) {
    return computation(
        application.function,
        stack.pushArgument(application.argument));
  }
}
