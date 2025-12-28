package com.mikosik.stork.test.cases.language;

import static com.mikosik.stork.common.io.Ascii.isAlphanumeric;
import static com.mikosik.stork.common.io.Ascii.isPrintable;
import static com.mikosik.stork.problem.compile.importing.IllegalCharacter.illegalCharacter;
import static com.mikosik.stork.test.ProgramTest.minimalProgramTest;
import static java.util.stream.IntStream.range;
import static org.quackery.Suite.suite;

import org.quackery.Test;

import com.mikosik.stork.test.ProgramTest;

public class TestImport {
  public static Test testImport() {
    return suite("import")
        .add(suite("validate import syntax")
            .add(reportIllegalPrintableCharacters()))
        .add(suite("successful")
            .add(canImportFromSubdirectoryToRoot())
            .add(canImportFromDeepSubdirectoryToRoot())
            .add(canImportFromSiblingDirectory())
            .add(canImportFromParentDirectory()));
  }

  private static Test reportIllegalPrintableCharacters() {
    var cases = range(0, 256)
        .filter(character -> isPrintable((byte) character))
        .filter(character -> !isAlphanumeric((byte) character))
        .filter(character -> character != '/')
        .filter(character -> character != ' ')
        .mapToObj(character -> reportIllegalCharacter((byte) character))
        .toList();
    return suite("report illegal characters").addAll(cases);
  }

  private static Test reportIllegalCharacter(byte character) {
    return programTest("character [%c] is illegal".formatted(character))
        .imports("ab%cde".formatted(character))
        .source("main(stdin) { 'ok' }")
        .expect(illegalCharacter("ab%cde".formatted(character), character));
  }

  private static Test canImportFromSubdirectoryToRoot() {
    return programTest("can import from subdirectory to root")
        .imports("sub/message")
        .source("main(stdin) { message }")
        .namespace("sub")
        .source("message { 'ok' }")
        .stdout("ok");
  }

  private static Test canImportFromDeepSubdirectoryToRoot() {
    return programTest("can import from deep subdirectory to root")
        .imports("subA/subB/message")
        .source("main(stdin) { message }")
        .namespace("subA/subB")
        .source("message { 'ok' }")
        .stdout("ok");
  }

  private static Test canImportFromSiblingDirectory() {
    return programTest("can import from sibling directory")
        .imports("dirA/messageA")
        .source("main(stdin) { messageA }")
        .namespace("dirA")
        .imports("dirB/messageB")
        .source("messageA { messageB }")
        .namespace("dirB")
        .source("messageB { 'ok' }")
        .stdout("ok");
  }

  private static Test canImportFromParentDirectory() {
    return programTest("can import from parent directory")
        .imports("dirA/dirB/messageB")
        .source("main(stdin) { messageB }")
        .namespace("dirA/dirB")
        .imports("dirA/messageA")
        .source("messageB { messageA }")
        .namespace("dirA")
        .source("messageA { 'ok' }")
        .stdout("ok");
  }

  private static ProgramTest programTest(String name) {
    return minimalProgramTest(name);
  }
}
