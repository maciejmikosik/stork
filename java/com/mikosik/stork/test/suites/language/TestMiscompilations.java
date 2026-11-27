package com.mikosik.stork.test.suites.language;

import static com.mikosik.stork.compile.err.Miscompilation.miscompilation;
import static com.mikosik.stork.model.exp.Namespace.root;
import static com.mikosik.stork.model.exp.Variable.variable;
import static com.mikosik.stork.model.token.Punctuator.DOT;
import static com.mikosik.stork.test.Factories.namespace;
import static com.mikosik.stork.test.ProgramTest.minimalProgramTest;
import static com.mikosik.stork.test.build.StorkDirectoryBuilder.path;
import static org.quackery.Suite.suite;

import org.quackery.Test;

import com.mikosik.stork.test.ProgramTest;

public class TestMiscompilations {
  public static Test testCompilerMiscompilations() {
    return suite("compiler")
        .add(aggregatesAllMiscompilations());
  }

  private static Test aggregatesAllMiscompilations() {
    return programTest("aggregates all miscompilations")
        .add(path()
            .imports("x xx xxx")
            .source("main(stdin) { ! }"))
        .add(path("a/aa")
            .imports("y yy yyy")
            .source("func ."))
        .expect(
            miscompilation("malformed import")
                .in(root)
                .set("import", "x xx xxx"),
            miscompilation("illegal character in code")
                .in(root)
                .in(variable("main"))
                .set("character", (byte) '!'),
            miscompilation("malformed import")
                .in(namespace("a/aa"))
                .set("import", "y yy yyy"),
            miscompilation("unexpected token")
                .in(namespace("a/aa"))
                .in(variable("func"))
                .set("token", DOT));
  }

  private static ProgramTest programTest(String name) {
    return minimalProgramTest(name);
  }
}
