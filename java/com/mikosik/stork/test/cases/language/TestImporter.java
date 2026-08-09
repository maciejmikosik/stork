package com.mikosik.stork.test.cases.language;

import static com.mikosik.stork.test.ProgramTest.minimalProgramTest;
import static com.mikosik.stork.test.StorkDirectoryBuilder.path;
import static org.quackery.Suite.suite;

import org.quackery.Test;

import com.mikosik.stork.test.ProgramTest;

public class TestImporter {
  public static Test testImporter() {
    return suite("importer")
        .add(canImportFromSubdirectoryToRoot())
        .add(canImportFromDeepSubdirectoryToRoot())
        .add(canImportFromSiblingDirectory())
        .add(canImportFromParentDirectory());
  }

  private static Test canImportFromSubdirectoryToRoot() {
    return programTest("can import from subdirectory to root")
        .add(path()
            .imports("sub/message")
            .source("main(stdin) { message }"))
        .add(path("sub")
            .source("message { 'ok' }"))
        .stdout("ok");
  }

  private static Test canImportFromDeepSubdirectoryToRoot() {
    return programTest("can import from deep subdirectory to root")
        .add(path()
            .imports("subA/subB/message")
            .source("main(stdin) { message }"))
        .add(path("subA/subB")
            .source("message { 'ok' }"))
        .stdout("ok");
  }

  private static Test canImportFromSiblingDirectory() {
    return programTest("can import from sibling directory")
        .add(path()
            .imports("dirA/messageA")
            .source("main(stdin) { messageA }"))
        .add(path("dirA")
            .imports("dirB/messageB")
            .source("messageA { messageB }"))
        .add(path("dirB")
            .source("messageB { 'ok' }"))
        .stdout("ok");
  }

  private static Test canImportFromParentDirectory() {
    return programTest("can import from parent directory")
        .add(path()
            .imports("dirA/dirB/messageB")
            .source("main(stdin) { messageB }"))
        .add(path("dirA/dirB")
            .imports("dirA/messageA")
            .source("messageB { messageA }"))
        .add(path("dirA")
            .source("messageA { 'ok' }"))
        .stdout("ok");
  }

  private static ProgramTest programTest(String name) {
    return minimalProgramTest(name);
  }
}
