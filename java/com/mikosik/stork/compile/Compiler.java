package com.mikosik.stork.compile;

import static com.mikosik.stork.common.Collections.toMapFromEntries;
import static com.mikosik.stork.common.Sequence.sequenceOf;
import static com.mikosik.stork.common.Sequence.toSequence;
import static com.mikosik.stork.common.Throwables.runtimeException;
import static com.mikosik.stork.common.io.Ascii.isAlphanumeric;
import static com.mikosik.stork.common.io.Input.input;
import static com.mikosik.stork.compile.link.Bind.bindLambdaParameter;
import static com.mikosik.stork.compile.link.Bind.export;
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
import static com.mikosik.stork.model.Unit.unit;
import static com.mikosik.stork.model.Variable.variable;
import static com.mikosik.stork.model.change.Changes.deep;
import static com.mikosik.stork.model.change.Changes.ifVariable;
import static com.mikosik.stork.model.change.Changes.onBody;
import static com.mikosik.stork.model.change.Changes.onEachDefinition;
import static com.mikosik.stork.problem.compile.importing.IllegalCharacter.illegalCharacter;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.util.Map.entry;

import java.util.List;

import com.mikosik.stork.common.Sequence;
import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Library;
import com.mikosik.stork.model.Link;
import com.mikosik.stork.model.Linkage;
import com.mikosik.stork.model.Source;
import com.mikosik.stork.model.Unit;

public class Compiler {
  public static Sequence<Definition> compile(List<Source> sources) {
    var namespaceToLinkage = sources.stream()
        .filter(source -> source.kind == IMPORT)
        .map(importSource -> entry(
            importSource.namespace,
            linkageFrom(importSource.content)))
        .collect(toMapFromEntries());

    return sources.stream()
        .filter(source -> source.kind == CODE)
        .map(codeSource -> entry(
            codeSource.namespace,
            compileCode(codeSource.content)))
        .map(entry -> {
          var namespace = entry.getKey();
          var library = entry.getValue();
          var linkage = namespaceToLinkage
              .getOrDefault(namespace, linkage(sequenceOf()));
          return selfBuild(unit(namespace, library, linkage));
        })
        .map(Compiler::makeComputable)
        .flatMap(library -> library.definitions.stream())
        .collect(toSequence());
  }

  private static Library makeComputable(Library library) {
    return onEachDefinition(onBody(deep(unlambda)))
        .andThen(onEachDefinition(onBody(deep(unquote))))
        .apply(library);
  }

  private static Library selfBuild(Unit unit) {
    return onEachDefinition(onBody(deep(bindLambdaParameter)))
        .andThen(export(unit.namespace))
        .andThen(onEachDefinition(onBody(deep(ifVariable(linking(unit.linkage))))))
        .apply(unit.library);
  }

  private static Library compileCode(byte[] content) {
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
