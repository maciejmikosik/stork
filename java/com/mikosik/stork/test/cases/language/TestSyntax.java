package com.mikosik.stork.test.cases.language;

import static com.mikosik.stork.test.ProgramTest.minimalProgramTest;
import static org.quackery.Suite.suite;

import org.quackery.Test;

import com.mikosik.stork.test.ProgramTest;

public class TestSyntax {
  public static Test testSyntax() {
    return suite("syntax")
        .add(enablesStringLiterals())
        .add(suite("function")
            .add(functionReturnsArgument())
            .add(functionReturnsConstant())
            .add(functionForwardsArgumentAndResult()))
        .add(suite("application")
            .add(applicationArgumentCanBeInteger())
            .add(applicationArgumentCanBeLambda()))
        .add(suite("chain")
            .add(chainCanStartWithInteger())
            .add(chainCanStartWithString())
            .add(chainCanStartWithApplication())
            .add(chainCanBeLong())
            .add(chainCanHaveApplications())
            .add(chainCanBeArgument()))
        .add(suite("binding")
            .add(bindingIgnoresInteger())
            .add(suite("shadowing")
                .add(shadowingLambdaParameters())
                .add(shadowingFunctionParameters())
                .add(shadowingFunctionByLambdaParameters()))
            .add(appliedArgumentIsNotAccidentlyBoundToLambdaParameter()))
        .add(cachesDuplicatedArgument());
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

  private static ProgramTest chainCanStartWithInteger() {
    return programTest("can start with integer")
        .sourceFile("""
            main(stdin) {
              123.function
            }

            function(x) {
              "x"
            }
            """)
        .stdout("x");
  }

  private static ProgramTest chainCanStartWithString() {
    return programTest("can start with string")
        .sourceFile("""
            main(stdin) {
              "object".identity
            }

            identity(x) {
              x
            }
            """)
        .stdout("object");
  }

  private static ProgramTest chainCanStartWithApplication() {
    return programTest("can start with application")
        .sourceFile("""
            main(stdin) {
              identity("object").identity
            }

            identity(x) {
              x
            }
            """)
        .stdout("object");
  }

  private static ProgramTest chainCanBeLong() {
    return programTest("can be long")
        .sourceFile("""
            main(stdin) {
              "string".identity.identity.identity
            }

            identity(x) {
              x
            }
            """)
        .stdout("string");
  }

  private static ProgramTest chainCanHaveApplications() {
    return programTest("can have applications")
        .sourceFile("""
            main(stdin) {
              "string"
                .identity(identity)(identity)
            }

            identity(x) {
              x
            }
            """)
        .stdout("string");
  }

  private static ProgramTest chainCanBeArgument() {
    return programTest("can be argument")
        .sourceFile("""
            main(stdin) {
              identity("object".identity)
            }

            identity(x) {
              x
            }
            """)
        .stdout("object");
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
        .sourceFile("""
            main(stdin) {
              longCalculation
                ("true")
                ("false")
            }

            longCalculation {
              f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(
              f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(
              true
               ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) )
               ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) )
            }

            f(x) {
              xor(x)(x)
            }

            xor(a)(b) {
              a
                (b(false)(true))
                (b(true)(false))
            }
            true(a)(b) { a }
            false(a)(b) { b }
            """)
        .stdout("false");
  }

  private static ProgramTest programTest(String name) {
    return minimalProgramTest(name);
  }
}
