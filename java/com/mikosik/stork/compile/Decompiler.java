package com.mikosik.stork.compile;

import static com.mikosik.stork.common.io.Serializables.join;
import static com.mikosik.stork.common.io.Serializables.serializable;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

import java.util.stream.Stream;

import com.mikosik.stork.common.io.Serializable;
import com.mikosik.stork.model.Application;
import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.EagerInstruction;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Instruction;
import com.mikosik.stork.model.Integer;
import com.mikosik.stork.model.Lambda;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.model.NamedInstruction;
import com.mikosik.stork.model.Parameter;
import com.mikosik.stork.model.Quote;
import com.mikosik.stork.model.Variable;
import com.mikosik.stork.program.Stdin;
import com.mikosik.stork.program.Stdout;

public class Decompiler {
  public static Serializable decompile(Module module) {
    return join(module.definitions.stream()
        .flatMap(definition -> Stream.of(
            serializable(' '),
            decompile(definition)))
        .skip(1)
        .collect(toList()));
  }

  public static Serializable decompile(Definition definition) {
    return join(
        serializable(definition.identifier.name()),
        decompileBody(definition.body));
  }

  public static Serializable decompile(Expression expression) {
    if (expression instanceof Variable variable) {
      return serializable(variable.name);
    } else if (expression instanceof Identifier identifier) {
      return serializable(identifier.name());
    } else if (expression instanceof Integer integer) {
      return serializable(integer.value.toString());
    } else if (expression instanceof Quote quote) {
      return join(
          serializable('\"'),
          serializable(quote.string),
          serializable('\"'));
    } else if (expression instanceof EagerInstruction eager) {
      return join(
          serializable(eager.visited
              ? "eagerVisited("
              : "eager("),
          decompile(eager.instruction),
          serializable(')'));
    } else if (expression instanceof NamedInstruction instruction) {
      return join(
          serializable('<'),
          decompile(instruction.name),
          serializable('>'));
    } else if (expression instanceof Instruction instruction) {
      return serializable("<>");
    } else if (expression instanceof Parameter parameter) {
      return serializable(parameter.name);
    } else if (expression instanceof Lambda lambda) {
      return join(
          serializable('('),
          serializable(lambda.parameter.name),
          serializable(')'),
          decompileBody(lambda.body));
    } else if (expression instanceof Application application) {
      return join(
          decompile(application.function),
          serializable('('),
          decompile(application.argument),
          serializable(')'));
    } else if (expression instanceof Stdin stdin) {
      return join(
          serializable("stdin("),
          serializable("" + stdin.index),
          serializable(')'));
    } else if (expression instanceof Stdout) {
      return serializable("stdout");
    } else {
      throw new RuntimeException(format("unknown expression: %s", expression));
    }
  }

  private static Serializable decompileBody(Expression body) {
    return body instanceof Lambda
        ? decompile(body)
        : join(
            serializable('{'),
            decompile(body),
            serializable('}'));
  }
}
