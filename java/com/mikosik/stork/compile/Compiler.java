package com.mikosik.stork.compile;

import static com.mikosik.stork.common.ImmutableList.join;
import static com.mikosik.stork.common.Logic.constant;
import static com.mikosik.stork.common.io.Input.input;
import static com.mikosik.stork.compile.Importer.importer;
import static com.mikosik.stork.compile.link.Bind.bindLambdaParameter;
import static com.mikosik.stork.compile.link.Unlambda.unlambda;
import static com.mikosik.stork.compile.link.Unquote.unquote;
import static com.mikosik.stork.compile.link.VerifyLibrary.verify;
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

public class Compiler {
  public static List<Definition> compile(Compilation compilation) {
    var compiled = compilation.sources.stream()
        .filter(source -> source.kind == CODE)
        .map(Compiler::compile)
        .flatMap(List::stream)
        .toList();

    var importer = importer(compilation.sources);
    var linked = join(compiled, compilation.definitions).stream()
        .map(importer::injectInto)
        .toList();

    return verify(linked);
  }

  private static List<Definition> compile(Source source) {
    var library = compileCode(source.content);
    var exported = library.stream()
        .map(definition -> definition.identifier.variable)
        .collect(toSet());
    return library.stream()
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
  }

  private static List<Definition> compileCode(byte[] content) {
    // TODO common for converting byte[] -> Iterator<Byte>
    return parse(tokenize(input(content).iterator()));
  }
}
