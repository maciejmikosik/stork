package com.mikosik.stork.test.suites.unit;

import static com.mikosik.stork.common.io.BytesBuilder.bytesBuilder;
import static com.mikosik.stork.test.SourceBuilder.mainWithBody;
import static com.mikosik.stork.test.StorkDirectoryBuilder.path;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;
import static org.quackery.run.Runners.expect;

import org.quackery.Test;

public class TestAsciiStringsSafetyChecks {
  public static Test asciiStringsSafetyChecks() {
    return expect(RuntimeException.class,
        suite("ascii strings safety checks")
            .add(newCase(
                "StorkDirectoryBuilder",
                () -> path().source("München")))
            .add(newCase(
                "BytesBuilder",
                () -> bytesBuilder().appendAscii("München")))
            .add(newCase(
                "SourceBuilder.mainWithBody",
                () -> mainWithBody("München")))
            .add(newCase(
                "SourceBuilder.stringLiteral",
                () -> mainWithBody("München"))));
  }
}
