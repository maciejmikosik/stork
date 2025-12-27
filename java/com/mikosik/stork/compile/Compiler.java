package com.mikosik.stork.compile;

import static com.mikosik.stork.common.Collections.toMapFromEntries;
import static com.mikosik.stork.common.Logic.constant;
import static com.mikosik.stork.common.Sequence.sequenceOf;
import static com.mikosik.stork.common.Sequence.toSequence;
import static com.mikosik.stork.common.Throwables.runtimeException;
import static com.mikosik.stork.common.io.Ascii.isAlphanumeric;
import static com.mikosik.stork.common.io.Input.input;
import static com.mikosik.stork.compile.link.Bind.bindLambdaParameter;
import static com.mikosik.stork.compile.link.Bind.linking;
import static com.mikosik.stork.compile.link.Unlambda.unlambda;
import static com.mikosik.stork.compile.link.Unquote.unquote;
import static com.mikosik.stork.compile.parse.Parser.parse;
import static com.mikosik.stork.compile.tokenize.Tokenizer.tokenize;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Link.link;
import static com.mikosik.stork.model.Linkage.linkage;
import static com.mikosik.stork.model.Source.Kind.CODE;
import static com.mikosik.stork.model.Source.Kind.IMPORT;
import static com.mikosik.stork.model.Variable.variable;
import static com.mikosik.stork.model.change.Changes.deep;
import static com.mikosik.stork.model.change.Changes.ifVariable;
import static com.mikosik.stork.model.change.Changes.onBody;
import static com.mikosik.stork.model.change.Changes.onIdentifier;
import static com.mikosik.stork.model.change.Changes.onNamespace;
import static com.mikosik.stork.problem.compile.importing.IllegalCharacter.illegalCharacter;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.util.Map.entry;
import static java.util.stream.Collectors.toSet;

import java.util.List;
import java.util.Map;

import com.mikosik.stork.common.Sequence;
import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Link;
import com.mikosik.stork.model.Linkage;
import com.mikosik.stork.model.Namespace;
import com.mikosik.stork.model.Source;

public class Compiler {
  private static final Linkage noLinkage = linkage(sequenceOf());

  public static Sequence<Definition> compile(List<Source> sources) {
    // TODO create dedicated class for handling imports
    var linkages = sources.stream()
        .filter(source -> source.kind == IMPORT)
        .map(importSource -> entry(
            importSource.namespace,
            linkageFrom(importSource.content)))
        .collect(toMapFromEntries());

    return sources.stream()
        .filter(source -> source.kind == CODE)
        .map(source -> compile(source, linkages))
        .flatMap(definitions -> definitions.stream())
        .collect(toSequence());
  }

  private static Sequence<Definition> compile(
      Source source,
      Map<Namespace, Linkage> linkages) {
    var linkage = linkages.getOrDefault(source.namespace, noLinkage);
    var library = compileCode(source.content);
    var exported = library.stream()
        .map(definition -> definition.identifier.variable)
        .collect(toSet());
    return library.stream()
        // TODO test that order is ensured: lambda, local, import
        .map(onBody(deep(bindLambdaParameter)))
        .map(onBody(deep(ifVariable(variable -> exported.contains(variable)
            ? identifier(source.namespace, variable)
            : variable))))
        .map(onBody(deep(ifVariable(linking(linkage)))))
        .map(onIdentifier(onNamespace(constant(source.namespace))))
        // TODO inline compilation helpers
        .map(onBody(deep(unlambda)))
        .map(onBody(deep(unquote)))
        .collect(toSequence());
  }

  private static Sequence<Definition> compileCode(byte[] content) {
    // TODO common for converting byte[] -> Iterator<Byte>
    return parse(tokenize(input(content).iterator()));
  }

  private static Linkage linkageFrom(byte[] content) {
    return linkage(input(content).bufferedReader(US_ASCII).lines()
        .map(line -> linkFrom(line.trim()))
        .collect(toSequence()));
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
}
