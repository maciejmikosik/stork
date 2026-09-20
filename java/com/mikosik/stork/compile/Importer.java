package com.mikosik.stork.compile;

import static com.mikosik.stork.common.Collections.functionFrom;
import static com.mikosik.stork.common.Streamer.streamer;
import static com.mikosik.stork.common.Strings.split;
import static com.mikosik.stork.common.func.On.on;
import static com.mikosik.stork.compile.Patterns.IMPORT_LINE;
import static com.mikosik.stork.model.exp.Changes.deep;
import static com.mikosik.stork.model.exp.Changes.ifVariable;
import static com.mikosik.stork.model.exp.Changes.onBody;
import static com.mikosik.stork.model.exp.Identifier.identifier;
import static com.mikosik.stork.model.exp.Namespace.namespace;
import static com.mikosik.stork.model.exp.Variable.variable;
import static com.mikosik.stork.problem.compile.CompilerException.exception;
import static com.mikosik.stork.problem.compile.importing.MalformedImportFile.malformedImportFile;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.util.Map.entry;

import java.util.List;
import java.util.Map.Entry;

import com.mikosik.stork.common.func.Functions.Fab;
import com.mikosik.stork.model.disk.StorkDirectory;
import com.mikosik.stork.model.exp.Definition;
import com.mikosik.stork.model.exp.Expression;
import com.mikosik.stork.model.exp.Identifier;
import com.mikosik.stork.model.exp.Namespace;
import com.mikosik.stork.model.exp.Variable;
import com.mikosik.stork.problem.compile.CompilerException;
import com.mikosik.stork.problem.compile.importing.MalformedImportLine;

public class Importer {
  private final Fab<Namespace, Fab<Variable, Expression>> mapping;

  private Importer(Fab<Namespace, Fab<Variable, Expression>> mapping) {
    this.mapping = mapping;
  }

  public static Importer buildImporter(List<StorkDirectory> directories) {
    return streamer(directories)
        .map(directory -> entry(
            directory.namespace,
            parseImportFile(directory)))
        .apply(CompilerException::gatherCompilerProblems)
        .toListAndApply(entries -> new Importer(functionFrom(
            entries,
            namespace -> variable -> variable)));
  }

  private static Fab<Variable, Expression> parseImportFile(StorkDirectory directory) {
    var lines = new String(directory.importFile, US_ASCII).lines().toList();
    streamer(lines)
        .filter(line -> !line.matches(IMPORT_LINE))
        .map(MalformedImportLine::malformedImportLine)
        .toListAndConsume(problems -> {
          if (!problems.isEmpty()) {
            throw exception(malformedImportFile(directory.namespace, problems));
          }
        });
    return streamer(lines)
        .map(Importer::parse)
        .toListAndApply(entries -> functionFrom(
            entries,
            variable -> variable));
  }

  private static Entry<Variable, Expression> parse(String line) {
    var tokens = line.split(" ");
    var identifier = identifierParse(tokens[0]);
    var variable = tokens.length == 2
        ? variable(tokens[1])
        : identifier.variable;
    return entry(variable, identifier);
  }

  private static Identifier identifierParse(String name) {
    var components = split("/", name);
    return identifier(
        namespace(components.subList(0, components.size() - 1)),
        variable(components.getLast()));
  }

  public Definition injectInto(Definition definition) {
    return on(definition)
        .apply(onBody(deep(ifVariable(variable -> mapping
            .apply(definition.identifier.namespace)
            .apply(variable)))));
  }
}
