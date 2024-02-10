package com.mikosik.stork.test.cases;

import static com.mikosik.stork.test.ProgramTest.programTest;
import static org.quackery.Suite.suite;

import org.quackery.Test;

import com.mikosik.stork.test.ProgramTest;

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
            .add(processesStdinTwice()));
  }

  private static ProgramTest functionReturnsArgument() {
    return programTest("returns argument")
        .sourceFile("""
            main(stdin) {
              identity("argument")
            }

            identity(x) {
              x
            }
            """)
        .stdout("argument");
  }

  private static ProgramTest functionReturnsConstant() {
    return programTest("returns constant")
        .sourceFile("""
            main(stdin) {
              constant
            }

            constant {
              "constant"
            }
            """)
        .stdout("constant");
  }

  private static ProgramTest functionForwardsArgumentAndResult() {
    return programTest("forwards argument and result")
        .sourceFile("""
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

  private static ProgramTest applicationArgumentCanBeInteger() {
    return programTest("argument can be integer")
        .sourceFile("""
            main(stdin) {
              function(2)
            }

            function(x) {
              "x"
            }
            """)
        .stdout("x");
  }

  private static ProgramTest applicationArgumentCanBeLambda() {
    return programTest("argument can be lambda")
        .sourceFile("""
            main(stdin) {
              apply((x){x})("")
            }

            apply(f)(x) {
              f(x)
            }
            """)
        .stdout("");
  }

  private static ProgramTest bindingIgnoresInteger() {
    return programTest("ignores integer")
        .sourceFile("""
            apply2(f) {
              f(2)
            }

            main(stdin) {
              ""
            }
            """)
        .stdout("");
  }

  private static ProgramTest shadowingLambdaParameters() {
    return programTest("lambda parameters")
        .sourceFile("""
            main(stdin) {
              apply((x)(x){ x })("a")("b")
            }

            apply(lambda) {
              lambda
            }
            """)
        .stdout("b");
  }

  private static ProgramTest shadowingFunctionParameters() {
    return programTest("function parameters")
        .sourceFile("""
            main(stdin) {
              function("a")("b")
            }

            function(x)(x) {
              x
            }
            """)
        .stdout("b");
  }

  private static ProgramTest shadowingFunctionByLambdaParameters() {
    return programTest("function by lambda parameters")
        .sourceFile("""
            main(stdin) {
              function("a")("b")
            }

            function(x) {
              (x) { x }
            }
            """)
        .stdout("b");
  }

  private static ProgramTest appliedArgumentIsNotAccidentlyBoundToLambdaParameter() {
    return programTest("applied argument is not accidently bound to lambda parameter")
        .sourceFile("""
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

  private static ProgramTest cachesDuplicatedArgument() {
    return programTest("caches duplicated arguments")
        .importFile("""
            lang.integer.add
            lang.integer.equal
            """)
        .sourceFile("""
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

  private static ProgramTest enablesStringLiterals() {
    return programTest("enables string literals")
        .sourceFile("""
            main(stdin) {
              "Hello World!"
            }
            """)
        .stdout("Hello World!");
  }

  private static ProgramTest importsCoreFunctions() {
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

  private static ProgramTest stdoutCanBeEmpty() {
    return programTest("stdout can be empty")
        .sourceFile("""
            main(stdin) {
              ""
            }
            """)
        .stdout("");
  }

  private static ProgramTest forwardsStdinToStdout() {
    return programTest("forwards stdin to stdout")
        .sourceFile("""
            main(stdin) {
              stdin
            }
            """)
        .stdin("Hello World!")
        .stdout("Hello World!");
  }

  private static ProgramTest prependsStdin() {
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

  private static ProgramTest appendsStdin() {
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

  private static ProgramTest processesStdinTwice() {
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
