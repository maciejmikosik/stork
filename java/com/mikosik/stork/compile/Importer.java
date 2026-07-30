package com.mikosik.stork.compile;

import static com.mikosik.stork.common.Result.combine;
import static com.mikosik.stork.common.Result.Failure.failure;
import static com.mikosik.stork.common.Result.Success.success;
import static com.mikosik.stork.common.Streamer.streamer;
import static com.mikosik.stork.common.Strings.split;
import static com.mikosik.stork.common.io.Ascii.isAlphanumeric;
import static com.mikosik.stork.model.exp.Changes.deep;
import static com.mikosik.stork.model.exp.Changes.ifVariable;
import static com.mikosik.stork.model.exp.Changes.onBody;
import static com.mikosik.stork.model.exp.Identifier.identifier;
import static com.mikosik.stork.model.exp.Namespace.namespace;
import static com.mikosik.stork.model.exp.Variable.variable;
import static com.mikosik.stork.problem.compile.importing.IllegalCharacterInImport.illegalCharacterInImport;
import static com.mikosik.stork.problem.compile.importing.MalformedImport.malformedImport;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.util.Map.entry;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.mikosik.stork.common.Collections;
import com.mikosik.stork.common.Result;
import com.mikosik.stork.common.func.Functions.Fab;
import com.mikosik.stork.model.disk.StorkDirectory;
import com.mikosik.stork.model.exp.Definition;
import com.mikosik.stork.model.exp.Expression;
import com.mikosik.stork.model.exp.Identifier;
import com.mikosik.stork.model.exp.Namespace;
import com.mikosik.stork.model.exp.Variable;
import com.mikosik.stork.problem.compile.CompilerException;
import com.mikosik.stork.problem.compile.importing.CannotImport;

public class Importer {
  private final Map<Namespace, Map<Variable, Identifier>> imports;

  private Importer(Map<Namespace, Map<Variable, Identifier>> imports) {
    this.imports = imports;
  }

  public static Importer importer(List<StorkDirectory> directories) {
    var importsMap = streamer(directories)
        .map(directory -> parseImports(directory.importFile)
            .mapSuccess(map -> entry(directory.namespace, map)))
        .apply(streamer -> combine(streamer.toList()))
        .mapSuccess(Collections::mapFrom)
        .mapFailure(Collections::flatten)
        .unwrap(CompilerException::exception);
    return new Importer(importsMap);
  }

  private static Result<Map<Variable, Identifier>, List<CannotImport>> parseImports(
      byte[] content) {
    return streamer(new String(content, US_ASCII).lines().toList())
        .map(String::trim)
        .map(Importer::parseImport)
        .apply(streamer -> combine(streamer.toList()))
        .mapSuccess(Collections::mapFrom);
  }

  private static Result<Entry<Variable, Identifier>, CannotImport> parseImport(
      String line) {
    for (char character : line.toCharArray()) {
      if (!(isAlphanumeric((byte) character)
          || character == '/'
          || character == ' ')) {
        return failure(illegalCharacterInImport(line, (byte) character));
      }
    }

    var split = line.split(" ");
    if (split.length == 1) {
      var identifier = identifierParse(split[0]);
      return success(entry(identifier.variable, identifier));
    } else if (split.length == 2) {
      return success(entry(variable(split[1]), identifierParse(split[0])));
    } else {
      return failure(malformedImport(line));
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
