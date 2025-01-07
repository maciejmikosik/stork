package com.mikosik.stork.debug;

import static com.mikosik.stork.common.Throwables.runtimeException;
import static com.mikosik.stork.common.io.Serializables.join;
import static com.mikosik.stork.common.io.Serializables.serializable;

import java.util.stream.Stream;

import com.mikosik.stork.common.io.Serializable;
import com.mikosik.stork.model.Application;
import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Integer;
import com.mikosik.stork.model.Lambda;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.model.Operator;
import com.mikosik.stork.model.Parameter;
import com.mikosik.stork.model.Quote;
import com.mikosik.stork.model.Variable;
import com.mikosik.stork.program.Stdin;

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
      case Quote quote -> quoteBrackets(serializable(quote.string));
      case Operator operator -> join(
          serializable("$"),
          serializable(operator));
      case Parameter parameter -> serializable(parameter.name);
      case Lambda lambda -> join(
          roundBrackets(serializable(lambda.parameter.name)),
          decompileBody(lambda.body));
      case Application application -> join(
          decompile(application.function),
          roundBrackets(decompile(application.argument)));
      case Stdin stdin -> join(
          serializable("stdin"),
          roundBrackets(serializable("" + stdin.index)));
      default -> throw runtimeException("unknown expression: %s", expression);
    };
  }

  private static Serializable decompileBody(Expression body) {
    return switch (body) {
      case Lambda lambda -> decompile(body);
      default -> curlyBrackets(decompile(body));
    };
  }

  private static Serializable roundBrackets(Serializable serializable) {
    return brackets('(', ')', serializable);
  }

  private static Serializable curlyBrackets(Serializable serializable) {
    return brackets('{', '}', serializable);
  }

  private static Serializable quoteBrackets(Serializable serializable) {
    return brackets('\"', '\"', serializable);
  }

  private static Serializable brackets(
      char opening,
      char closing,
      Serializable serializable) {
    return join(
        serializable(opening),
        serializable,
        serializable(closing));
  }
}
