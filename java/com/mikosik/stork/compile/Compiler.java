package com.mikosik.stork.compile;

import static com.mikosik.stork.common.Collections.iterator;
import static com.mikosik.stork.common.ImmutableList.join;
import static com.mikosik.stork.common.ImmutableList.single;
import static com.mikosik.stork.common.Logic.constant;
import static com.mikosik.stork.compile.Compiled.compiled;
import static com.mikosik.stork.compile.Importer.importer;
import static com.mikosik.stork.compile.link.Bridge.stork;
import static com.mikosik.stork.compile.link.Unlambda.unlambda;
import static com.mikosik.stork.compile.link.VerifyLibrary.findLinkingProblems;
import static com.mikosik.stork.compile.parse.Parser.parse;
import static com.mikosik.stork.compile.tokenize.Tokenizer.tokenize;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.change.Changes.deep;
import static com.mikosik.stork.model.change.Changes.ifLambda;
import static com.mikosik.stork.model.change.Changes.ifQuote;
import static com.mikosik.stork.model.change.Changes.ifVariable;
import static com.mikosik.stork.model.change.Changes.onBody;
import static com.mikosik.stork.model.change.Changes.onIdentifier;
import static com.mikosik.stork.model.change.Changes.onNamespace;
import static java.util.stream.Collectors.toSet;

import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Namespace;
import com.mikosik.stork.model.StorkFile;
import com.mikosik.stork.model.StorkFile.ImportFile;
import com.mikosik.stork.model.StorkFile.SourceFile;
import com.mikosik.stork.problem.compile.CannotCompile;

public class Compiler {
  public static List<Definition> compile(Codebase codebase) {
    var filesFromDirectories = codebase.directories.stream()
        .flatMap(directory -> Stream.of(
            directory.importFile,
            directory.sourceFile))
        .toList();
    return compile(join(codebase.files, filesFromDirectories))
        .then(definitions -> join(definitions, codebase.dependencies))
        .thenTry(Compiler::verify)
        .getOrThrow();
  }

  private static Compiled<List<Definition>> verify(List<Definition> definitions) {
    var linkingProblems = findLinkingProblems(definitions);
    return linkingProblems.isEmpty()
        ? compiled(definitions)
        : compiled(linkingProblems);
  }

  private static Compiled<List<Definition>> compile(List<StorkFile> storkFiles) {
    try {
      var compiled = storkFiles.stream()
          .filter(SourceFile.class::isInstance)
          .map(SourceFile.class::cast)
          .map(sourceFile -> compile(sourceFile.content)
              .then(makeComputable(sourceFile.namespace))
              .getOrThrow())
          .flatMap(List::stream)
          .toList();

      var importFiles = storkFiles.stream()
          .filter(ImportFile.class::isInstance)
          .map(ImportFile.class::cast)
          .toList();
      var importer = importer(importFiles).getOrThrow();
      var linked = compiled.stream()
          .map(importer::injectInto)
          .toList();
      return compiled(linked);
    } catch (CannotCompile problem) {
      return compiled(single(problem));
    }
  }

  private static UnaryOperator<List<Definition>> makeComputable(
      Namespace namespace) {
    return definitions -> definitions.stream()
        // TODO test that order is ensured: lambda, local, import
        .map(onBody(bindLambdaParameters))
        .map(onBody(bindLocalFunctions(namespace, definitions)))
        .map(onIdentifier(onNamespace(constant(namespace))))
        .map(onBody(unlambda))
        .map(onBody(deep(ifQuote(quote -> stork(quote.string)))))
        .toList();
  }

  private static final UnaryOperator<Expression> bindLambdaParameters = deep(
      ifLambda(lambda -> deep(ifVariable(variable -> {
        return variable.name.equals(lambda.parameter.name)
            ? lambda.parameter
            : variable;
      })).apply(lambda)));

  private static UnaryOperator<Expression> bindLocalFunctions(
      Namespace namespace,
      List<Definition> definitions) {
    var localFunctions = definitions.stream()
        .map(definition -> definition.identifier.variable)
        .collect(toSet());
    return deep(ifVariable(variable -> localFunctions.contains(variable)
        ? identifier(namespace, variable)
        : variable));
  }

  private static Compiled<List<Definition>> compile(byte[] content) {
    try {
      return compiled(parse(tokenize(iterator(content))));
    } catch (CannotCompile problem) {
      return compiled(problem);
    }
  }
}
