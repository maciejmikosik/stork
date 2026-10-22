package com.mikosik.stork.test.suites.documentation;

import static com.mikosik.stork.test.ProgramTest.programTest;
import static org.quackery.Suite.suite;

import org.quackery.Test;

public class TestStdio {
  public static Test testStdio() {
    return suite("stdio")
        .add(programTest("does nothing")
            .source("""
                main(stdin) {
                  stdin
                }
                """)
            .stdin("Hello World!")
            .stdout("Hello World!"))
        .add(programTest("list operations")
            .imports("""
                lang/list/reverse
                lang/list/append
                """)
            .source("""
                main(stdin) {
                  stdin
                    .reverse
                    .append(stdin)
                }
                """)
            .stdin("abc")
            .stdout("cbaabc"))
        .add(programTest("integer arithmetics")
            .imports("""
                lang/list/each
                lang/integer/add
                """)
            .source("""
                main(stdin) {
                  stdin.each(add(1))
                }
                """)
            .stdin("HAL")
            .stdout("IBM"))
        .add(programTest("string literal")
            .source("""
                main(stdin) {
                  "Hello World!"
                }
                """)
            .stdout("Hello World!"));
  }
}
