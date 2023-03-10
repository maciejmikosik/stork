package com.mikosik.stork.test.cases;

import static com.mikosik.stork.test.ProgramTest2.programTest;
import static org.quackery.Suite.suite;

import org.quackery.Test;

import com.mikosik.stork.test.ProgramTest2;

public class TestProgram {
  public static Test testProgram() {
    return suite("program")
        .add(suite("function")
            .add(functionReturnsArgument())
            .add(functionReturnsConstant())
            .add(functionForwardsArgumentAndResult()))
        .add(suite("application")
            .add(applicationArgumentCanBeInteger())
            .add(applicationArgumentCanBeLambda()))
        .add(suite("binding")
            .add(bindingIgnoresInteger())
            .add(suite("shadowing")
                .add(shadowingLambdaParameters())
                .add(shadowingFunctionParameters())
                .add(shadowingFunctionByLambdaParameters()))
            .add(appliedArgumentIsNotAccidentlyBoundToLambdaParameter()))
        .add(cachesDuplicatedArgument())
        .add(enablesStringLiterals())
        .add(importsCoreFunctions())
        .add(suite("stdin/stdout")
            .add(stdoutCanBeEmpty())
            .add(forwardsStdinToStdout())
            .add(prependsStdin())
            .add(appendsStdin())
            .add(buildsStdout())
            .add(processesStdinTwice()));
  }

  private static ProgramTest2 functionReturnsArgument() {
    return programTest("returns argument")
        .file("stork", """
            main(stdin) {
              identity("argument")
            }

            identity(x) {
              x
            }
            """)
        .stdout("argument");
  }

  private static ProgramTest2 functionReturnsConstant() {
    return programTest("returns constant")
        .file("stork", """
            main(stdin) {
              constant
            }

            constant {
              "constant"
            }
            """)
        .stdout("constant");
  }

  private static ProgramTest2 functionForwardsArgumentAndResult() {
    return programTest("forwards argument and result")
        .file("stork", """
            main(stdin) {
              function("y")
            }

            function(x) {
              identity(x)
            }

            identity(x) {
              x
            }
            """)
        .stdout("y");
  }

  private static ProgramTest2 applicationArgumentCanBeInteger() {
    return programTest("argument can be integer")
        .file("stork", """
            main(stdin) {
              function(2)
            }

            function(x) {
              "x"
            }
            """)
        .stdout("x");
  }

  private static ProgramTest2 applicationArgumentCanBeLambda() {
    return programTest("argument can be lambda")
        .file("stork", """
            main(stdin) {
              apply((x){x})("")
            }

            apply(f)(x) {
              f(x)
            }
            """)
        .stdout("");
  }

  private static ProgramTest2 bindingIgnoresInteger() {
    return programTest("ignores integer")
        .file("stork", """
            apply2(f) {
              f(2)
            }

            main(stdin) {
              ""
            }
            """)
        .stdout("");
  }

  private static ProgramTest2 shadowingLambdaParameters() {
    return programTest("lambda parameters")
        .file("stork", """
            main(stdin) {
              apply((x)(x){ x })("a")("b")
            }

            apply(lambda) {
              lambda
            }
            """)
        .stdout("b");
  }

  private static ProgramTest2 shadowingFunctionParameters() {
    return programTest("function parameters")
        .file("stork", """
            main(stdin) {
              function("a")("b")
            }

            function(x)(x) {
              x
            }
            """)
        .stdout("b");
  }

  private static ProgramTest2 shadowingFunctionByLambdaParameters() {
    return programTest("function by lambda parameters")
        .file("stork", """
            main(stdin) {
              function("a")("b")
            }

            function(x) {
              (x) { x }
            }
            """)
        .stdout("b");
  }

  private static ProgramTest2 appliedArgumentIsNotAccidentlyBoundToLambdaParameter() {
    return programTest("applied argument is not accidently bound to lambda parameter")
        .file("stork", """
            main(stdin) {
              f(y)(z)
            }

            f(x)(y) {
              g(x)
            }

            g(x) { x }

            y { "y" }
            z { "z" }
            """)
        .stdout("y");
  }

  private static ProgramTest2 cachesDuplicatedArgument() {
    return programTest("caches duplicated arguments")
        .file("import", """
            lang.integer.add
            lang.integer.equal
            """)
        .file("stork", """
            main(stdin) {
              equal(0)(longCalculation)
                ("")
                ("")
            }

            longCalculation {
              double(double(double(double(double(
              double(double(double(double(double(
              double(double(double(double(double(
              double(double(double(double(double(
              double(double(double(double(double(
              double(double(double(double(double(
              double(double(double(double(double(
              double(double(double(double(double(
              double(double(double(double(double(
              double(double(double(double(double(
              0))))))))))))))))))))))))))))))))))))))))))))))))))
            }

            double(x) {
              add(x)(x)
            }
            """)
        .stdout("");
  }

  private static ProgramTest2 enablesStringLiterals() {
    return programTest("enables string literals")
        .file("stork", """
            main(stdin) {
              "Hello World!"
            }
            """)
        .stdout("Hello World!");
  }

  private static ProgramTest2 importsCoreFunctions() {
    return programTest("imports core functions")
        .file("import", """
            lang.stream.append
            """)
        .file("stork", """
            main(stdin) {
              append("!")("Hello World")
            }
            """)
        .stdout("Hello World!");
  }

  private static ProgramTest2 stdoutCanBeEmpty() {
    return programTest("stdout can be empty")
        .file("stork", """
            main(stdin) {
              ""
            }
            """)
        .stdout("");
  }

  private static ProgramTest2 forwardsStdinToStdout() {
    return programTest("forwards stdin to stdout")
        .file("stork", """
            main(stdin) {
              stdin
            }
            """)
        .stdin("Hello World!")
        .stdout("Hello World!");
  }

  private static ProgramTest2 prependsStdin() {
    return programTest("prepends stdin")
        .file("import", """
            lang.stream.prepend
            """)
        .file("stork", """
            main(stdin) {
              prepend("!")(stdin)
            }
            """)
        .stdin("Hello World")
        .stdout("!Hello World");
  }

  private static ProgramTest2 appendsStdin() {
    return programTest("appends stdin")
        .file("import", """
            lang.stream.append
            """)
        .file("stork", """
            main(stdin) {
              append("!")(stdin)
            }
            """)
        .stdin("Hello World")
        .stdout("Hello World!");
  }

  private static ProgramTest2 buildsStdout() {
    return programTest("builds stdout")
        .file("import", """
            lang.stream.some
            lang.stream.none
            """)
        .file("stork", """
            main(stdin) {
              some(120)(some(121)(some(122)(none)))
            }
            """)
        .stdout("xyz");
  }

  private static ProgramTest2 processesStdinTwice() {
    return programTest("processes stdin twice")
        .file("import", """
            lang.stream.append
            lang.stream.reverse
            """)
        .file("stork", """
            main(stdin) {
              reverse(append(reverse(stdin))(reverse(stdin)))
            }
            """)
        .stdin("abc")
        .stdout("abcabc");
  }
}
