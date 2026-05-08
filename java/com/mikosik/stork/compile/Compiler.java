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
import java.util.function.Function;

import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Namespace;
import com.mikosik.stork.problem.compile.CannotCompile;

public class Compiler {
  public static Compiled<List<Definition>> compile(Compilation compilation) {
    return compileSources(compilation)
        .thenTry(Compiler::verify);
  }

  private static Compiled<List<Definition>> verify(List<Definition> definitions) {
    var linkingProblems = findLinkingProblems(definitions);
    return linkingProblems.isEmpty()
        ? compiled(definitions)
        : compiled(linkingProblems);
  }

  private static Compiled<List<Definition>> compileSources(Compilation compilation) {
    try {
      var compiled = compilation.sources.stream()
          .filter(source -> source.kind == CODE)
          .map(source -> compile(source.content)
              .then(makeComputable(source.namespace))
              .getOrThrow())
          .flatMap(List::stream)
          .toList();

      var importer = importer(compilation.sources).getOrThrow();
      var linked = compiled.stream()
          .map(importer::injectInto)
          .toList();
      return compiled(join(linked, compilation.definitions));
    } catch (CannotCompile problem) {
      return compiled(single(problem));
    }
  }

  private static Function<List<Definition>, List<Definition>> makeComputable(
      Namespace namespace) {
    return definitions -> definitions.stream()
        // TODO test that order is ensured: lambda, local, import
        .map(onBody(deep(bindLambdaParameter)))
        .map(onBody(deep(bindLocalFunctions(namespace, definitions))))
        .map(onIdentifier(onNamespace(constant(namespace))))
        // TODO inline compilation helpers
        .map(onBody(deep(unlambda)))
        .map(onBody(deep(unquote)))
        // TODO onBody(deep(...)) should be enforced on lower level
        .toList();
  }

  private static Function<Expression, Expression> bindLocalFunctions(
      Namespace namespace,
      List<Definition> definitions) {
    var localFunctions = definitions.stream()
        .map(definition -> definition.identifier.variable)
        .collect(toSet());
    return ifVariable(variable -> localFunctions.contains(variable)
        ? identifier(namespace, variable)
        : variable);
  }

  private static Compiled<List<Definition>> compile(byte[] content) {
    try {
      // TODO common for converting byte[] -> Iterator<Byte>
      return compiled(parse(tokenize(input(content).iterator())));
    } catch (CannotCompile problem) {
      return compiled(problem);
    }
  }
}
