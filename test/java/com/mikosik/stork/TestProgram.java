package com.mikosik.stork;

import static com.mikosik.stork.testing.ProgramTest.programTest;
import static org.quackery.Suite.suite;

import org.quackery.Test;

public class TestProgram {
  public static Test testProgram() {
    return suite("testing program")
        .add(programTest("'abc'", "abc"))
        .add(programTest("append('!')('Hello World')", "Hello World!"))
        .add(programTest("append(single(33))('Hello World')", "Hello World!"));
  }
}
