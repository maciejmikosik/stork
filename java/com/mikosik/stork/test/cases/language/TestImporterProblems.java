package com.mikosik.stork.test.cases.language;

import static com.mikosik.stork.common.ImmutableList.list;
import static com.mikosik.stork.common.ImmutableList.single;
import static com.mikosik.stork.common.io.Ascii.isAlphanumeric;
import static com.mikosik.stork.common.io.Ascii.isPrintable;
import static com.mikosik.stork.model.exp.Namespace.namespace;
import static com.mikosik.stork.problem.compile.importing.IllegalCharacter.illegalCharacter;
import static com.mikosik.stork.problem.compile.importing.MalformedImportFile.malformedImportFile;
import static com.mikosik.stork.problem.compile.importing.MalformedImportLine.malformedImportLine;
import static com.mikosik.stork.test.ProgramTest.minimalProgramTest;
import static com.mikosik.stork.test.StorkDirectoryBuilder.path;
import static java.util.stream.IntStream.range;
import static org.quackery.Suite.suite;

import org.quackery.Test;

import com.mikosik.stork.test.ProgramTest;

public class TestImporterProblems {
  public static Test testImporterProblems() {
    return suite("importer reports problems")
        .add(reportsIllegalPrintableCharacters())
        .add(reportsMalformedImport())
        .add(reportsMultipleProblemsInSameFile())
        .add(reportsMultipleProblemsInDifferentFiles());
  }

  private static Test reportsIllegalPrintableCharacters() {
    var cases = range(0, 256)
        .filter(character -> isPrintable((byte) character))
        .filter(character -> !isAlphanumeric((byte) character))
        .filter(character -> character != '/')
        .filter(character -> character != ' ')
        .mapToObj(character -> reportsIllegalCharacter((byte) character))
        .toList();
    return suite("report illegal characters").addAll(cases);
  }

  private static Test reportsIllegalCharacter(byte character) {
    return programTest("character [%c] is illegal".formatted(character))
        .add(path("a/b")
            .imports("ab%cde".formatted(character)))
        .source("main(stdin) { 'ok' }")
        .expect(malformedImportFile(
            namespace(list("a", "b")),
            single(illegalCharacter("ab%cde".formatted(character), character))));
  }

  private static Test reportsMalformedImport() {
    return programTest("reports malformed import")
        .add(path("a/b")
            .imports("x y z"))
        .source("main(stdin) { 'ok' }")
        .expect(malformedImportFile(
            namespace(list("a", "b")),
            single(malformedImportLine("x y z"))));
  }

  private static Test reportsMultipleProblemsInSameFile() {
    return programTest("reports multiple problems in same file")
        .add(path("a/aa/aaa")
            .imports("""
                b bb bbb
                c cc ccc
                """))
        .source("main(stdin) { 'ok' }")
        .expect(malformedImportFile(
            namespace(list("a", "aa", "aaa")),
            list(
                malformedImportLine("b bb bbb"),
                malformedImportLine("c cc ccc"))));
  }

  private static Test reportsMultipleProblemsInDifferentFiles() {
    return programTest("reports multiple problems in same file")
        .add(path("a/aa/aaa")
            .imports("""
                b bb bbb
                """))
        .add(path("c/cc/ccc")
            .imports("""
                d dd ddd
                """))
        .source("main(stdin) { 'ok' }")
        .expect(
            malformedImportFile(
                namespace(list("a", "aa", "aaa")),
                single(malformedImportLine("b bb bbb"))),
            malformedImportFile(
                namespace(list("c", "cc", "ccc")),
                single(malformedImportLine("d dd ddd"))));
  }

  private static ProgramTest programTest(String name) {
    return minimalProgramTest(name);
  }
}
