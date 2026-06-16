package com.mikosik.stork.compile.link;

import static com.mikosik.stork.common.ImmutableList.single;
import static com.mikosik.stork.common.Strings.reverse;
import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Integer.integer;
import static com.mikosik.stork.model.Namespace.namespace;
import static com.mikosik.stork.model.Variable.variable;

import java.math.BigInteger;

import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Namespace;

public class Bridge {
  private static final Namespace lang = namespace(single("lang"));

  private static final Namespace bool = lang.add("boolean");
  public static final Identifier TRUE = identifier(bool, variable("true"));
  public static final Identifier FALSE = identifier(bool, variable("false"));

  private static final Namespace stream = lang.add("stream");
  public static final Identifier SOME = identifier(stream, variable("some"));
  public static final Identifier NONE = identifier(stream, variable("none"));
  public static final Identifier REDUCE_EAGER = identifier(stream, variable("reduceEager"));

  public static Expression stork(String string) {
    Expression stream = NONE;
    for (char character : reverse(string).toCharArray()) {
      stream = some(integer(character), stream);
    }
    return stream;
  }

  public static Expression some(Expression head, Expression tail) {
    return application(SOME, head, tail);
  }

  public static Expression stork(Boolean value) {
    return value ? TRUE : FALSE;
  }

  public static Expression stork(BigInteger value) {
    return integer(value);
  }
}
