package com.mikosik.stork.compile;

import static com.mikosik.stork.common.Chain.chain;
import static com.mikosik.stork.compile.Bind.bindLambdaParameter;
import static com.mikosik.stork.compile.Bind.export;
import static com.mikosik.stork.compile.Bind.join;
import static com.mikosik.stork.compile.Bind.linking;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Link.link;
import static com.mikosik.stork.model.Linkage.linkage;
import static com.mikosik.stork.model.Namespace.namespace;
import static com.mikosik.stork.model.Variable.variable;
import static com.mikosik.stork.model.change.Changes.changeVariable;
import static com.mikosik.stork.model.change.Changes.inModule;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.util.stream.Collectors.toList;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import com.mikosik.stork.common.io.Input;
import com.mikosik.stork.common.io.Node;
import com.mikosik.stork.model.Link;
import com.mikosik.stork.model.Linkage;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.model.Namespace;

public class Stars {
  public static Module moduleFromDirectory(Node directory) {
    Stream<Node> filter = directory.nested()
        .filter(Node::isRegularFile)
        .filter(file -> file.name().equals("stork"));
    List<Module> modules = filter
        .map(file -> moduleFromFile(directory, file))
        .collect(toList());
    return join(chain(modules));
  }

  private static Module moduleFromFile(Node directory, Node file) {
    Module module = compile(file);
    Namespace namespace = relative(directory, file);
    Linkage linkage = linkageFrom(file);

    return inModule(bindLambdaParameter)
        .andThen(export(namespace))
        .andThen(inModule(changeVariable(linking(linkage))))
        .apply(module);
  }

  private static Module compile(Node file) {
    try (Input input = file.input().buffered()) {
      Compiler compiler = new Compiler();
      return compiler.compileModule(input);
    }
  }

  private static Linkage linkageFrom(Node file) {
    Node linkageFile = file.parent().child("import");
    if (linkageFile.isRegularFile()) {
      try (Input input = linkageFile.input()) {
        return linkageFrom(input);
      }
    } else {
      return linkage();
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

  private static Namespace relative(Node directory, Node file) {
    return directory.name().equals(file.parent().name())
        ? namespace()
        : namespace(chain(directory.relativeToNested(file.parent()))
            .map(Path::toString)
            .reverse());
  }
}
