package com.mikosik.stork.test.cases.unit;

import static com.mikosik.stork.common.Description.description;
import static com.mikosik.stork.compile.tokenize.Label.label;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Variable.variable;
import static com.mikosik.stork.problem.Describe.describe;
import static com.mikosik.stork.problem.compile.importing.IllegalCharacter.illegalCharacter;
import static com.mikosik.stork.problem.compile.link.FunctionDefinedMoreThanOnce.functionDefinedMoreThanOnce;
import static com.mikosik.stork.problem.compile.link.FunctionNotDefined.functionNotDefined;
import static com.mikosik.stork.problem.compile.link.VariableCannotBeBound.variableCannotBeBound;
import static com.mikosik.stork.problem.compile.parse.UnexpectedToken.unexpected;
import static com.mikosik.stork.problem.compile.tokenize.IllegalCharacterInCode.illegalCharacterInCode;
import static com.mikosik.stork.problem.compile.tokenize.IllegalCharacterInString.illegalCharacterInString;
import static com.mikosik.stork.problem.compute.CannotCompute.cannotCompute;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;
import static org.quackery.report.AssertException.assertEquals;

import org.quackery.Test;

import com.mikosik.stork.common.Description;
import com.mikosik.stork.problem.Problem;

public class TestProblemDescriptions {
  public static Test testProblemDescriptions() {
    return suite("problem descriptions")
        .add(test(
            illegalCharacter("text", (byte) '!'),
            "import [text] contains illegal ascii character [!]"))
        .add(test(
            illegalCharacterInString((byte) '!'),
            "string contains illegal ascii character [!]"))
        .add(test(
            illegalCharacterInCode((byte) '!'),
            "code contains illegal ascii character [!]"))
        .add(test(
            unexpected(label("label_name")),
            "unexpected label [label_name]"))
        .add(test(
            functionDefinedMoreThanOnce(identifier("a.b.c")),
            "function [a.b.c] is defined more than once"))
        .add(test(
            functionNotDefined(identifier("a.b.c"), identifier("x.y.z")),
            "function [a.b.c] imports undefined function [x.y.z]"))
        .add(test(
            variableCannotBeBound(identifier("a.b.c"), variable("var")),
            "function [a.b.c] uses undefined variable [var]"))
        .add(test(
            cannotCompute(),
            "cannot compute"));
  }

  private static Test test(Problem problem, Description expected) {
    return newCase(problem.getClass().getSimpleName(), () -> {
      assertEquals(describe(problem), expected);
    });
  }

  private static Test test(Problem problem, String expected) {
    return test(problem, description(expected));
  }
}
