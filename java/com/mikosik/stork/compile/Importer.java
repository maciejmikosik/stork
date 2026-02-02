package com.mikosik.stork.compile;

import static com.mikosik.stork.common.Collections.toMapFromEntries;
import static com.mikosik.stork.common.Throwables.runtimeException;
import static com.mikosik.stork.common.io.Ascii.isAlphanumeric;
import static com.mikosik.stork.common.io.Input.input;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Source.Kind.IMPORT;
import static com.mikosik.stork.model.Variable.variable;
import static com.mikosik.stork.problem.compile.importing.IllegalCharacter.illegalCharacter;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.util.Map.entry;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Namespace;
import com.mikosik.stork.model.Source;
import com.mikosik.stork.model.Variable;

public class Importer {
  private final Map<Namespace, Map<Variable, Identifier>> imports;

  private Importer(Map<Namespace, Map<Variable, Identifier>> imports) {
    this.imports = imports;
  }

  public static Importer importer(List<Source> sources) {
    var imports = sources.stream()
        .filter(source -> source.kind == IMPORT)
        .map(importSource -> entry(
            importSource.namespace,
            parseImports(importSource.content)))
        .collect(toMapFromEntries());
    return new Importer(imports);
  }

  private static Map<Variable, Identifier> parseImports(byte[] content) {
    return input(content).bufferedReader(US_ASCII).lines()
        .map(line -> parseImport(line.trim()))
        .collect(toMapFromEntries());
  }

  private static Entry<Variable, Identifier> parseImport(String line) {
    line.chars().forEach(character -> {
      if (!(isAlphanumeric((byte) character)
          || character == '/'
          || character == ' ')) {
        throw illegalCharacter(line, (byte) character);
      }
    });

    var split = line.split(" ");
    if (split.length == 1) {
      var identifier = identifier(split[0]);
      return entry(identifier.variable, identifier);
    } else if (split.length == 2) {
      return entry(variable(split[1]), identifier(split[0]));
    } else {
      throw runtimeException("illegal import line: %s", line);
    }
  }

  public Function<Variable, Expression> importsFor(Namespace namespace) {
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
