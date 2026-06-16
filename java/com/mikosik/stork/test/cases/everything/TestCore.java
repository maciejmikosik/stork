package com.mikosik.stork.test.cases.everything;

import static com.mikosik.stork.common.ImmutableList.list;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Namespace.namespace;
import static com.mikosik.stork.model.Variable.variable;
import static com.mikosik.stork.problem.compile.link.FunctionDefinedMoreThanOnce.functionDefinedMoreThanOnce;
import static com.mikosik.stork.test.ProgramTest.programTest;
import static com.mikosik.stork.test.StorkDirectoryBuilder.path;
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
        .add(path("lang/stream")
            .source("length(stream) { 0 }"))
        .expect(functionDefinedMoreThanOnce(
            identifier(namespace(list("lang", "stream")), variable("length"))));
  }
}
