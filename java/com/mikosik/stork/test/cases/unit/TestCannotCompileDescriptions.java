package com.mikosik.stork.test.cases.unit;

import static com.mikosik.stork.common.Description.description;
import static com.mikosik.stork.common.ImmutableList.list;
import static com.mikosik.stork.compile.tokenize.Label.label;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Namespace.namespace;
import static com.mikosik.stork.model.Variable.variable;
import static com.mikosik.stork.problem.Describe.describe;
import static com.mikosik.stork.problem.compile.importing.IllegalCharacter.illegalCharacter;
import static com.mikosik.stork.problem.compile.link.DuplicatedFunction.duplicatedFunction;
import static com.mikosik.stork.problem.compile.link.UndefinedFunction.undefinedFunction;
import static com.mikosik.stork.problem.compile.link.VariableCannotBeBound.variableCannotBeBound;
import static com.mikosik.stork.problem.compile.parse.UnexpectedToken.unexpected;
import static com.mikosik.stork.problem.compile.tokenize.IllegalCharacterInCode.illegalCharacterInCode;
import static com.mikosik.stork.problem.compile.tokenize.IllegalCharacterInString.illegalCharacterInString;
import static com.mikosik.stork.test.Assertions.assertMatch;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;

import org.quackery.Test;

import com.mikosik.stork.common.Description;
import com.mikosik.stork.problem.compile.CannotCompile;

public class TestCannotCompileDescriptions {
  public static Test testCannotCompileDescriptions() {
    return suite("describer can describe compiler problems")
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
            duplicatedFunction(
                identifier(namespace(list("a", "b")), variable("c"))),
            "function [a/b/c] is defined more than once"))
        .add(test(
            undefinedFunction(
                identifier(namespace(list("a", "b")), variable("c")),
                identifier(namespace(list("x", "y")), variable("z"))),
            "function [a/b/c] imports undefined function [x/y/z]"))
        .add(test(
            variableCannotBeBound(
                identifier(namespace(list("a", "b")), variable("c")),
                variable("var")),
            "function [a/b/c] uses undefined variable [var]"));
  }

  private static Test test(CannotCompile cannotCompile, Description expected) {
    return newCase(cannotCompile.getClass().getSimpleName(), () -> {
      assertMatch(expected, describe(cannotCompile));
    });
  }

  private static Test test(CannotCompile cannotCompile, String expected) {
    return test(cannotCompile, description(expected));
  }
}
