package com.mikosik.stork.test.cases.language;

import static com.mikosik.stork.common.col.ImmutableList.single;
import static com.mikosik.stork.common.io.Ascii.isLetter;
import static com.mikosik.stork.common.io.Ascii.isNewline;
import static com.mikosik.stork.compile.err.Problems.malformedImportLine;
import static com.mikosik.stork.model.exp.Namespace.namespace;
import static com.mikosik.stork.model.exp.Namespace.namespaceRoot;
import static com.mikosik.stork.test.ProgramTest.minimalProgramTest;
import static com.mikosik.stork.test.StorkDirectoryBuilder.path;
import static java.util.stream.IntStream.range;
import static org.quackery.Suite.suite;

import org.quackery.Test;

import com.mikosik.stork.model.exp.Namespace;
import com.mikosik.stork.test.ProgramTest;

public class TestImporterProblems {
  private static final Namespace root = namespaceRoot();

  public static Test testImporterProblems() {
    return suite("importer reports")
        .add(reportsMalformedLines())
        .add(reportsMultipleProblems());
  }

  private static Test reportsMalformedLines() {
    return suite("malformed lines")
        .add(suite("illegal characters")
            .addAll(range(0, 128)
                .filter(character -> !isLetter((byte) character))
                .filter(character -> !isNewline((byte) character))
                .mapToObj(character -> testMalformedLine(
                    Character.toString(character)))
                .toList()))
        .add(suite("illegal slashes")
            .add(testMalformedLine("/a/b"))
            .add(testMalformedLine("a//b"))
            .add(testMalformedLine("a/b/"))
            .add(testMalformedLine("/a/b c"))
            .add(testMalformedLine("a//b c"))
            .add(testMalformedLine("a/b/ c"))
            .add(testMalformedLine("a/b /c"))
            .add(testMalformedLine("a/b c/d"))
            .add(testMalformedLine("a/b c/"))
            .add(testMalformedLine("/"))
            .add(testMalformedLine("//"))
            .add(testMalformedLine("/ /")))
        .add(suite("illegal spaces")
            .add(testMalformedLine(" a/b"))
            .add(testMalformedLine("a/b "))
            .add(testMalformedLine(" a/b c"))
            .add(testMalformedLine("a/b  c"))
            .add(testMalformedLine("a/b c ")))
        .add(suite("wrong number of tokens")
            .add(testMalformedLine(""))
            .add(testMalformedLine(" "))
            .add(testMalformedLine("a b c")));
  }

  private static Test testMalformedLine(String line) {
    return programTest(line)
        .imports(line + "\n")
        .source("main(stdin) { 'ok' }")
        .expect(malformedImportLine()
            .location(root)
            .object(line));
  }

  private static Test reportsMultipleProblems() {
    return suite("multiple problems")
        .add(programTest("in same file")
            .imports("!\n@\n#\n")
            .source("main(stdin) { 'ok' }")
            .expect(
                malformedImportLine()
                    .location(root)
                    .object("!"),
                malformedImportLine()
                    .location(root)
                    .object("@"),
                malformedImportLine()
                    .location(root)
                    .object("#")))
        .add(programTest("in different files")
            .add(path("a")
                .imports("!"))
            .add(path("b")
                .imports("@"))
            .add(path("c")
                .imports("#"))
            .source("main(stdin) { 'ok' }")
            .expect(
                malformedImportLine()
                    .location(namespace(single("a")))
                    .object("!"),
                malformedImportLine()
                    .location(namespace(single("b")))
                    .object("@"),
                malformedImportLine()
                    .location(namespace(single("c")))
                    .object("#")));
  }

  private static ProgramTest programTest(String name) {
    return minimalProgramTest(name);
  }
}
