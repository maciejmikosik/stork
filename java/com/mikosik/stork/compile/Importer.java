package com.mikosik.stork.compile;

import static com.mikosik.stork.common.Collections.mapFrom;
import static com.mikosik.stork.common.Streamer.streamer;
import static com.mikosik.stork.common.Strings.split;
import static com.mikosik.stork.common.io.Ascii.isAlphanumeric;
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
import java.util.Map;
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
  private final Map<Namespace, Map<Variable, Identifier>> imports;

  private Importer(Map<Namespace, Map<Variable, Identifier>> imports) {
    this.imports = imports;
  }

  public static Importer buildImporter(List<StorkDirectory> directories) {
    return streamer(directories)
        .map(directory -> entry(
            directory.namespace,
            parseImportFile(directory)))
        .apply(CompilerException::gatherCompilerProblems)
        .apply(streamer -> new Importer(mapFrom(streamer.toList())));
  }

  private static Map<Variable, Identifier> parseImportFile(StorkDirectory directory) {
    var lines = new String(directory.importFile, US_ASCII).lines().toList();
    var problems = streamer(lines)
        .filter(Importer::isMalformed)
        .map(MalformedImportLine::malformedImportLine)
        .toList();
    if (problems.isEmpty()) {
      return streamer(lines)
          .map(Importer::parse)
          .apply(streamer -> mapFrom(streamer.toList()));
    } else {
      throw exception(malformedImportFile(directory.namespace, problems));
    }
  }

  private static boolean isMalformed(String line) {
    for (char character : line.toCharArray()) {
      if (!(isAlphanumeric((byte) character)
          || character == '/'
          || character == ' ')) {
        return true;
      }
    }
    var split = line.split(" ");
    return split.length < 1 || 2 < split.length;
  }

  private static Entry<Variable, Identifier> parse(String line) {
    var split = line.split(" ");
    if (split.length == 1) {
      var identifier = identifierParse(split[0]);
      return entry(identifier.variable, identifier);
    } else if (split.length == 2) {
      return entry(variable(split[1]), identifierParse(split[0]));
    } else {
      throw new RuntimeException("should be validated");
    }
  }

  private static Identifier identifierParse(String name) {
    var components = split("/", name);
    return identifier(
        namespace(components.subList(0, components.size() - 1)),
        variable(components.getLast()));
  }

  public Definition injectInto(Definition definition) {
    return onBody(
        deep(ifVariable(importsFor(definition.identifier.namespace))))
            .apply(definition);
  }

  private Fab<Variable, Expression> importsFor(Namespace namespace) {
    if (imports.containsKey(namespace)) {
      var namespaceImports = imports.get(namespace);
      return variable -> {
        if (namespaceImports.containsKey(variable)) {
          return namespaceImports.get(variable);
        }
        return variable;
      };
    } else {
      return variable -> variable;
    }
  }
}
