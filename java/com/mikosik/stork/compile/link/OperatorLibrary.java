package com.mikosik.stork.compile.link;

import static com.mikosik.stork.common.Sequence.flatten;
import static com.mikosik.stork.common.Sequence.toSequence;
import static com.mikosik.stork.model.Definition.definition;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Namespace.namespaceOf;
import static com.mikosik.stork.model.Variable.variable;
import static java.util.Arrays.stream;

import com.mikosik.stork.common.Sequence;
import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Namespace;
import com.mikosik.stork.model.Operator;

public class OperatorLibrary {
  public static Sequence<Definition> operatorLibrary() {
    return flatten(
        asLibrary(StackOperator.class),
        asLibrary(MathOperator.class));
  }

  private static Sequence<Definition> asLibrary(Class<? extends Operator> enumClass) {
    return stream(enumClass.getEnumConstants())
        .map(operator -> definition(
            identifier(NAMESPACE, variable(operator.toString())),
            operator))
        .collect(toSequence());
  }

  private static final Namespace NAMESPACE = namespaceOf("lang", "op");
}
