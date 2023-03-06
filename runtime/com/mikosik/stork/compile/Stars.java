package com.mikosik.stork.compile;

import static com.mikosik.stork.common.Chain.chain;
import static com.mikosik.stork.common.io.Node.node;
import static com.mikosik.stork.compile.Bind.bindLambdaParameter;
import static com.mikosik.stork.compile.Bind.export;
import static com.mikosik.stork.compile.Bind.join;
import static com.mikosik.stork.compile.Bind.linking;
import static com.mikosik.stork.compile.CheckCollisions.checkCollisions;
import static com.mikosik.stork.compile.CheckUndefined.checkUndefined;
import static com.mikosik.stork.compile.CombinatoryModule.combinatoryModule;
import static com.mikosik.stork.compile.MathModule.mathModule;
import static com.mikosik.stork.compile.Unlambda.unlambda;
import static com.mikosik.stork.compile.Unquote.unquote;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Link.link;
import static com.mikosik.stork.model.Linkage.linkage;
import static com.mikosik.stork.model.Namespace.namespace;
import static com.mikosik.stork.model.Variable.variable;
import static com.mikosik.stork.model.change.Changes.changeVariable;
import static com.mikosik.stork.model.change.Changes.inModule;
import static java.nio.charset.StandardCharsets.US_ASCII;

import java.nio.file.Path;
import java.util.stream.Stream;

import com.mikosik.stork.common.io.Input;
import com.mikosik.stork.common.io.Node;
import com.mikosik.stork.model.Link;
import com.mikosik.stork.model.Linkage;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.model.Namespace;

public class Stars {
  public static Module langModule() {
    return join(chain(
        combinatoryModule(),
        mathModule(),
        moduleFromDirectory(node("core_star"))));
  }

  public static Module build(Module module) {
    return inModule(unlambda)
        .andThen(inModule(unquote))
        .apply(module);
  }

  public static Module verify(Module module) {
    checkCollisions(module);
    checkUndefined(module);
    return module;
  }

  public static Module moduleFromDirectory(Node rootDirectory) {
    return join(chain(rootDirectory.nested()
        .filter(Node::isDirectory)
        .map(directory -> moduleFromDirectory(rootDirectory, directory))));
  }

  private static Module moduleFromDirectory(Node rootDirectory, Node directory) {
    Module module = compile(directory.child("stork"));
    Namespace namespace = relative(rootDirectory, directory);
    Linkage linkage = linkageFrom(directory.child("import"));

    return inModule(bindLambdaParameter)
        .andThen(export(namespace))
        .andThen(inModule(changeVariable(linking(linkage))))
        .apply(module);
  }

  private static Module compile(Node file) {
    try (Input input = file.tryInput().buffered()) {
      Compiler compiler = new Compiler();
      return compiler.compileModule(input);
    }
  }

  private static Linkage linkageFrom(Node file) {
    try (Input input = file.tryInput().buffered()) {
      return linkageFrom(input);
    }
  }

  private static Linkage linkageFrom(Input input) {
    Stream<String> lines = input.bufferedReader(US_ASCII).lines();
    return linkage(chain(lines.map(Stars::linkFrom)));
  }

  private static Link linkFrom(String line) {
    String[] split = line.split(" ");
    if (split.length == 1) {
      return link(identifier(split[0]));
    } else if (split.length == 2) {
      return link(identifier(split[0]), variable(split[1]));
    } else {
      throw new RuntimeException();
    }
  }

  private static Namespace relative(Node rootDirectory, Node directory) {
    return rootDirectory.name().equals(directory.name())
        ? namespace()
        : namespace(chain(rootDirectory.relativeToNested(directory))
            .map(Path::toString)
            .reverse());
  }
}
