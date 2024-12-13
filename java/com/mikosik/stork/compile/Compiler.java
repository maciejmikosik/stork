package com.mikosik.stork.compile;

import static com.mikosik.stork.common.Throwables.runtimeException;
import static com.mikosik.stork.common.io.Input.tryInput;
import static com.mikosik.stork.common.io.InputOutput.components;
import static com.mikosik.stork.common.io.InputOutput.walk;
import static com.mikosik.stork.compile.link.Bind.bindLambdaParameter;
import static com.mikosik.stork.compile.link.Bind.export;
import static com.mikosik.stork.compile.link.Bind.linking;
import static com.mikosik.stork.compile.link.CombinatoryModule.combinatoryModule;
import static com.mikosik.stork.compile.link.MathModule.mathModule;
import static com.mikosik.stork.compile.link.Modules.join;
import static com.mikosik.stork.compile.link.Unlambda.unlambda;
import static com.mikosik.stork.compile.link.Unquote.unquote;
import static com.mikosik.stork.compile.parse.Parser.parse;
import static com.mikosik.stork.compile.tokenize.Tokenizer.tokenize;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Link.link;
import static com.mikosik.stork.model.Linkage.linkage;
import static com.mikosik.stork.model.Namespace.namespace;
import static com.mikosik.stork.model.Namespace.namespaceOf;
import static com.mikosik.stork.model.Unit.unit;
import static com.mikosik.stork.model.Variable.variable;
import static com.mikosik.stork.model.change.Changes.deep;
import static com.mikosik.stork.model.change.Changes.ifVariable;
import static com.mikosik.stork.model.change.Changes.onBody;
import static com.mikosik.stork.model.change.Changes.onEachDefinition;
import static com.mikosik.stork.program.ProgramModule.programModule;
import static java.nio.charset.StandardCharsets.US_ASCII;

import java.nio.file.Files;
import java.nio.file.Path;

import com.mikosik.stork.common.io.Input;
import com.mikosik.stork.model.Link;
import com.mikosik.stork.model.Linkage;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.model.Namespace;
import com.mikosik.stork.model.Unit;

public class Compiler {
  public static Module compileCoreLibrary(Path directory) {
    return join(
        compileDirectory(directory),
        combinatoryModule(),
        mathModule(),
        makeComputable(programModule()));
  }

  private static Module makeComputable(Module module) {
    return onEachDefinition(onBody(deep(unlambda)))
        .andThen(onEachDefinition(onBody(deep(unquote))))
        .apply(module);
  }

  public static Module compileDirectory(Path rootDirectory) {
    return makeComputable(join(walk(rootDirectory)
        .filter(Files::isDirectory)
        .map(directory -> compileSubDirectory(rootDirectory, directory))
        .toList()));
  }

  private static Module compileSubDirectory(Path rootDirectory, Path directory) {
    return selfBuild(unitFrom(rootDirectory, directory));
  }

  private static Module selfBuild(Unit unit) {
    return onEachDefinition(onBody(deep(bindLambdaParameter)))
        .andThen(export(unit.namespace))
        .andThen(onEachDefinition(onBody(deep(ifVariable(linking(unit.linkage))))))
        .apply(unit.module);
  }

  private static Unit unitFrom(Path rootDirectory, Path directory) {
    return unit(
        relative(rootDirectory, directory),
        compileFile(directory.resolve("source")),
        linkageFrom(directory.resolve("import")));
  }

  private static Module compileFile(Path file) {
    try (Input input = tryInput(file).buffered()) {
      return parse(tokenize(input.iterator()));
    }
  }

  private static Linkage linkageFrom(Path file) {
    try (Input input = tryInput(file).buffered()) {
      return linkageFrom(input);
    }
  }

  private static Linkage linkageFrom(Input input) {
    return linkage(input.bufferedReader(US_ASCII).lines()
        .map(Compiler::linkFrom)
        .toList());
  }

  private static Link linkFrom(String line) {
    String[] split = line.split(" ");
    if (split.length == 1) {
      return link(identifier(split[0]));
    } else if (split.length == 2) {
      return link(identifier(split[0]), variable(split[1]));
    } else {
      throw runtimeException("illegal import line: %s", line);
    }
  }

  private static Namespace relative(Path rootDirectory, Path directory) {
    return rootDirectory.equals(directory)
        ? namespaceOf()
        : namespace(components(rootDirectory.relativize(directory)));
  }
}
