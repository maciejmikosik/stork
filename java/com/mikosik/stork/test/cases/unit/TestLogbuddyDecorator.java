package com.mikosik.stork.test.cases.unit;

import static com.mikosik.stork.common.io.InputOutput.delete;
import static com.mikosik.stork.compute.ApplicationComputer.applicationComputer;
import static com.mikosik.stork.compute.CachingComputer.caching;
import static com.mikosik.stork.compute.IntegerComputer.integerComputer;
import static com.mikosik.stork.compute.InterruptibleComputer.interruptible;
import static com.mikosik.stork.compute.LibraryComputer.computer;
import static com.mikosik.stork.compute.OperatorComputer.operatorComputer;
import static com.mikosik.stork.compute.Router.router;
import static com.mikosik.stork.debug.Debug.configuredDecorator;
import static com.mikosik.stork.model.Library.libraryOf;
import static java.nio.file.Files.createTempFile;
import static org.quackery.Case.newCase;

import org.quackery.Test;

import com.mikosik.stork.compute.Computer;
import com.mikosik.stork.model.Application;
import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Integer;
import com.mikosik.stork.model.Operator;

public class TestLogbuddyDecorator {
  public static Test testLogbuddyDecorator() {
    return newCase("logbuddy can decorate computers", () -> {
      var logFile = createTempFile("stork_test_", "");
      configuredDecorator(logFile).decorate(buildComputer());
      delete(logFile);
    });
  }

  private static Computer buildComputer() {
    return interruptible(caching(router()
        .route(Identifier.class, computer(libraryOf()))
        .route(Operator.class, operatorComputer())
        .route(Application.class, applicationComputer())
        .route(Integer.class, integerComputer())));
  }
}
