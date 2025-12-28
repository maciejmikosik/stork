package com.mikosik.stork.test.cases.everything;

import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.problem.compile.link.CannotLinkLibrary.cannotLinkLibrary;
import static com.mikosik.stork.problem.compile.link.FunctionDefinedMoreThanOnce.functionDefinedMoreThanOnce;
import static com.mikosik.stork.test.ProgramTest.programTest;
import static org.quackery.Suite.suite;

import org.quackery.Test;

public class TestCore {
  public static Test testCore() {
    return suite("core")
        .add(importsCoreFunction())
        .add(cannotOverrideCoreFunction());
  }

  private static Test importsCoreFunction() {
    return programTest("can import core function")
        .imports("lang/stream/append")
        .source("main(stdin) { append('!')('Hello World') }")
        .stdout("Hello World!");
  }

  private static Test cannotOverrideCoreFunction() {
    return programTest("cannot override core function")
        .namespace("lang/stream")
        .source("length(stream) { 0 }")
        .expect(cannotLinkLibrary(
            functionDefinedMoreThanOnce(
                identifier("lang/stream/length"))));
  }
}
