package com.mikosik.stork.test.cases.everything;

import static com.mikosik.stork.test.ProgramTest.programTest;
import static org.quackery.Suite.suite;

import org.quackery.Test;

public class TestStdio {
  public static Test testStdio() {
    return suite("stdin/stdout")
        .add(stdoutCanBeEmpty())
        .add(forwardsStdinToStdout())
        .add(prependsStdin())
        .add(appendsStdin())
        .add(processesStdinTwice());
  }

  private static Test stdoutCanBeEmpty() {
    return programTest("stdout can be empty")
        .source("main(stdin) { '' }")
        .stdout("");
  }

  private static Test forwardsStdinToStdout() {
    return programTest("forwards stdin to stdout")
        .source("main(stdin) { stdin }")
        .stdin("ok")
        .stdout("ok");
  }

  private static Test prependsStdin() {
    return programTest("prepends stdin")
        .imports("lang/stream/prepend")
        .source("main(stdin) { prepend('!')(stdin) }")
        .stdin("ok")
        .stdout("!ok");
  }

  private static Test appendsStdin() {
    return programTest("appends stdin")
        .imports("lang/stream/append")
        .source("main(stdin) { append('!')(stdin) }")
        .stdin("ok")
        .stdout("ok!");
  }

  private static Test processesStdinTwice() {
    return programTest("processes stdin twice")
        .imports("""
            lang/stream/append
            lang/stream/reverse
            """)
        .source("""
            main(stdin) {
              reverse(append
                (reverse(stdin))
                (reverse(stdin)))
            }
            """)
        .stdin("ok")
        .stdout("okok");
  }
}
