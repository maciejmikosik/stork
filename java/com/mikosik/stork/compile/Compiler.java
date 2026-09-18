package com.mikosik.stork.compile;

import static com.mikosik.stork.common.Collections.each;
import static com.mikosik.stork.common.Collections.toMapIgnoringDuplicates;
import static com.mikosik.stork.common.ImmutableList.join;
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
import static com.mikosik.stork.problem.compile.CompilerException.exception;
import static com.mikosik.stork.problem.compile.Gatherer.gatherer;
import static java.util.Objects.deepEquals;

import java.util.List;

import com.mikosik.stork.common.Collections;
import com.mikosik.stork.common.Streamer;
import com.mikosik.stork.common.func.Functions.Faa;
import com.mikosik.stork.model.disk.StorkDirectory;
import com.mikosik.stork.model.exp.Definition;
import com.mikosik.stork.model.exp.Expression;
import com.mikosik.stork.model.exp.Namespace;
import com.mikosik.stork.problem.compile.CompilerException;

public class Compiler {
  public static List<Definition> compile(Codebase codebase) {
    var gatherer = gatherer();
    var definitions = gatherer.gather(() -> compileSources(codebase.directories));
    var importer = gatherer.gather(() -> buildImporter(codebase.directories));
    gatherer.verify();

    return link(join(
        // TODO create test that detects missing injection
        streamer(definitions)
            .map(importer::injectInto)
            .toList(),
        codebase.dependencies));
  }

  private static List<Definition> compileSources(List<StorkDirectory> directories) {
    return streamer(directories)
        .map(Compiler::compileSource)
        .apply(CompilerException::gatherCompilerProblems)
        .map(Streamer::streamer)
        .apply(Streamer::flatten)
        .toList();
  }

  private static List<Definition> compileSource(StorkDirectory directory) {
    return on(directory.sourceFile)
        .map(Collections::iterator)
        .map(Tokenizer::tokenize)
        .map(Parser::tryParse)
        .map(result -> result.unwrap(CompilerException::exception))
        .map(each(onBody(bindLambdaParameters)))
        .map(bind(directory.namespace))
        .map(each(onBody(unlambda)))
        .map(each(onBody(deep(ifQuote(quote -> stork(quote.string))))))
        .apply();
  }

  private static Importer buildImporter(List<StorkDirectory> directories) {
    return tryBuildImporter(directories)
        .unwrap(CompilerException::exception);
  }

  private static List<Definition> link(List<Definition> definitions) {
    var problems = findLinkingProblems(definitions);
    if (problems.isEmpty()) {
      return definitions;
    } else {
      throw exception(problems);
    }
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
