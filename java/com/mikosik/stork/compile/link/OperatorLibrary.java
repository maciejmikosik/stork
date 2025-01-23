package com.mikosik.stork.compile.link;

import static com.mikosik.stork.common.Sequence.toSequenceThen;
import static com.mikosik.stork.model.Definition.definition;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Library.join;
import static com.mikosik.stork.model.Namespace.namespaceOf;
import static com.mikosik.stork.model.Variable.variable;
import static java.util.Arrays.stream;

import com.mikosik.stork.model.Library;
import com.mikosik.stork.model.Namespace;
import com.mikosik.stork.model.Operator;

public class OperatorLibrary {
  public static Library operatorLibrary() {
    return join(
        asLibrary(StackOperator.class),
        asLibrary(MathOperator.class));
  }

  private static Library asLibrary(Class<? extends Operator> enumClass) {
    return stream(enumClass.getEnumConstants())
        .map(operator -> definition(
            identifier(NAMESPACE, variable(operator.toString())),
            operator))
        .collect(toSequenceThen(Library::library));
  }

  private static final Namespace NAMESPACE = namespaceOf("lang", "op");
}
