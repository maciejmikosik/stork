package com.mikosik.stork.compute;

import static com.mikosik.stork.compute.Computation.computation;

import com.mikosik.stork.model.Application;

public class ApplicationComputer extends TypedComputer<Application> {
  private ApplicationComputer() {
    super(Application.class);
  }

  public static Computer applicationComputer() {
    return new ApplicationComputer();
  }

  public Computation compute(Application application, Stack stack) {
    return computation(
        application.function,
        stack.pushArgument(application.argument));
  }
}
