package com.mikosik.stork.tool.link;

import static com.mikosik.stork.common.Chain.chainFrom;
import static com.mikosik.stork.data.model.Definition.definition;
import static com.mikosik.stork.data.model.Module.module;
import static com.mikosik.stork.data.model.Opcode.ADD;
import static com.mikosik.stork.data.model.Opcode.EQUAL;
import static com.mikosik.stork.data.model.Opcode.MORE_THAN;
import static com.mikosik.stork.data.model.Opcode.NEGATE;
import static com.mikosik.stork.tool.common.Aliens.computeArguments;
import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Definition;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Module;

public class OpcodingLinker implements Linker {
  private final Linker linker;

  private OpcodingLinker(Linker linker) {
    this.linker = linker;
  }

  public static Linker opcoding(Linker linker) {
    return new OpcodingLinker(linker);
  }

  public Module link(Chain<Module> modules) {
    return installOpcodes(linker.link(modules));
  }

  private static Module installOpcodes(Module module) {
    List<Definition> definitions = module.definitions.stream()
        .map(OpcodingLinker::replaceByOpcodeIfDefined)
        .collect(toList());
    return module(chainFrom(definitions));
  }

  // replace native by exception
  private static Definition replaceByOpcodeIfDefined(Definition definition) {
    String name = definition.variable.name;
    return opcodes.containsKey(name)
        ? definition(definition.variable, opcodes.get(name))
        : definition;
  }

  private static final Map<String, Expression> opcodes = opcodes();

  private static Map<String, Expression> opcodes() {
    Map<String, Expression> map = new HashMap<>();
    map.put("stork.integer.negate", computeArguments(1, NEGATE));
    map.put("stork.integer.add", computeArguments(2, ADD));
    map.put("stork.integer.equal", computeArguments(2, EQUAL));
    map.put("stork.integer.moreThan", computeArguments(2, MORE_THAN));
    return map;
  }
}
