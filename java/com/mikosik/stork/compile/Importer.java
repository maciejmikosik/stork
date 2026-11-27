package com.mikosik.stork.compile;

import static com.mikosik.stork.common.col.Collections.functionFrom;
import static com.mikosik.stork.common.col.Streamer.streamer;
import static com.mikosik.stork.common.func.On.on;
import static com.mikosik.stork.common.text.Strings.split;
import static com.mikosik.stork.compile.Patterns.IMPORT_LINE;
import static com.mikosik.stork.compile.err.CompilerException.verifyNo;
import static com.mikosik.stork.compile.err.Context.in;
import static com.mikosik.stork.compile.err.Miscompilations.importCollision;
import static com.mikosik.stork.compile.err.Miscompilations.malformedImportLine;
import static com.mikosik.stork.model.exp.Changes.deep;
import static com.mikosik.stork.model.exp.Changes.ifVariable;
import static com.mikosik.stork.model.exp.Changes.onBody;
import static com.mikosik.stork.model.exp.Namespace.namespace;
import static com.mikosik.stork.model.exp.Reference.reference;
import static com.mikosik.stork.model.exp.Variable.variable;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.util.Map.entry;
import static java.util.stream.Collectors.groupingBy;

import java.util.List;

import com.mikosik.stork.common.func.Functions.Fab;
import com.mikosik.stork.compile.err.CompilerException;
import com.mikosik.stork.compile.err.Miscompilation;
import com.mikosik.stork.model.disk.StorkDirectory;
import com.mikosik.stork.model.exp.Definition;
import com.mikosik.stork.model.exp.Expression;
import com.mikosik.stork.model.exp.Namespace;
import com.mikosik.stork.model.exp.Reference;
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
            in(directory.namespace).run(() -> parse(directory.importFile))))
        .apply(CompilerException::gatherMiscompilations)
        .toListAndApply(entries -> new Importer(functionFrom(
            entries,
            namespace -> variable -> variable)));
  }

  private static Fab<Variable, Expression> parse(byte[] source) {
    var lines = parseLines(source);
    verifyNo(findImportCollisions(lines));
    return asMappingFunction(lines);
  }

  private static Fab<Variable, Expression> asMappingFunction(List<Line> importLines) {
    return streamer(importLines)
        .map(line -> entry(line.variable, (Expression) line.reference))
        .toListAndApply(entries -> functionFrom(
            entries,
            variable -> variable));
  }

  private static List<Line> parseLines(byte[] source) {
    var lines = new String(source, US_ASCII).lines().toList();
    verifyNo(findMalformedImports(lines));
    return streamer(lines)
        .map(Importer::parse)
        .toList();
  }

  private static Line parse(String line) {
    var tokens = line.split(" ");
    var reference = referenceParse(tokens[0]);
    var variable = tokens.length == 2
        ? variable(tokens[1])
        : reference.variable;
    return new Line(line, reference, variable);
  }

  private static Reference referenceParse(String name) {
    var components = split("/", name);
    return reference(
        namespace(components.subList(0, components.size() - 1)),
        variable(components.getLast()));
  }

  public Definition injectInto(Definition definition) {
    return on(definition)
        .apply(onBody(deep(ifVariable(variable -> mapping
            .apply(definition.reference.namespace)
            .apply(variable)))));
  }

  private static List<Miscompilation> findMalformedImports(List<String> lines) {
    return streamer(lines)
        .filter(line -> !line.matches(IMPORT_LINE))
        .map(line -> malformedImportLine()
            .set("import", line)
            .build())
        .toList();
  }

  private static List<Miscompilation> findImportCollisions(List<Line> lines) {
    var linesByVariable = lines.stream()
        .collect(groupingBy(line -> line.variable));
    return streamer(linesByVariable.entrySet())
        .filter(entry -> entry.getValue().size() > 1)
        .map(entry -> importCollision()
            .set("imports", entry.getValue().stream()
                .map(line -> line.source)
                .toList())
            .set("variable", entry.getKey())
            .build())
        .toList();
  }

  private static class Line {
    public final String source;
    public final Reference reference;
    public final Variable variable;

    private Line(
        String source,
        Reference reference,
        Variable variable) {
      this.source = source;
      this.reference = reference;
      this.variable = variable;
    }
  }
}
