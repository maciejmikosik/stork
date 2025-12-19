package com.mikosik.stork.compile;

import static com.mikosik.stork.common.Catcher.catcher;
import static com.mikosik.stork.common.Sequence.toSequence;
import static com.mikosik.stork.common.Sequence.toSequenceThen;
import static com.mikosik.stork.common.Throwables.runtimeException;
import static com.mikosik.stork.common.io.Ascii.isAlphanumeric;
import static com.mikosik.stork.compile.link.Bind.bindLambdaParameter;
import static com.mikosik.stork.compile.link.Bind.export;
import static com.mikosik.stork.compile.link.Bind.linking;
import static com.mikosik.stork.compile.link.Unlambda.unlambda;
import static com.mikosik.stork.compile.link.Unquote.unquote;
import static com.mikosik.stork.compile.link.VerifyLibrary.verify;
import static com.mikosik.stork.compile.parse.Parser.parse;
import static com.mikosik.stork.compile.tokenize.Tokenizer.tokenize;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Library.join;
import static com.mikosik.stork.model.Link.link;
import static com.mikosik.stork.model.Linkage.linkage;
import static com.mikosik.stork.model.Namespace.namespaceOf;
import static com.mikosik.stork.model.Unit.unit;
import static com.mikosik.stork.model.Variable.variable;
import static com.mikosik.stork.model.change.Changes.deep;
import static com.mikosik.stork.model.change.Changes.ifVariable;
import static com.mikosik.stork.model.change.Changes.onBody;
import static com.mikosik.stork.model.change.Changes.onEachDefinition;
import static com.mikosik.stork.problem.compile.CannotCompileDirectory.cannotCompileDirectory;
import static com.mikosik.stork.problem.compile.importing.IllegalCharacter.illegalCharacter;
import static java.nio.charset.StandardCharsets.US_ASCII;

import com.mikosik.stork.common.io.Directory;
import com.mikosik.stork.common.io.File;
import com.mikosik.stork.common.io.Input;
import com.mikosik.stork.model.Library;
import com.mikosik.stork.model.Link;
import com.mikosik.stork.model.Linkage;
import com.mikosik.stork.model.Namespace;
import com.mikosik.stork.model.Unit;
import com.mikosik.stork.problem.compile.CannotCompile;

public class Compiler {
  public static Library compile(Compilation compilation) {
    var compiledSources = compilation.sources.stream()
        .map(Compiler::compileTree)
        .collect(toSequenceThen(Library::join));
    var dependencies = join(compilation.libraries);
    return verify(join(
        makeComputable(compiledSources),
        dependencies));
  }

  private static Library makeComputable(Library library) {
    return onEachDefinition(onBody(deep(unlambda)))
        .andThen(onEachDefinition(onBody(deep(unquote))))
        .apply(library);
  }

  private static Library compileTree(Directory rootDirectory) {
    return compileTree(namespaceOf(), rootDirectory);
  }

  private static Library compileTree(Namespace namespace, Directory directory) {
    var libraryFromThisDirectory = compileDirectory(namespace, directory);
    var libraryFromSubDirectories = directory.directories()
        .map(subDirectory -> compileTree(namespace.add(subDirectory.name()), subDirectory))
        .collect(toSequenceThen(Library::join));
    return join(
        libraryFromThisDirectory,
        libraryFromSubDirectories);
  }

  private static Library compileDirectory(Namespace namespace, Directory directory) {
    return selfBuild(unitFrom(namespace, directory));
  }

  private static Library selfBuild(Unit unit) {
    return onEachDefinition(onBody(deep(bindLambdaParameter)))
        .andThen(export(unit.namespace))
        .andThen(onEachDefinition(onBody(deep(ifVariable(linking(unit.linkage))))))
        .apply(unit.library);
  }

  private static Unit unitFrom(Namespace namespace, Directory directory) {
    var catcher = catcher(CannotCompile.class);
    var library = catcher.tryCatch(() -> compileFile(directory.file("source.stork")));
    var imports = catcher.tryCatch(() -> linkageFrom(directory.file("import.stork")));
    catcher.rethrow(problems -> cannotCompileDirectory(toDirectory(namespace), problems));
    return unit(namespace, library, imports);
  }

  private static String toDirectory(Namespace namespace) {
    return namespace.path.isEmpty()
        ? "."
        : namespace.toString();
  }

  private static Library compileFile(File file) {
    try (Input input = file.tryInput().buffered()) {
      return parse(tokenize(input.iterator()));
    }
  }

  private static Linkage linkageFrom(File file) {
    try (Input input = file.tryInput().buffered()) {
      return linkageFrom(input);
    }
  }

  private static Linkage linkageFrom(Input input) {
    return linkage(input.bufferedReader(US_ASCII).lines()
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
