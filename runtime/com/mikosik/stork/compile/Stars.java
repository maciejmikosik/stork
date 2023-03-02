package com.mikosik.stork.compile;

import static com.mikosik.stork.common.Chain.chain;
import static com.mikosik.stork.compile.Bind.bindLambdaParameter;
import static com.mikosik.stork.compile.Bind.export;
import static com.mikosik.stork.compile.Bind.identifyVariables;
import static com.mikosik.stork.compile.Link.link;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Namespace.namespace;
import static com.mikosik.stork.model.change.Changes.inModule;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.util.stream.Collectors.toList;

import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.common.io.Input;
import com.mikosik.stork.common.io.Node;
import com.mikosik.stork.model.Identifier;
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
    return link(chain(modules));
  }

  private static Module moduleFromFile(Node directory, Node file) {
    Module module = compile(file);
    Namespace namespace = relative(directory, file);
    Chain<Identifier> imports = importsFor(file);

    return inModule(bindLambdaParameter)
        .andThen(export(namespace))
        .andThen(inModule(identifyVariables(imports)))
        .apply(module);
  }

  private static Module compile(Node file) {
    try (Input input = file.input().buffered()) {
      Compiler compiler = new Compiler();
      return compiler.compileModule(input);
    }
  }

  private static Chain<Identifier> importsFor(Node file) {
    Node importFile = file.parent().child("import");
    Chain<Identifier> result = chain();
    if (importFile.isRegularFile()) {
      try (Scanner scanner = importFile.input().buffered().scan(US_ASCII)) {
        while (scanner.hasNext()) {
          result = result.add(identifier(scanner.nextLine()));
        }
      }
    }
    return result;
  }

  private static Namespace relative(Node directory, Node file) {
    return directory.name().equals(file.parent().name())
        ? namespace()
        : namespace(chain(directory.relativeToNested(file.parent()))
            .map(Path::toString)
            .reverse());
  }
}
