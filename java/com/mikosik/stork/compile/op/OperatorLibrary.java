package com.mikosik.stork.compile.op;

import static com.mikosik.stork.common.ImmutableList.join;
import static com.mikosik.stork.common.ImmutableList.list;
import static com.mikosik.stork.model.exp.Definition.definition;
import static com.mikosik.stork.model.exp.Identifier.identifier;
import static com.mikosik.stork.model.exp.Namespace.namespace;
import static com.mikosik.stork.model.exp.Variable.variable;
import static java.util.Arrays.stream;

import java.util.List;

import com.mikosik.stork.model.exp.Definition;
import com.mikosik.stork.model.exp.Namespace;
import com.mikosik.stork.model.exp.Operator;

public class OperatorLibrary {
  public static List<Definition> operatorLibrary() {
    return join(
        asLibrary(StackOperator.class),
        asLibrary(MathOperator.class));
  }

  private static List<Definition> asLibrary(Class<? extends Operator> enumClass) {
    return stream(enumClass.getEnumConstants())
        .map(operator -> definition(
            identifier(NAMESPACE, variable(operator.toString())),
            operator))
        .toList();
  }

  private static final Namespace NAMESPACE = namespace(list("lang", "op"));
}
