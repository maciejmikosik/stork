package com.mikosik.stork.tool.link;

import static com.mikosik.stork.common.Chain.chainFrom;
import static com.mikosik.stork.common.Chain.empty;
import static com.mikosik.stork.model.Variable.variable;
import static com.mikosik.stork.tool.common.Scope.LOCAL;
import static com.mikosik.stork.tool.link.Link.link;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;

import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.common.io.Input;
import com.mikosik.stork.common.io.Node;
import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.model.Variable;
import com.mikosik.stork.tool.common.Traverser;
import com.mikosik.stork.tool.compile.Compiler;

public class Stars {
  public static Module moduleFromDirectory(Node directory) {
    Stream<Node> filter = directory.nested()
        .filter(Node::isRegularFile)
        .filter(file -> file.name().equals("stork"));
    List<Module> modules = filter
        .map(file -> moduleFromFile(directory, file))
        .collect(toList());
    return link(chainFrom(modules));
  }

  private static Module moduleFromFile(Node directory, Node file) {
    String packagePrefix = packagePrefix(directory, file);
    Chain<String> imports = importsFor(file);
    Compiler compiler = new Compiler();
    try (Input input = file.input().buffered()) {
      return import_(imports, export(packagePrefix, compiler.compileModule(input)));
    }
  }

  private static Chain<String> importsFor(Node file) {
    Node importFile = file.parent().child("import");
    Chain<String> result = empty();
    if (importFile.isRegularFile()) {
      try (Scanner scanner = importFile.input().buffered().scan(UTF_8)) {
        while (scanner.hasNext()) {
          result = result.add(scanner.nextLine());
        }
      }
    }
    return result;
  }

  private static String packagePrefix(Node directory, Node file) {
    Path path = directory.relativeToNested(file.parent());
    String packageName = path.toString().replace('/', '.');
    return packageName.isEmpty()
        ? packageName
        : packageName + '.';
  }

  private static Module export(String packagePrefix, Module module) {
    Chain<Definition> definitions = module.definitions;
    for (Definition definition : definitions) {
      Variable original = definition.variable;
      Variable replacement = variable(packagePrefix + original.name);
      module = renameTo(replacement, original, module);
    }
    return module;
  }

  private static Module import_(Chain<String> imports, Module module) {
    for (String import_ : imports) {
      Variable global = variable(import_);
      Variable local = variable(LOCAL.format(global));
      module = renameTo(global, local, module);

    }
    return module;
  }

  private static Module renameTo(
      Variable replacement,
      Variable original,
      Module module) {
    return new Traverser() {
      protected Variable traverse(Variable variable) {
        return variable.name.equals(original.name)
            ? replacement
            : variable;
      }

      protected Variable traverseDefinitionName(Variable variable) {
        return traverse(variable);
      }
    }.traverse(module);
  }
}
