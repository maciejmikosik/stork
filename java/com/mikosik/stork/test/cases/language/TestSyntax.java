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
        .add(suite("pipe")
            .add(pipeCanHaveSingleFunction())
            .add(pipeCanHaveApplication())
            .add(pipeCanBeLongAndHaveApplications())
            .add(pipeCanBeArgument()))
        .add(suite("binding")
            .add(suite("shadowing")
                .add(shadowingLambdaParameters())
                .add(shadowingFunctionParameters())
                .add(shadowingFunctionByLambdaParameters()))
            .add(appliedArgumentIsNotAccidentlyBoundToLambdaParameter()))
        .add(cachesDuplicatedArgument());
  }

  private static Test enablesStringLiterals() {
    return programTest("enables string literals")
        .source("""
            main(stdin) {
              "Hello World!"
            }
            """)
        .stdout("Hello World!");
  }

  private static Test functionCanReturnArgument() {
    return programTest("can return argument")
        .source("""
            main(stdin) {
              function("mock")
            }

            function(string) {
              string
            }
            """)
        .stdout("mock");
  }

  private static Test functionCanReturnConstant() {
    return programTest("can return constant")
        .source("""
            main(stdin) {
              constant
            }

            constant {
              "mock"
            }
            """)
        .stdout("mock");
  }

  private static Test functionCanDelegate() {
    return programTest("can delegate")
        .imports("lang/stream/some")
        .source("""
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

  private static Test applicationArgumentCanBeInteger() {
    return programTest("argument can be integer")
        .imports("lang/stream/some")
        .source("""
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
  private static Test applicationArgumentCanBeLambda() {
    return programTest("argument can be lambda")
        .imports("lang/stream/some")
        .source("""
            main(stdin) {
              apply((string){some(33)(string)})("mock")
            }

            apply(f)(x) {
              f(x)
            }
            """)
        .stdout("!mock");
  }

  private static Test chainCanStartWithInteger() {
    return programTest("can start with integer")
        .imports("""
            lang/stream/some
            """)
        .source("""
            main(stdin) {
              33.function
            }

            function(char) {
              some(char)("mock")
            }
            """)
        .stdout("!mock");
  }

  private static Test chainCanStartWithString() {
    return programTest("can start with string")
        .imports("lang/stream/some")
        .source("""
            main(stdin) {
              "mock".function
            }

            function(string) {
              some(33)(string)
            }
            """)
        .stdout("!mock");
  }

  private static Test chainCanStartWithApplication() {
    return programTest("can start with application")
        .imports("lang/stream/some")
        .source("""
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

  private static Test chainCanBeLong() {
    return programTest("can be long")
        .imports("lang/stream/some")
        .source("""
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

  private static Test chainCanHaveApplication() {
    return programTest("can have application")
        .imports("lang/stream/some")
        .source("""
            main(stdin) {
              "mock".function(65)(66)
            }

            function(charA)(charB)(string) {
              some(charB)(some(charA)(string))
            }
            """)
        .stdout("BAmock");
  }

  private static Test chainCanBeLongAndHaveApplications() {
    return programTest("can be long and have applications")
        .imports("lang/stream/some")
        .source("""
            main(stdin) {
              function(65)("mock").function(66).function(67)
            }

            function(char)(string) {
              some(char)(string)
            }
            """)
        .stdout("CBAmock");
  }

  private static Test chainCanBeArgument() {
    return programTest("can be argument")
        .imports("lang/stream/some")
        .source("""
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

  private static Test pipeCanHaveSingleFunction() {
    return programTest("can have single function")
        .imports("lang/stream/some")
        .source("""
            main(stdin) {
              pipe("mock")
            }

            pipe {
              .functionA
            }

            functionA(string) {
              some(65)(string)
            }
            """)
        .stdout("Amock");
  }

  private static Test pipeCanHaveApplication() {
    return programTest("can have application")
        .imports("lang/stream/some")
        .source("""
            main(stdin) {
              pipe("mock")
            }

            pipe {
              .some(65)
            }
            """)
        .stdout("Amock");
  }

  private static Test pipeCanBeLongAndHaveApplications() {
    return programTest("can be long and have applications")
        .imports("lang/stream/some")
        .source("""
            main(stdin) {
              pipe("mock")
            }

            pipe {
              .some(65)
              .some(66)
              .some(67)
            }
            """)
        .stdout("CBAmock");
  }

  private static Test pipeCanBeArgument() {
    return programTest("can be argument")
        .imports("lang/stream/some")
        .source("""
            main(stdin) {
              identity(.some(65))("mock")
            }

            identity(x) {
              x
            }
            """)
        .stdout("Amock");
  }

  private static Test shadowingLambdaParameters() {
    return programTest("lambda parameters")
        .source("""
            main(stdin) {
              apply((string)(string){ string })("mockA")("mockB")
            }

            apply(lambda) {
              lambda
            }
            """)
        .stdout("mockB");
  }

  private static Test shadowingFunctionParameters() {
    return programTest("function parameters")
        .source("""
            main(stdin) {
              function("mockA")("mockB")
            }

            function(string)(string) {
              string
            }
            """)
        .stdout("mockB");
  }

  private static Test shadowingFunctionByLambdaParameters() {
    return programTest("function by lambda parameters")
        .source("""
            main(stdin) {
              function("mockA")("mockB")
            }

            function(string) {
              (string) { string }
            }
            """)
        .stdout("mockB");
  }

  private static Test appliedArgumentIsNotAccidentlyBoundToLambdaParameter() {
    return programTest("applied argument is not accidently bound to lambda parameter")
        .source("""
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

  private static Test cachesDuplicatedArgument() {
    return programTest("caches duplicated arguments")
        .imports("lang/stream/some")
        .source("""
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
