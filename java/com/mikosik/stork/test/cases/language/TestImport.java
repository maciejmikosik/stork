package com.mikosik.stork.test.cases.language;

import static com.mikosik.stork.test.ProgramTest.minimalProgramTest;
import static org.quackery.Suite.suite;

import org.quackery.Test;

import com.mikosik.stork.test.ProgramTest;

public class TestImport {
  public static Test testImport() {
    return suite("import")
        .add(suite("successful")
            .add(canImportFromSubdirectoryToRoot())
            .add(canImportFromDeepSubdirectoryToRoot())
            .add(canImportFromNeighborDirectory())
            .add(canImportFromParentDirectory()));
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
