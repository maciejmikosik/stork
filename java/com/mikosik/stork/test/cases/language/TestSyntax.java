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
            .add(functionCanReturnArgument())
            .add(functionCanReturnConstant())
            .add(functionCanDelegate()))
        .add(suite("application")
            .add(applicationArgumentCanBeInteger())
            .add(applicationArgumentCanBeLambda()))
        .add(suite("chain")
            .add(chainCanStartWithInteger())
            .add(chainCanStartWithString())
            .add(chainCanStartWithApplication())
            .add(chainCanBeLong())
            .add(chainCanHaveApplication())
            .add(chainCanBeLongAndHaveApplications())
            .add(chainCanBeArgument()))
        .add(suite("binding")
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

  private static ProgramTest functionCanReturnArgument() {
    return programTest("can return argument")
        .sourceFile("""
            main(stdin) {
              function("mock")
            }

            function(string) {
              string
            }
            """)
        .stdout("mock");
  }

  private static ProgramTest functionCanReturnConstant() {
    return programTest("can return constant")
        .sourceFile("""
            main(stdin) {
              constant
            }

            constant {
              "mock"
            }
            """)
        .stdout("mock");
  }

  private static ProgramTest functionCanDelegate() {
    return programTest("can delegate")
        .importFile("lang.stream.some")
        .sourceFile("""
            main(stdin) {
              function("mock")
            }

            function(string) {
              delegate(string)
            }

            delegate(string) {
              some(33)(string)
            }
            """)
        .stdout("!mock");
  }

  private static ProgramTest applicationArgumentCanBeInteger() {
    return programTest("argument can be integer")
        .importFile("lang.stream.some")
        .sourceFile("""
            main(stdin) {
              function(33)
            }

            function(char) {
              some(char)("mock")
            }
            """)
        .stdout("!mock");
  }

  /**
   * TODO You cannot apply lambda to argument now. You should be allowed. This
   * will also simplify some test where lambda is wrapped in apply function to
   * workaround that limitation.
   */
  private static ProgramTest applicationArgumentCanBeLambda() {
    return programTest("argument can be lambda")
        .importFile("lang.stream.some")
        .sourceFile("""
            main(stdin) {
              apply((string){some(33)(string)})("mock")
            }

            apply(f)(x) {
              f(x)
            }
            """)
        .stdout("!mock");
  }

  private static ProgramTest chainCanStartWithInteger() {
    return programTest("can start with integer")
        .importFile("""
            lang.stream.some
            """)
        .sourceFile("""
            main(stdin) {
              33.function
            }

            function(char) {
              some(char)("mock")
            }
            """)
        .stdout("!mock");
  }

  private static ProgramTest chainCanStartWithString() {
    return programTest("can start with string")
        .importFile("lang.stream.some")
        .sourceFile("""
            main(stdin) {
              "mock".function
            }

            function(string) {
              some(33)(string)
            }
            """)
        .stdout("!mock");
  }

  private static ProgramTest chainCanStartWithApplication() {
    return programTest("can start with application")
        .importFile("lang.stream.some")
        .sourceFile("""
            main(stdin) {
              functionA("mock").functionB
            }

            functionA(string) {
              some(65)(string)
            }

            functionB(string) {
              some(66)(string)
            }
            """)
        .stdout("BAmock");
  }

  private static ProgramTest chainCanBeLong() {
    return programTest("can be long")
        .importFile("lang.stream.some")
        .sourceFile("""
            main(stdin) {
              "mock".functionA.functionB.functionC
            }

            functionA(string) {
              some(65)(string)
            }

            functionB(string) {
              some(66)(string)
            }

            functionC(string) {
              some(67)(string)
            }
            """)
        .stdout("CBAmock");
  }

  private static ProgramTest chainCanHaveApplication() {
    return programTest("can have application")
        .importFile("lang.stream.some")
        .sourceFile("""
            main(stdin) {
              "mock".function(65)(66)
            }

            function(charA)(charB)(string) {
              some(charB)(some(charA)(string))
            }
            """)
        .stdout("BAmock");
  }

  private static ProgramTest chainCanBeLongAndHaveApplications() {
    return programTest("can be long and have applications")
        .importFile("lang.stream.some")
        .sourceFile("""
            main(stdin) {
              function(65)("mock").function(66).function(67)
            }

            function(char)(string) {
              some(char)(string)
            }
            """)
        .stdout("CBAmock");
  }

  private static ProgramTest chainCanBeArgument() {
    return programTest("can be argument")
        .importFile("lang.stream.some")
        .sourceFile("""
            main(stdin) {
              functionB("mock".functionA)
            }

            functionA(string) {
              some(65)(string)
            }

            functionB(string) {
              some(66)(string)
            }
            """)
        .stdout("BAmock");
  }

  private static ProgramTest shadowingLambdaParameters() {
    return programTest("lambda parameters")
        .sourceFile("""
            main(stdin) {
              apply((string)(string){ string })("mockA")("mockB")
            }

            apply(lambda) {
              lambda
            }
            """)
        .stdout("mockB");
  }

  private static ProgramTest shadowingFunctionParameters() {
    return programTest("function parameters")
        .sourceFile("""
            main(stdin) {
              function("mockA")("mockB")
            }

            function(string)(string) {
              string
            }
            """)
        .stdout("mockB");
  }

  private static ProgramTest shadowingFunctionByLambdaParameters() {
    return programTest("function by lambda parameters")
        .sourceFile("""
            main(stdin) {
              function("mockA")("mockB")
            }

            function(string) {
              (string) { string }
            }
            """)
        .stdout("mockB");
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
        .importFile("lang.stream.some")
        .sourceFile("""
            main(stdin) {
              longCalculation("")
            }

            longCalculation(string) {
              f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(
              f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(
              string
               ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) )
               ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) )
            }

            f(string) {
              join(string)(string)
            }

            join(stringA)(stringB) {
              stringA
                ((head)(tail) {
                  some(head)(join(tail)(stringB))
                })
                (stringB)
            }
            """)
        .stdout("");
  }

  private static ProgramTest programTest(String name) {
    return minimalProgramTest(name);
  }
}
