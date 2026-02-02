package com.mikosik.stork.compile;

import static com.mikosik.stork.common.Collections.toMapFromEntries;
import static com.mikosik.stork.common.ImmutableList.none;
import static com.mikosik.stork.common.Throwables.runtimeException;
import static com.mikosik.stork.common.io.Ascii.isAlphanumeric;
import static com.mikosik.stork.common.io.Input.input;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Link.link;
import static com.mikosik.stork.model.Linkage.linkage;
import static com.mikosik.stork.model.Source.Kind.IMPORT;
import static com.mikosik.stork.model.Variable.variable;
import static com.mikosik.stork.problem.compile.importing.IllegalCharacter.illegalCharacter;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.util.Map.entry;
import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Link;
import com.mikosik.stork.model.Linkage;
import com.mikosik.stork.model.Namespace;
import com.mikosik.stork.model.Source;
import com.mikosik.stork.model.Variable;

public class Importer {
  private final Map<Namespace, Linkage> linkages;

  private Importer(Map<Namespace, Linkage> linkages) {
    this.linkages = linkages;
  }

  public static Importer importer(List<Source> sources) {
    var linkages = sources.stream()
        .filter(source -> source.kind == IMPORT)
        .map(importSource -> entry(
            importSource.namespace,
            linkageFrom(importSource.content)))
        .collect(toMapFromEntries());
    return new Importer(linkages);
  }

  private static Linkage linkageFrom(byte[] content) {
    return linkage(input(content).bufferedReader(US_ASCII).lines()
        .map(line -> linkFrom(line.trim()))
        .toList());
  }

  private static Link linkFrom(String line) {
    line.chars().forEach(character -> {
      if (!(isAlphanumeric((byte) character)
          || character == '/'
          || character == ' ')) {
        throw illegalCharacter(line, (byte) character);
      }
    });

    String[] split = line.split(" ");
    if (split.length == 1) {
      return link(identifier(split[0]));
    } else if (split.length == 2) {
      return link(identifier(split[0]), variable(split[1]));
    } else {
      throw runtimeException("illegal import line: %s", line);
    }
  }

  private static final Linkage noLinkage = linkage(none());

  public Function<Variable, Expression> linkageFor(Namespace namespace) {
    return linking(getLinkage(namespace));
  }

  private Linkage getLinkage(Namespace namespace) {
    return linkages.getOrDefault(namespace, noLinkage);
  }

  private static Function<Variable, Expression> linking(Linkage linkage) {
    Map<Variable, Expression> map = linkage.links.stream()
        .collect(toMap(
            link -> link.variable,
            link -> link.identifier));
    return variable -> map.getOrDefault(variable, variable);
  }
}
