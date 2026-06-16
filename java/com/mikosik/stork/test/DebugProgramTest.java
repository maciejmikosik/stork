package com.mikosik.stork.test;

import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Variable.variable;
import static com.mikosik.stork.problem.compile.tokenize.IllegalCharacterInCode.illegalCharacterInCode;
import static com.mikosik.stork.problem.compute.FunctionMissing.functionMissing;
import static com.mikosik.stork.test.ProgramTest.minimalProgramTest;

import org.quackery.Case;
import org.quackery.Test;

// TODO convert those into unit tests and include in RunTests
public class DebugProgramTest {
  public static void main(String... args) {
    report((Case) minimalProgramTest("cannot compile / cannot compile")
        .source("main(stdin){ / }")
        .expect(illegalCharacterInCode((byte) '?')));
    report((Case) minimalProgramTest("cannot compile / cannot compute")
        .source("main(stdin){ 2 }")
        .expect(illegalCharacterInCode((byte) '?')));
    report((Case) minimalProgramTest("cannot compile / stdout")
        .source("main(stdin){ stdin }")
        .expect(illegalCharacterInCode((byte) '?')));
    report((Case) minimalProgramTest("cannot compute / cannot compile")
        .source("main(stdin){ ? }")
        .expect(functionMissing(identifier(variable("x")))));
    report((Case) minimalProgramTest("cannot compute / cannot compute")
        .source("main(stdin){ 2 }")
        .expect(functionMissing(identifier(variable("x")))));
    report((Case) minimalProgramTest("cannot compute / stdout")
        .source("main(stdin){ stdin }")
        .expect(functionMissing(identifier(variable("x")))));
    report((Case) minimalProgramTest("stdout / cannot compile")
        .source("main(stdin){ ? }")
        .stdout(""));
    report((Case) minimalProgramTest("stdout / cannot compute")
        .source("main(stdin){ 2 }")
        .stdout(""));
    report((Case) minimalProgramTest("stdout / stdout")
        .source("main(stdin){ 'a' }")
        .stdout("b"));
  }

  private static void report(Case caz) {
    System.out.println("---------- test ------------");
    System.out.println(nameOf(caz));
    System.out.println("----------------------------");
    System.out.println(getThrowable(caz).getMessage());
  }

  private static String nameOf(Test test) {
    return test.visit(
        (name, body) -> name,
        (name, children) -> name);
  }

  private static Throwable getThrowable(Case caz) {
    return caz.visit((name, body) -> {
      try {
        body.run();
      } catch (Throwable throwable) {
        return throwable;
      }
      throw new RuntimeException("no throwable");
    }, null);
  }
}
