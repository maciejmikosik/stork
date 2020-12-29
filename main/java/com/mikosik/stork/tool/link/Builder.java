package com.mikosik.stork.tool.link;

import static com.mikosik.stork.common.Chain.chainFrom;
import static com.mikosik.stork.model.Module.module;
import static com.mikosik.stork.model.Variable.variable;
import static com.mikosik.stork.tool.common.Invocation.asInvocation;
import static com.mikosik.stork.tool.common.Scope.LOCAL;
import static java.util.stream.Collectors.toList;

import java.util.Iterator;
import java.util.List;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.model.Quote;
import com.mikosik.stork.model.Variable;
import com.mikosik.stork.tool.common.Invocation;
import com.mikosik.stork.tool.common.Traverser;

public class Builder implements Weaver {
  private Builder() {}

  public static Weaver builder() {
    return new Builder();
  }

  public Module weave(Module module) {
    List<Definition> buildDefinitions = module.definitions.stream()
        .filter(definition -> definition.variable.name.equals("build"))
        .collect(toList());
    for (Definition buildFunction : buildDefinitions) {
      module = build(buildFunction, module);
    }
    List<Definition> definitions = module.definitions.stream()
        .filter(definition -> !definition.variable.name.equals("build"))
        .collect(toList());
    return module(chainFrom(definitions));
  }

  private static Module build(Definition buildFunction, Module module) {
    Chain<Expression> instructions = asInvocation(buildFunction.expression).arguments;
    for (Expression instruction : instructions) {
      module = build(asInvocation(instruction), module);
    }
    return module;
  }

  private static Module build(Invocation instruction, Module module) {
    switch (instruction.function.name) {
      case "exportAs":
        return exportAs(instruction.arguments, module);
      case "importAs":
        return importAs(instruction.arguments, module);
      case "import":
        return import_(instruction.arguments, module);
      default:
        throw new RuntimeException(instruction.function.name);
    }
  }

  private static Module exportAs(Chain<Expression> arguments, Module module) {
    Iterator<Expression> iterator = arguments.iterator();
    Variable global = variable(asJavaString(iterator.next()));
    Variable local = variable(asJavaString(iterator.next()));
    return renameTo(global, local, module);
  }

  private static Module importAs(Chain<Expression> arguments, Module module) {
    Iterator<Expression> iterator = arguments.iterator();
    Variable local = variable(asJavaString(iterator.next()));
    Variable global = variable(asJavaString(iterator.next()));
    return renameTo(global, local, module);
  }

  private static Module import_(Chain<Expression> arguments, Module module) {
    Iterator<Expression> iterator = arguments.iterator();
    Variable global = variable(asJavaString(iterator.next()));
    Variable local = variable(LOCAL.format(global));
    return renameTo(global, local, module);
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

  public static String asJavaString(Expression expression) {
    return ((Quote) expression).string;
  }
}
