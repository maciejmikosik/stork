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
            .add(canImportFromNeighborDirectory())
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
        .importFile("ab%cde".formatted(character))
        .sourceFile("""
            main(stdin) {
              "ok"
            }
            """)
        .expect(illegalCharacter("ab%cde".formatted(character), character));
  }

  private static Test canImportFromSubdirectoryToRoot() {
    return programTest("can import from subdirectory to root")
        .sourceFile("sub", """
            message { "ok" }
            """)
        .importFile("sub/message")
        .sourceFile("""
            main(stdin) {
              message
            }
            """)
        .stdout("ok");
  }

  private static Test canImportFromDeepSubdirectoryToRoot() {
    return programTest("can import from deep subdirectory to root")
        .sourceFile("subA/subB", """
            message { "ok" }
            """)
        .importFile("subA/subB/message")
        .sourceFile("""
            main(stdin) {
              message
            }
            """)
        .stdout("ok");
  }

  private static Test canImportFromNeighborDirectory() {
    return programTest("can import from neighbor directory")
        .sourceFile("dirA", """
            messageA { "ok" }
            """)
        .importFile("dirB", "dirA/messageA")
        .sourceFile("dirB", """
            messageB {
              messageA
            }
            """)
        .importFile("dirB/messageB")
        .sourceFile("""
            main(stdin) {
              messageB
            }
            """)
        .stdout("ok");
  }

  private static Test canImportFromParentDirectory() {
    return programTest("can import from parent directory")
        .sourceFile("dirA", """
            messageA { "ok" }
            """)
        .importFile("dirA/dirB", "dirA/messageA")
        .sourceFile("dirA/dirB", """
            messageB {
              messageA
            }
            """)
        .importFile("dirA/dirB/messageB")
        .sourceFile("""
            main(stdin) {
              messageB
            }
            """)
        .stdout("ok");
  }

  private static ProgramTest programTest(String name) {
    return minimalProgramTest(name);
  }
}
