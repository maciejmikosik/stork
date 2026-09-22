package com.mikosik.stork.compile;

import static com.mikosik.stork.common.col.Collections.functionFrom;
import static com.mikosik.stork.common.col.Streamer.streamer;
import static com.mikosik.stork.common.func.On.on;
import static com.mikosik.stork.common.text.Strings.split;
import static com.mikosik.stork.compile.Patterns.IMPORT_LINE;
import static com.mikosik.stork.compile.err.Problems.importCollision;
import static com.mikosik.stork.compile.err.Problems.malformedImportLine;
import static com.mikosik.stork.model.exp.Changes.deep;
import static com.mikosik.stork.model.exp.Changes.ifVariable;
import static com.mikosik.stork.model.exp.Changes.onBody;
import static com.mikosik.stork.model.exp.Identifier.identifier;
import static com.mikosik.stork.model.exp.Namespace.namespace;
import static com.mikosik.stork.model.exp.Variable.variable;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.util.Map.entry;
import static java.util.stream.Collectors.groupingBy;

import java.util.List;

import com.mikosik.stork.common.func.Functions.Fab;
import com.mikosik.stork.compile.err.CompilerException;
import com.mikosik.stork.model.disk.StorkDirectory;
import com.mikosik.stork.model.exp.Definition;
import com.mikosik.stork.model.exp.Expression;
import com.mikosik.stork.model.exp.Identifier;
import com.mikosik.stork.model.exp.Namespace;
import com.mikosik.stork.model.exp.Variable;

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
    var lines = parseImportLines(directory);
    var linesByVariable = lines.stream()
        .collect(groupingBy(line -> line.variable));
    streamer(linesByVariable.entrySet())
        .filter(entry -> entry.getValue().size() > 1)
        .map(entry -> importCollision()
            .location(directory.namespace)
            .object(entry.getValue().stream()
                .map(line -> line.source)
                .toList())
            .variable(entry.getKey())
            .build())
        .toListAndConsume(CompilerException::verifyNoProblems);
    return asMappingFunction(lines);
  }

  private static Fab<Variable, Expression> asMappingFunction(List<Line> importLines) {
    return streamer(importLines)
        .map(line -> entry(line.variable, (Expression) line.identifier))
        .toListAndApply(entries -> functionFrom(
            entries,
            variable -> variable));
  }

  private static List<Line> parseImportLines(StorkDirectory directory) {
    var lines = new String(directory.importFile, US_ASCII).lines().toList();
    streamer(lines)
        .filter(line -> !line.matches(IMPORT_LINE))
        .map(line -> malformedImportLine()
            .location(directory.namespace)
            .object(line)
            .build())
        .toListAndConsume(CompilerException::verifyNoProblems);
    return streamer(lines)
        .map(Importer::parse)
        .toList();
  }

  private static Line parse(String line) {
    var tokens = line.split(" ");
    var identifier = identifierParse(tokens[0]);
    var variable = tokens.length == 2
        ? variable(tokens[1])
        : identifier.variable;
    return new Line(line, identifier, variable);
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

  private static class Line {
    public final String source;
    public final Identifier identifier;
    public final Variable variable;

    private Line(
        String source,
        Identifier identifier,
        Variable variable) {
      this.source = source;
      this.identifier = identifier;
      this.variable = variable;
    }
  }
}
