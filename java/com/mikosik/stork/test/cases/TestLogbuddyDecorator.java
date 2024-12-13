package com.mikosik.stork.test.cases;

import static com.mikosik.stork.common.io.InputOutput.delete;
import static com.mikosik.stork.debug.Debug.configuredDecorator;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Module.moduleOf;
import static com.mikosik.stork.program.Program.program;
import static java.nio.file.Files.createTempFile;
import static org.quackery.Case.newCase;

import org.quackery.Test;

public class TestLogbuddyDecorator {
  public static Test testLogbuddyDecorator() {
    return newCase("logbuddy decorator can decorate program", () -> {
      var module = moduleOf();
      var logFile = createTempFile("stork_test_", "");
      configuredDecorator(logFile)
          .decorate(program(identifier("main"), module));
      delete(logFile);
    });
  }
}
