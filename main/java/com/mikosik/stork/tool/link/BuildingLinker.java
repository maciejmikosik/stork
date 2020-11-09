package com.mikosik.stork.tool.link;

import static com.mikosik.stork.common.Chain.chainFrom;
import static com.mikosik.stork.data.model.Application.application;
import static com.mikosik.stork.data.model.Definition.definition;
import static com.mikosik.stork.data.model.Lambda.lambda;
import static com.mikosik.stork.data.model.Module.module;
import static com.mikosik.stork.data.model.Switch.switchOn;
import static com.mikosik.stork.data.model.Variable.variable;
import static com.mikosik.stork.tool.common.Invocation.asInvocation;
import static com.mikosik.stork.tool.common.Scope.LOCAL;
import static com.mikosik.stork.tool.common.Translate.asJavaString;
import static java.util.stream.Collectors.toList;

import java.util.Iterator;
import java.util.List;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Definition;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Module;
import com.mikosik.stork.data.model.Variable;
import com.mikosik.stork.tool.common.Invocation;

public class BuildingLinker implements Linker {
  private final Linker linker;

  private BuildingLinker(Linker linker) {
    this.linker = linker;
  }

  public static Linker building(Linker linker) {
    return new BuildingLinker(linker);
  }

  public Module link(Chain<Module> modules) {
    return linker.link(modules.map(BuildingLinker::build));
  }

  private static Module build(Module module) {
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

  private static Module renameTo(Variable replacement, Variable original, Module module) {
    List<Definition> definitions = module.definitions.stream()
        .map(definition -> renameTo(replacement, original, definition))
        .collect(toList());
    return module(chainFrom(definitions));
  }

  private static Definition renameTo(
      Variable replacement,
      Variable original,
      Definition definition) {
    return definition(
        (Variable) renameTo(replacement, original, definition.variable),
        renameTo(replacement, original, definition.expression));
  }

  private static Expression renameTo(
      Variable replacement,
      Variable original,
      Expression expression) {
    return switchOn(expression)
        .ifVariable(variable -> variable.name.equals(original.name)
            ? replacement
            : variable)
        .ifLambda(lambda -> lambda(
            lambda.parameter,
            renameTo(replacement, original, lambda.body)))
        .ifApplication(application -> application(
            renameTo(replacement, original, application.function),
            renameTo(replacement, original, application.argument)))
        .ifParameter(parameter -> parameter)
        .elseReturn(() -> expression);
  }
}
