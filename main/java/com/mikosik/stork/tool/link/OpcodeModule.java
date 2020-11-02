package com.mikosik.stork.tool.link;

import static com.mikosik.stork.common.Chain.chainFrom;
import static com.mikosik.stork.data.model.Definition.definition;
import static com.mikosik.stork.data.model.Integer.integer;
import static com.mikosik.stork.data.model.Module.module;
import static com.mikosik.stork.data.model.comp.Computation.computation;
import static com.mikosik.stork.data.model.comp.Function.function;
import static com.mikosik.stork.tool.common.Translate.asStorkBoolean;
import static com.mikosik.stork.tool.compute.Operands.operands;
import static com.mikosik.stork.tool.link.Repository.repository;
import static java.util.stream.Collectors.toList;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.mikosik.stork.data.model.Definition;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Module;
import com.mikosik.stork.data.model.Opcode;
import com.mikosik.stork.data.model.comp.Computation;
import com.mikosik.stork.data.model.comp.Stack;
import com.mikosik.stork.tool.compute.Operands;

public class OpcodeModule {
  public static Module opcodeModule() {
    Module module = repository().module("opcode.stork");
    List<Definition> definitions = module.definitions.stream()
        .map(OpcodeModule::replaceByOpcodeIfDefined)
        .collect(toList());
    return module(chainFrom(definitions));
  }

  // replace implementedByRuntime by exception
  private static Definition replaceByOpcodeIfDefined(Definition definition) {
    String name = definition.variable.name;
    return opcodes.containsKey(name)
        ? definition(definition.variable, opcodes.get(name))
        : definition;
  }

  private static final Map<String, Opcode> opcodes = opcodes();

  private static Map<String, Opcode> opcodes() {
    Map<String, Opcode> map = new HashMap<String, Opcode>();
    map.put("opArg", OpcodeModule::handleArgument);
    map.put("opNegate", OpcodeModule::handleNegate);
    map.put("opAdd", OpcodeModule::handleAdd);
    map.put("opEqual", OpcodeModule::handleEqual);
    map.put("opMoreThan", OpcodeModule::handleMoreThan);
    return map;
  }

  private static Computation handleArgument(Stack stack) {
    Operands operands = operands(stack);
    Expression function = operands.next();
    Expression argument = operands.next();
    Stack newStack = operands.stack();
    return computation(
        argument,
        function(
            function,
            newStack));
  }

  private static Computation handleNegate(Stack stack) {
    return handle(stack, x -> integer(x.negate()));
  }

  private static Computation handleAdd(Stack stack) {
    return handle(stack, (x, y) -> integer(x.add(y)));
  }

  private static Computation handleEqual(Stack stack) {
    return handle(stack, (x, y) -> asStorkBoolean(x.equals(y)));
  }

  private static Computation handleMoreThan(Stack stack) {
    return handle(stack, (x, y) -> asStorkBoolean(y.compareTo(x) > 0));
  }

  private static Computation handle(
      Stack stack,
      Function<BigInteger, Expression> logic) {
    Operands operands = operands(stack);
    return computation(
        logic.apply(operands.nextJavaBigInteger()),
        operands.stack());
  }

  private static Computation handle(
      Stack stack,
      BiFunction<BigInteger, BigInteger, Expression> logic) {
    Operands operands = operands(stack);
    return computation(
        logic.apply(
            operands.nextJavaBigInteger(),
            operands.nextJavaBigInteger()),
        operands.stack());
  }
}
