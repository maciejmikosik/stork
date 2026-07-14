package com.mikosik.stork.debug;

import static com.mikosik.stork.common.ImmutableList.join;
import static com.mikosik.stork.common.ImmutableList.single;
import static com.mikosik.stork.common.Throwables.runtimeException;
import static com.mikosik.stork.common.io.Serializables.join;
import static com.mikosik.stork.common.io.Serializables.serializable;
import static java.lang.String.join;

import com.mikosik.stork.common.io.Serializable;
import com.mikosik.stork.model.Application;
import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Integer;
import com.mikosik.stork.model.Lambda;
import com.mikosik.stork.model.Operator;
import com.mikosik.stork.model.Parameter;
import com.mikosik.stork.model.Quote;
import com.mikosik.stork.model.Variable;

public class Decompiler {
  public static Serializable decompile(Definition definition) {
    return join(
        serializable(print(definition.identifier)),
        decompileBody(definition.body));
  }

  public static Serializable decompile(Expression expression) {
    return switch (expression) {
      case Variable variable -> serializable(variable.name);
      case Identifier identifier -> serializable(print(identifier));
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

  // TODO rewrite using serializables
  private static String print(Identifier identifier) {
    return join("/", join(
        identifier.namespace.components,
        single(identifier.variable.name)));
  }
}
