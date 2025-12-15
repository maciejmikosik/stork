package com.mikosik.stork.test.cases.everything;

import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.problem.compile.link.FunctionDefinedMoreThanOnce.functionDefinedMoreThanOnce;
import static com.mikosik.stork.test.ProgramTest.programTest;
import static org.quackery.Suite.suite;

import org.quackery.Test;

public class TestSimplePrograms {
  public static Test testSimplePrograms() {
    return suite("simple programs")
        .add(importsCoreFunctions())
        .add(cannotOverrideCoreFunction())
        .add(suite("stdin/stdout")
            .add(stdoutCanBeEmpty())
            .add(forwardsStdinToStdout())
            .add(prependsStdin())
            .add(appendsStdin())
            .add(processesStdinTwice()));
  }

  private static Test importsCoreFunctions() {
    return programTest("imports core functions")
        .importFile("""
            lang.stream.append
            """)
        .sourceFile("""
            main(stdin) {
              append("!")("Hello World")
            }
            """)
        .stdout("Hello World!");
  }

  private static Test cannotOverrideCoreFunction() {
    return programTest("cannot override core function")
        .sourceFile("lang/stream", """
            length(stream) { 0 }
            """)
        .expect(functionDefinedMoreThanOnce(identifier("lang.stream.length")));
  }

  private static Test stdoutCanBeEmpty() {
    return programTest("stdout can be empty")
        .sourceFile("""
            main(stdin) {
              ""
            }
            """)
        .stdout("");
  }

  private static Test forwardsStdinToStdout() {
    return programTest("forwards stdin to stdout")
        .sourceFile("""
            main(stdin) {
              stdin
            }
            """)
        .stdin("Hello World!")
        .stdout("Hello World!");
  }

  private static Test prependsStdin() {
    return programTest("prepends stdin")
        .importFile("""
            lang.stream.prepend
            """)
        .sourceFile("""
            main(stdin) {
              prepend("!")(stdin)
            }
            """)
        .stdin("Hello World")
        .stdout("!Hello World");
  }

  private static Test appendsStdin() {
    return programTest("appends stdin")
        .importFile("""
            lang.stream.append
            """)
        .sourceFile("""
            main(stdin) {
              append("!")(stdin)
            }
            """)
        .stdin("Hello World")
        .stdout("Hello World!");
  }

  private static Test processesStdinTwice() {
    return programTest("processes stdin twice")
        .importFile("""
            lang.stream.append
            lang.stream.reverse
            """)
        .sourceFile("""
            main(stdin) {
              reverse(append(reverse(stdin))(reverse(stdin)))
            }
            """)
        .stdin("abc")
        .stdout("abcabc");
  }
}
