package com.mikosik.stork.compile;

import static com.mikosik.stork.common.Collections.toMapFromEntries;
import static com.mikosik.stork.common.Strings.split;
import static com.mikosik.stork.common.Throwables.runtimeException;
import static com.mikosik.stork.common.io.Ascii.isAlphanumeric;
import static com.mikosik.stork.common.io.Input.input;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Namespace.namespace;
import static com.mikosik.stork.model.Variable.variable;
import static com.mikosik.stork.model.change.Changes.deep;
import static com.mikosik.stork.model.change.Changes.ifVariable;
import static com.mikosik.stork.model.change.Changes.onBody;
import static com.mikosik.stork.problem.compile.CompilerException.exception;
import static com.mikosik.stork.problem.compile.importing.IllegalCharacter.illegalCharacter;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.util.Map.entry;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.mikosik.stork.common.func.Functions.Fab;
import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Namespace;
import com.mikosik.stork.model.StorkDirectory;
import com.mikosik.stork.model.Variable;

public class Importer {
  private final Map<Namespace, Map<Variable, Identifier>> imports;

  private Importer(Map<Namespace, Map<Variable, Identifier>> imports) {
    this.imports = imports;
  }

  public static Importer importer(List<StorkDirectory> directories) {
    var imports = directories.stream()
        .map(directory -> entry(
            directory.namespace,
            parseImports(directory.importFile.content)))
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
        throw exception(illegalCharacter(line, (byte) character));
      }
    });

    var split = line.split(" ");
    if (split.length == 1) {
      var identifier = identifierParse(split[0]);
      return entry(identifier.variable, identifier);
    } else if (split.length == 2) {
      return entry(variable(split[1]), identifierParse(split[0]));
    } else {
      throw runtimeException("illegal import line: %s", line);
    }
  }

  public static Identifier identifierParse(String name) {
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
