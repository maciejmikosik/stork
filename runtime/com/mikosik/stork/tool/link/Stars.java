package com.mikosik.stork.tool.link;

import static com.mikosik.stork.common.Chain.chain;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.tool.link.Bind.bindLambdaParameter;
import static com.mikosik.stork.tool.link.Bind.export;
import static com.mikosik.stork.tool.link.Bind.identifyVariables;
import static com.mikosik.stork.tool.link.Changes.inModule;
import static com.mikosik.stork.tool.link.Link.link;
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
import com.mikosik.stork.tool.compile.Compiler;

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
    String namespace = namespace(directory, file);
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

  private static String namespace(Node directory, Node file) {
    Path path = directory.relativeToNested(file.parent());
    String packageName = path.toString().replace('/', '.');
    return packageName.isEmpty()
        ? packageName
        : packageName + '.';
  }
}
