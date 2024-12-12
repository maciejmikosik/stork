package com.mikosik.stork.debug;

import static com.mikosik.stork.common.io.Serializables.join;
import static com.mikosik.stork.common.io.Serializables.serializable;
import static java.lang.String.format;

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
        .toList());
  }

  public static Serializable decompile(Definition definition) {
    return join(
        serializable(definition.identifier.name()),
        decompileBody(definition.body));
  }

  public static Serializable decompile(Expression expression) {
    return switch (expression) {
      case Variable variable -> serializable(variable.name);
      case Identifier identifier -> serializable(identifier.name());
      case Integer integer -> serializable(integer.value.toString());
      case Quote quote -> join(
          serializable('\"'),
          serializable(quote.string),
          serializable('\"'));
      case EagerInstruction eager -> join(
          serializable(eager.visited
              ? "eagerVisited("
              : "eager("),
          decompile(eager.instruction),
          serializable(')'));
      case NamedInstruction instruction -> join(
          serializable('<'),
          decompile(instruction.name),
          serializable('>'));
      case Instruction instruction -> serializable("<>");
      case Parameter parameter -> serializable(parameter.name);
      case Lambda lambda -> join(
          serializable('('),
          serializable(lambda.parameter.name),
          serializable(')'),
          decompileBody(lambda.body));
      case Application application -> join(
          decompile(application.function),
          serializable('('),
          decompile(application.argument),
          serializable(')'));
      case Stdin stdin -> join(
          serializable("stdin("),
          serializable("" + stdin.index),
          serializable(')'));
      case Stdout stdout -> serializable("stdout");
      default -> throw new RuntimeException(format("unknown expression: %s", expression));
    };
  }

  private static Serializable decompileBody(Expression body) {
    return switch (body) {
      case Lambda lambda -> decompile(body);
      default -> join(
          serializable('{'),
          decompile(body),
          serializable('}'));
    };
  }
}
