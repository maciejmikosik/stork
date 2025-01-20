package com.mikosik.stork.compile;

import static com.mikosik.stork.common.Sequence.toSequence;
import static com.mikosik.stork.common.Sequence.toSequenceThen;
import static com.mikosik.stork.common.Throwables.runtimeException;
import static com.mikosik.stork.compile.link.Bind.bindLambdaParameter;
import static com.mikosik.stork.compile.link.Bind.export;
import static com.mikosik.stork.compile.link.Bind.linking;
import static com.mikosik.stork.compile.link.Modules.join;
import static com.mikosik.stork.compile.link.OperatorModule.operatorModule;
import static com.mikosik.stork.compile.link.Unlambda.unlambda;
import static com.mikosik.stork.compile.link.Unquote.unquote;
import static com.mikosik.stork.compile.link.VerifyModule.verify;
import static com.mikosik.stork.compile.parse.Parser.parse;
import static com.mikosik.stork.compile.tokenize.Tokenizer.tokenize;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Link.link;
import static com.mikosik.stork.model.Linkage.linkage;
import static com.mikosik.stork.model.Namespace.namespaceOf;
import static com.mikosik.stork.model.Unit.unit;
import static com.mikosik.stork.model.Variable.variable;
import static com.mikosik.stork.model.change.Changes.deep;
import static com.mikosik.stork.model.change.Changes.ifVariable;
import static com.mikosik.stork.model.change.Changes.onBody;
import static com.mikosik.stork.model.change.Changes.onEachDefinition;
import static java.nio.charset.StandardCharsets.US_ASCII;

import com.mikosik.stork.common.io.Directory;
import com.mikosik.stork.common.io.File;
import com.mikosik.stork.common.io.Input;
import com.mikosik.stork.compile.link.Modules;
import com.mikosik.stork.model.Link;
import com.mikosik.stork.model.Linkage;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.model.Namespace;
import com.mikosik.stork.model.Unit;

public class Compiler {
  public static Module compile(Compilation compilation) {
    var compiledSources = compilation.sources.stream()
        .map(Compiler::compileTree)
        .collect(toSequenceThen(Modules::join));
    var dependencies = join(compilation.libraries);
    return verify(join(
        makeComputable(compiledSources),
        dependencies));
  }

  public static Module nativeModule() {
    return operatorModule();
  }

  private static Module makeComputable(Module module) {
    return onEachDefinition(onBody(deep(unlambda)))
        .andThen(onEachDefinition(onBody(deep(unquote))))
        .apply(module);
  }

  private static Module compileTree(Directory rootDirectory) {
    return compileTree(namespaceOf(), rootDirectory);
  }

  private static Module compileTree(Namespace namespace, Directory directory) {
    var moduleFromThisDirectory = compileDirectory(namespace, directory);
    var moduleFromSubDirectories = directory.directories()
        .map(subDirectory -> compileTree(namespace.add(subDirectory.name()), subDirectory))
        .collect(toSequenceThen(Modules::join));
    return join(
        moduleFromThisDirectory,
        moduleFromSubDirectories);
  }

  private static Module compileDirectory(Namespace namespace, Directory directory) {
    return selfBuild(unitFrom(namespace, directory));
  }

  private static Module selfBuild(Unit unit) {
    return onEachDefinition(onBody(deep(bindLambdaParameter)))
        .andThen(export(unit.namespace))
        .andThen(onEachDefinition(onBody(deep(ifVariable(linking(unit.linkage))))))
        .apply(unit.module);
  }

  private static Unit unitFrom(Namespace namespace, Directory directory) {
    return unit(
        namespace,
        compileFile(directory.file("source.stork")),
        linkageFrom(directory.file("import.stork")));
  }

  private static Module compileFile(File file) {
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
        .map(Compiler::linkFrom)
        .collect(toSequence()));
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
}
