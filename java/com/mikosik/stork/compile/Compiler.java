package com.mikosik.stork.compile;

import static com.mikosik.stork.common.Collections.each;
import static com.mikosik.stork.common.Collections.toMapIgnoringDuplicates;
import static com.mikosik.stork.common.ImmutableList.join;
import static com.mikosik.stork.common.func.On.on;
import static com.mikosik.stork.compile.Importer.importer;
import static com.mikosik.stork.compile.link.Bridge.stork;
import static com.mikosik.stork.compile.link.Unlambda.unlambda;
import static com.mikosik.stork.compile.link.VerifyLibrary.findLinkingProblems;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.change.Changes.deep;
import static com.mikosik.stork.model.change.Changes.ifLambda;
import static com.mikosik.stork.model.change.Changes.ifQuote;
import static com.mikosik.stork.model.change.Changes.ifVariable;
import static com.mikosik.stork.model.change.Changes.onBody;
import static com.mikosik.stork.model.change.Changes.onIdentifier;
import static com.mikosik.stork.problem.compile.CompilerException.exception;

import java.util.List;

import com.mikosik.stork.common.Collections;
import com.mikosik.stork.common.func.Functions.Faa;
import com.mikosik.stork.compile.parse.Parser;
import com.mikosik.stork.compile.tokenize.Tokenizer;
import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Namespace;
import com.mikosik.stork.model.StorkDirectory;

public class Compiler {
  public static List<Definition> compile(Codebase codebase) {
    return verify(join(
        compile(codebase.directories),
        codebase.dependencies));
  }

  private static List<Definition> verify(List<Definition> definitions) {
    var linkingProblems = findLinkingProblems(definitions);
    if (linkingProblems.isEmpty()) {
      return definitions;
    } else {
      // TODO aggregate compiler problems
      throw exception(linkingProblems.getFirst());
    }
  }

  private static List<Definition> compile(List<StorkDirectory> directories) {
    // TODO aggregate compiler problems from stream
    var compiled = directories.stream()
        .map(directory -> on(directory.sourceFile.content)
            .map(Collections::iterator)
            .map(Tokenizer::tokenize)
            .map(Parser::parse)
            .map(each(onBody(deep(ifLambda(lambda -> on(lambda)
                .map(deep(ifVariable(variable -> variable.name.equals(lambda.parameter.name)
                    ? lambda.parameter
                    : variable)))
                .get())))))
            .map(bind(directory.namespace))
            .get())
        .flatMap(List::stream)
        .map(onBody(unlambda))
        .map(onBody(deep(ifQuote(quote -> stork(quote.string)))))
        .toList();

    var importer = importer(directories);
    return compiled.stream()
        .map(importer::injectInto)
        .toList();
  }

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
