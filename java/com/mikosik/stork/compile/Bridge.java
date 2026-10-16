package com.mikosik.stork.compile;

import static com.mikosik.stork.common.col.ImmutableList.single;
import static com.mikosik.stork.common.text.Strings.reverse;
import static com.mikosik.stork.model.exp.Application.application;
import static com.mikosik.stork.model.exp.Integer.integer;
import static com.mikosik.stork.model.exp.Namespace.namespace;
import static com.mikosik.stork.model.exp.Reference.reference;
import static com.mikosik.stork.model.exp.Variable.variable;

import java.math.BigInteger;

import com.mikosik.stork.model.exp.Expression;
import com.mikosik.stork.model.exp.Namespace;
import com.mikosik.stork.model.exp.Reference;

public class Bridge {
  private static final Namespace lang = namespace(single("lang"));

  private static final Namespace bool = lang.add("boolean");
  public static final Reference TRUE = reference(bool, variable("true"));
  public static final Reference FALSE = reference(bool, variable("false"));

  private static final Namespace stream = lang.add("stream");
  public static final Reference SOME = reference(stream, variable("some"));
  public static final Reference NONE = reference(stream, variable("none"));
  public static final Reference REDUCE_EAGER = reference(stream, variable("reduceEager"));

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
