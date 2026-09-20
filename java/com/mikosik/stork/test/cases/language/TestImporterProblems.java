package com.mikosik.stork.test.cases.language;

import static com.mikosik.stork.common.ImmutableList.list;
import static com.mikosik.stork.common.ImmutableList.single;
import static com.mikosik.stork.common.io.Ascii.isLetter;
import static com.mikosik.stork.common.io.Ascii.isNewline;
import static com.mikosik.stork.model.exp.Namespace.namespace;
import static com.mikosik.stork.model.exp.Namespace.namespaceRoot;
import static com.mikosik.stork.problem.compile.importing.MalformedImportFile.malformedImportFile;
import static com.mikosik.stork.problem.compile.importing.MalformedImportLine.malformedImportLine;
import static com.mikosik.stork.test.ProgramTest.minimalProgramTest;
import static com.mikosik.stork.test.StorkDirectoryBuilder.path;
import static java.util.stream.IntStream.range;
import static org.quackery.Suite.suite;

import org.quackery.Suite;
import org.quackery.Test;

import com.mikosik.stork.test.ProgramTest;

public class TestImporterProblems {
  public static Test testImporterProblems() {
    return suite("importer reports")
        .add(suite("illegal characters")
            .addAll(range(0, 128)
                .filter(character -> !isLetter((byte) character))
                .filter(character -> !isNewline((byte) character))
                .mapToObj(character -> singleLine(Character.toString(character)))
                .toList()))
        .add(suite("illegal slashes")
            .add(singleLine("/a/b"))
            .add(singleLine("a//b"))
            .add(singleLine("a/b/"))
            .add(singleLine("/a/b c"))
            .add(singleLine("a//b c"))
            .add(singleLine("a/b/ c"))
            .add(singleLine("a/b /c"))
            .add(singleLine("a/b c/d"))
            .add(singleLine("a/b c/"))
            .add(singleLine("/"))
            .add(singleLine("//"))
            .add(singleLine("/ /")))
        .add(suite("illegal spaces")
            .add(singleLine(" a/b"))
            .add(singleLine("a/b "))
            .add(singleLine(" a/b c"))
            .add(singleLine("a/b  c"))
            .add(singleLine("a/b c ")))
        .add(suite("wrong number of tokens")
            .add(singleLine(""))
            .add(singleLine(" "))
            .add(singleLine("a b c")))
        .add(reportsMultipleProblems());
  }

  private static Test singleLine(String line) {
    return programTest(line)
        .imports(line + "\n")
        .source("main(stdin) { 'ok' }")
        .expect(malformedImportFile(
            namespaceRoot(),
            single(malformedImportLine(line))));
  }

  private static Suite reportsMultipleProblems() {
    return suite("multiple problems")
        .add(programTest("in same file")
            .imports("!\n@\n#\n")
            .source("main(stdin) { 'ok' }")
            .expect(malformedImportFile(
                namespaceRoot(),
                list(
                    malformedImportLine("!"),
                    malformedImportLine("@"),
                    malformedImportLine("#")))))
        .add(programTest("in different files")
            .add(path("a")
                .imports("!"))
            .add(path("b")
                .imports("@"))
            .add(path("c")
                .imports("#"))
            .source("main(stdin) { 'ok' }")
            .expect(
                malformedImportFile(
                    namespace(single("a")),
                    single(malformedImportLine("!"))),
                malformedImportFile(
                    namespace(single("b")),
                    single(malformedImportLine("@"))),
                malformedImportFile(
                    namespace(single("c")),
                    single(malformedImportLine("#")))));
  }

  private static ProgramTest programTest(String name) {
    return minimalProgramTest(name);
  }
}
