package com.mikosik.stork.test.suites.language;

import static com.mikosik.stork.common.col.ImmutableList.list;
import static com.mikosik.stork.common.col.ImmutableList.single;
import static com.mikosik.stork.common.io.Ascii.isLetter;
import static com.mikosik.stork.common.io.Ascii.isNewline;
import static com.mikosik.stork.compile.err.Miscompilation.miscompilation;
import static com.mikosik.stork.model.exp.Namespace.namespace;
import static com.mikosik.stork.model.exp.Namespace.root;
import static com.mikosik.stork.model.exp.Variable.variable;
import static com.mikosik.stork.test.Factories.namespace;
import static com.mikosik.stork.test.ProgramTest.minimalProgramTest;
import static com.mikosik.stork.test.build.StorkDirectoryBuilder.path;
import static java.util.stream.IntStream.range;
import static org.quackery.Suite.suite;

import org.quackery.Test;

import com.mikosik.stork.test.ProgramTest;

public class TestImporterMiscompilations {
  public static Test testImporterMiscompilations() {
    return suite("importer reports")
        .add(reportsMalformedLines())
        .add(reportsCollisions())
        .add(reportsMultipleMiscompilations());
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
        .source("""
            main(stdin) { "ok" }
            """)
        .expect(miscompilation("malformed import")
            .in(root)
            .set("import", line));
  }

  private static Test reportsCollisions() {
    return suite("collision")
        .add(programTest("same exact import")
            .imports("""
                a/b/c
                a/b/c
                """)
            .source("""
                main(stdin) { "ok" }
                """)
            .expect(miscompilation("import collision")
                .in(root)
                .set("imports", list("a/b/c", "a/b/c"))
                .set("variable", variable("c"))))
        .add(programTest("same variable from different directory")
            .imports("""
                a/b/c
                a/x/c
                """)
            .source("""
                main(stdin) { "ok" }
                """)
            .expect(miscompilation("import collision")
                .in(root)
                .set("imports", list("a/b/c", "a/x/c"))
                .set("variable", variable("c"))))
        .add(programTest("of renamed variable")
            .imports("""
                a/b/c
                a/b/d c
                """)
            .source("""
                main(stdin) { "ok" }
                """)
            .expect(miscompilation("import collision")
                .in(root)
                .set("imports", list("a/b/c", "a/b/d c"))
                .set("variable", variable("c"))))
        .add(programTest("includes namespaces")
            .add(path("a/b")
                .imports("""
                    x/y/z
                    x/y/z
                    """))
            .add(path("a/c")
                .imports("""
                    x/y/v
                    x/y/v
                    """))
            .source("""
                main(stdin) { "ok" }
                """)
            .expect(
                miscompilation("import collision")
                    .in(namespace("a/b"))
                    .set("imports", list("x/y/z", "x/y/z"))
                    .set("variable", variable("z")),
                miscompilation("import collision")
                    .in(namespace("a/c"))
                    .set("imports", list("x/y/v", "x/y/v"))
                    .set("variable", variable("v"))));
  }

  private static Test reportsMultipleMiscompilations() {
    return suite("multiple miscompilations")
        .add(programTest("in same file")
            .imports("!\n@\n#\n")
            .source("""
                main(stdin) { "ok" }
                """)
            .expect(
                miscompilation("malformed import")
                    .in(root)
                    .set("import", "!"),
                miscompilation("malformed import")
                    .in(root)
                    .set("import", "@"),
                miscompilation("malformed import")
                    .in(root)
                    .set("import", "#")))
        .add(programTest("in different files")
            .add(path("a")
                .imports("!"))
            .add(path("b")
                .imports("@"))
            .add(path("c")
                .imports("#"))
            .source("""
                main(stdin) { "ok" }
                """)
            .expect(
                miscompilation("malformed import")
                    .in(namespace(single("a")))
                    .set("import", "!"),
                miscompilation("malformed import")
                    .in(namespace(single("b")))
                    .set("import", "@"),
                miscompilation("malformed import")
                    .in(namespace(single("c")))
                    .set("import", "#")));
  }

  private static ProgramTest programTest(String name) {
    return minimalProgramTest(name);
  }
}
