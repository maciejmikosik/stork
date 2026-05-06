package com.mikosik.stork.compile;

import static com.mikosik.stork.common.ImmutableList.join;
import static com.mikosik.stork.common.ImmutableList.single;
import static com.mikosik.stork.common.Logic.constant;
import static com.mikosik.stork.common.io.Input.input;
import static com.mikosik.stork.compile.Compiled.compiled;
import static com.mikosik.stork.compile.Importer.importer;
import static com.mikosik.stork.compile.link.Bind.bindLambdaParameter;
import static com.mikosik.stork.compile.link.Unlambda.unlambda;
import static com.mikosik.stork.compile.link.Unquote.unquote;
import static com.mikosik.stork.compile.link.VerifyLibrary.findLinkingProblems;
import static com.mikosik.stork.compile.parse.Parser.parse;
import static com.mikosik.stork.compile.tokenize.Tokenizer.tokenize;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Source.Kind.CODE;
import static com.mikosik.stork.model.change.Changes.deep;
import static com.mikosik.stork.model.change.Changes.ifVariable;
import static com.mikosik.stork.model.change.Changes.onBody;
import static com.mikosik.stork.model.change.Changes.onIdentifier;
import static com.mikosik.stork.model.change.Changes.onNamespace;
import static java.util.stream.Collectors.toSet;

import java.util.List;

import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Source;
import com.mikosik.stork.problem.compile.CannotCompile;

public class Compiler {
  public static Compiled<List<Definition>> compile(Compilation compilation) {
    return compileSources(compilation)
        .thenTry(Compiler::verify);
  }

  public static Compiled<List<Definition>> verify(List<Definition> definitions) {
    var linkingProblems = findLinkingProblems(definitions);
    return linkingProblems.isEmpty()
        ? compiled(definitions)
        : compiled(linkingProblems);
  }

  public static Compiled<List<Definition>> compileSources(Compilation compilation) {
    try {
      var compiled = compilation.sources.stream()
          .filter(source -> source.kind == CODE)
          .map(Compiler::compile)
          .map(Compiled::getOrThrow)
          .flatMap(List::stream)
          .toList();

      var importer = importer(compilation.sources);
      var linked = join(compiled, compilation.definitions).stream()
          .map(importer::injectInto)
          .toList();
      return compiled(linked);
    } catch (CannotCompile problem) {
      return compiled(single(problem));
    }
  }

  private static Compiled<List<Definition>> compile(Source source) {
    return compileCode(source.content)
        .then(definitions -> {
          var exported = definitions.stream()
              .map(definition -> definition.identifier.variable)
              .collect(toSet());
          return definitions.stream()
              // TODO test that order is ensured: lambda, local, import
              .map(onBody(deep(bindLambdaParameter)))
              .map(onBody(deep(ifVariable(variable -> exported.contains(variable)
                  ? identifier(source.namespace, variable)
                  : variable))))
              .map(onIdentifier(onNamespace(constant(source.namespace))))
              // TODO inline compilation helpers
              .map(onBody(deep(unlambda)))
              .map(onBody(deep(unquote)))
              .toList();
        });
  }

  private static Compiled<List<Definition>> compileCode(byte[] content) {
    try {
      // TODO common for converting byte[] -> Iterator<Byte>
      return compiled(parse(tokenize(input(content).iterator())));
    } catch (CannotCompile problem) {
      return compiled(problem);
    }
  }
}
