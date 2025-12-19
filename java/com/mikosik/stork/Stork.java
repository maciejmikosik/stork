package com.mikosik.stork;

import static com.mikosik.stork.Core.core;
import static com.mikosik.stork.Core.Mode.PRODUCTION;
import static com.mikosik.stork.common.io.Directories.workingDirectory;
import static com.mikosik.stork.common.io.Input.input;
import static com.mikosik.stork.common.io.Output.output;
import static com.mikosik.stork.compile.Compilation.compilation;
import static com.mikosik.stork.compile.Compiler.compile;
import static com.mikosik.stork.compile.Compiler.makeComputable;
import static com.mikosik.stork.compile.link.Bind.bindLambdaParameter;
import static com.mikosik.stork.compile.parse.Parser.parse;
import static com.mikosik.stork.compile.tokenize.Tokenizer.tokenize;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.change.Changes.deep;
import static com.mikosik.stork.model.change.Changes.ifVariable;
import static com.mikosik.stork.model.change.Changes.onBody;
import static com.mikosik.stork.model.change.Changes.onEachDefinition;
import static com.mikosik.stork.program.Program.program;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.ByteArrayInputStream;

import com.mikosik.stork.common.io.Directory;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Library;
import com.mikosik.stork.problem.Problem;

public class Stork {
  public static void main(String[] args) {
    Expression main = null;
    Library library = null;
    Directory temporaryDirectory = null;
    try {
      if (args.length == 0) {
        main = identifier("main");
        library = compile(compilation()
            .source(workingDirectory())
            .library(core(PRODUCTION)));
      } else if (args.length == 1) {
        var code = "MAINFROMARGUMENT{%s}".formatted(args[0]);
        main = identifier("MAINFROMARGUMENT");
        var selfBuild = onEachDefinition(onBody(deep(bindLambdaParameter)))
            .andThen(onEachDefinition(onBody(deep(ifVariable(Identifier::identifier)))));
        Library mainLibrary = makeComputable(selfBuild.apply(parse(tokenize(
            input(new ByteArrayInputStream(code.getBytes(UTF_8))).iterator()))));
        library = compile(compilation()
            .source(workingDirectory())
            .library(core(PRODUCTION))
            .library(mainLibrary));
      } else {
        System.err.println("too many arguments");
        System.exit(1);
      }

      program(main, library)
          .run(input(System.in), output(System.out));
      System.exit(0);
    } catch (Problem problem) {
      System.err.println(problem);
      System.exit(1);
    } finally {
      if (temporaryDirectory != null) {
        temporaryDirectory.delete();
      }
    }
  }
}
