package com.mikosik.stork.compile;

import static com.mikosik.stork.common.Collections.each;
import static com.mikosik.stork.common.Collections.toMapIgnoringDuplicates;
import static com.mikosik.stork.common.ImmutableList.cast;
import static com.mikosik.stork.common.ImmutableList.join;
import static com.mikosik.stork.common.ImmutableList.none;
import static com.mikosik.stork.common.Result.combine;
import static com.mikosik.stork.common.Result.Failure.failure;
import static com.mikosik.stork.common.Result.Success.success;
import static com.mikosik.stork.common.Streamer.streamer;
import static com.mikosik.stork.common.func.On.on;
import static com.mikosik.stork.compile.Bridge.stork;
import static com.mikosik.stork.compile.Importer.tryBuildImporter;
import static com.mikosik.stork.compile.Unlambda.unlambda;
import static com.mikosik.stork.compile.VerifyLibrary.findLinkingProblems;
import static com.mikosik.stork.model.exp.Changes.deep;
import static com.mikosik.stork.model.exp.Changes.ifLambda;
import static com.mikosik.stork.model.exp.Changes.ifQuote;
import static com.mikosik.stork.model.exp.Changes.ifVariable;
import static com.mikosik.stork.model.exp.Changes.onBody;
import static com.mikosik.stork.model.exp.Changes.onIdentifier;
import static com.mikosik.stork.model.exp.Identifier.identifier;
import static java.util.Objects.deepEquals;

import java.util.List;

import com.mikosik.stork.common.Collections;
import com.mikosik.stork.common.func.Functions.Faa;
import com.mikosik.stork.model.exp.Definition;
import com.mikosik.stork.model.exp.Expression;
import com.mikosik.stork.model.exp.Namespace;
import com.mikosik.stork.problem.compile.CompilerException;

public class Compiler {
  public static List<Definition> compile(Codebase codebase) {
    var triedDefinitions = streamer(codebase.directories)
        .map(directory -> on(directory.sourceFile)
            .map(Collections::iterator)
            .map(Tokenizer::tokenize)
            .map(Parser::tryParse)
            .apply()
            .mapSuccess(each(onBody(bindLambdaParameters)))
            .mapSuccess(bind(directory.namespace)))
        .apply(streamer -> combine(streamer.toList()))
        .mapSuccess(Collections::flatten)
        .mapFailure(Collections::flatten)
        .mapSuccess(each(onBody(unlambda)))
        .mapSuccess(each(onBody(deep(ifQuote(quote -> stork(quote.string))))));

    var triedImporter = tryBuildImporter(codebase.directories);

    return triedDefinitions
        .flatMapSuccess(definitions -> triedImporter.switcher(
            importer -> success(each(importer::injectInto).apply(definitions)),
            problems -> failure(none())))
        .mapFailure(compilerProblems -> triedImporter.switcher(
            importer -> compilerProblems,
            importerProblems -> join(compilerProblems, importerProblems)))
        .mapSuccess(definitions -> join(definitions, codebase.dependencies))
        .flatMapSuccess(definitions -> {
          var linkingProblems = findLinkingProblems(definitions);
          return linkingProblems.isEmpty()
              ? success(definitions)
              : failure(cast(linkingProblems));
        })
        .unwrap(CompilerException::exception);
  }

  private static final Faa<Expression> bindLambdaParameters = deep(
      ifLambda(lambda -> on(lambda)
          .apply(deep(ifVariable(variable -> deepEquals(
              variable.name,
              lambda.parameter.name)
                  ? lambda.parameter
                  : variable)))));

  private static Faa<List<Definition>> bind(Namespace namespace) {
    return definitions -> {
      var table = definitions.stream()
          .map(definition -> definition.identifier.variable)
          .collect(toMapIgnoringDuplicates(
              variable -> variable,
              variable -> identifier(namespace, variable)));

      return definitions.stream()
          .map(onIdentifier(identifier -> table.get(identifier.variable)))
          .map(onBody(deep(ifVariable(variable -> table.containsKey(variable)
              ? table.get(variable)
              : variable))))
          .toList();
    };
  }
}
