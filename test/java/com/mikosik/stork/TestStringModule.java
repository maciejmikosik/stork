package com.mikosik.stork;

import static com.mikosik.stork.testing.StorkModuleTest.testEqual;
import static org.quackery.Suite.suite;

import org.quackery.Test;

public class TestStringModule {
  public static Test testStringModule() {
    return suite("string.stork")
        .add(suite("parsing")
            .add(testEqual("\"\"", "none"))
            .add(testEqual("\"a\"", "some(97)(none)"))
            .add(testEqual("\"012\"", "some(48)(some(49)(some(50)(none)))")));
  }
}
