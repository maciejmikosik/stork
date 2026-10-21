package com.mikosik.stork.test.cases;

import static com.mikosik.stork.test.ProgramTest.programTest;
import static org.quackery.Suite.suite;

import org.quackery.Test;

public class TestDocumentation {
  public static Test testDocumentation() {
    return suite("documentation")
        .add(testStdio())
        .add(testFunction());
  }

  private static Test testStdio() {
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

  private static Test testFunction() {
    return suite("function")
        .add(suite("invocation")
            .add(programTest("one argument")
                .imports("lang/list/reverse")
                .source("""
                    main(stdin) {
                      reverse("!dlroW olleH")
                    }
                    """)
                .stdout("Hello World!"))
            .add(programTest("two arguments")
                .imports("lang/list/append")
                .source("""
                    main(stdin) {
                      append("!")("Hello World")
                    }
                    """)
                .stdout("Hello World!"))
            .add(programTest("nested")
                .imports("""
                    lang/list/append
                    lang/list/prepend
                    """)
                .source("""
                    main(stdin) {
                      append("!")(prepend("Hello ")("World"))
                    }
                    """)
                .stdout("Hello World!")))
        .add(suite("chain")
            .add(programTest("single")
                .imports("lang/list/append")
                .source("""
                    main(stdin) {
                      "Hello World".append("!")
                    }
                    """)
                .stdout("Hello World!"))
            .add(programTest("double")
                .imports("""
                    lang/list/append
                    lang/list/prepend
                    """)
                .source("""
                    main(stdin) {
                      "World"
                        .prepend("Hello ")
                        .append("!")
                    }
                    """)
                .stdout("Hello World!")))
        .add(suite("definition")
            .add(programTest("one parameter")
                .imports("lang/list/append")
                .source("""
                    main(stdin) {
                      shout("Hello World")
                    }

                    shout(message) {
                      message.append("!")
                    }
                    """)
                .stdout("Hello World!"))
            .add(programTest("two parameters")
                .imports("""
                    lang/list/append
                    lang/list/cycle
                    lang/list/limit
                    """)
                .source("""
                    main(stdin) {
                      yell(3)("Hello World")
                    }

                    yell(loudness)(message) {
                      message
                        .append(cycle("!").limit(loudness))
                    }
                    """)
                .stdout("Hello World!!!"))
            .add(programTest("no parameters")
                .source("""
                    main(stdin) {
                      message
                    }

                    message {
                      "Hello World!"
                    }
                    """)
                .stdout("Hello World!")))
        .add(suite("currying")
            .add(programTest("example")
                .imports("lang/list/append")
                .source("""
                    main(stdin) {
                      "Hello World"
                        .twice(append("!"))
                    }

                    twice(action)(object) {
                      object
                        .action
                        .action
                    }
                    """)
                .stdout("Hello World!!"))
            .add(programTest("eta reduction")
                .imports("""
                    lang/list/append
                    lang/list/cycle
                    lang/list/limit
                    """)
                .source("""
                    main(stdin) { "ok" }

                    shout {
                      append("!")
                    }

                    yell(loudness) {
                      append(cycle("!").limit(loudness))
                    }
                    """)
                .stdout("ok"))
            .add(programTest("long chain")
                .imports("""
                    lang/list/repeat
                    lang/list/limit
                    lang/list/each
                    lang/list/append
                    lang/list/flatten
                    """)
                .source("""
                    main(stdin) {
                      shoutTimes(3)("Hello World")
                    }

                    shoutTimes(n)(message) {
                      message
                        .repeat
                        .limit(n)
                        .each(append("!"))
                        .each(append(" "))
                        .flatten
                    }
                    """)
                .stdout("Hello World! Hello World! Hello World! ")))
        .add(suite("pipe")
            .add(programTest("example")
                .imports("""
                    lang/list/repeat
                    lang/list/limit
                    lang/list/each
                    lang/list/append
                    lang/list/flatten
                    """)
                .source("""
                    main(stdin) { "ok" }

                    twice(action)(object) {
                      .action
                      .action
                    }

                    shoutTimes(n) {
                      .repeat
                      .limit(n)
                      .each(append("!"))
                      .each(append(" "))
                      .flatten
                    }
                    """)
                .stdout("ok")))
        .add(suite("lambda")
            .add(programTest("example")
                .imports("""
                    lang/list/filter
                    lang/integer/atLeast
                    lang/integer/atMost
                    lang/integer/add
                    lang/boolean/and
                    """)
                .source("""
                    main(stdin) {
                      "a1b2c3d4e5"
                        .filter((character) {
                          and
                            (atLeast(a)(character))
                            (atMost(z)(character))
                      })
                    }

                    a { 97 }
                    z { a.add(25) }
                    """)
                .stdout("abcde"))
            .add(programTest("extracted")
                .imports("""
                    lang/list/filter
                    lang/integer/atLeast
                    lang/integer/atMost
                    lang/integer/add
                    lang/boolean/and
                    """)
                .source("""
                    main(stdin) {
                      "a1b2c3d4e5"
                        .filter(isLetter)
                    }

                    isLetter(character) {
                      and
                        (atLeast(a)(character))
                        (atMost(z)(character))
                    }

                    a { 97 }
                    z { a.add(25) }
                    """)
                .stdout("abcde")));
  }
}
