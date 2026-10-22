package com.mikosik.stork.test.suites.program;

import static com.mikosik.stork.compile.err.Problems.duplicatedFunction;
import static com.mikosik.stork.model.exp.Variable.variable;
import static com.mikosik.stork.test.Factories.namespace;
import static com.mikosik.stork.test.ProgramTest.programTest;
import static com.mikosik.stork.test.StorkDirectoryBuilder.path;
import static org.quackery.Suite.suite;

import org.quackery.Test;

public class TestImportCore {
  public static Test testImportCore() {
    return suite("import core")
        .add(importsCoreFunction())
        .add(cannotOverrideCoreFunction());
  }

  private static Test importsCoreFunction() {
    return programTest("can import core function")
        .imports("lang/list/append")
        .source("main(stdin) { append('!')('Hello World') }")
        .stdout("Hello World!");
  }

  private static Test cannotOverrideCoreFunction() {
    return programTest("cannot override core function")
        .add(path("lang/list")
            .source("length(list) { 0 }"))
        .expect(duplicatedFunction()
            .in(namespace("lang/list"))
            .object(variable("length")));
  }
}
