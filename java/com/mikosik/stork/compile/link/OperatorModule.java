package com.mikosik.stork.compile.link;

import static com.mikosik.stork.common.Sequence.toSequenceThen;
import static com.mikosik.stork.compile.link.Modules.join;
import static com.mikosik.stork.model.Definition.definition;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Namespace.namespaceOf;
import static com.mikosik.stork.model.Variable.variable;
import static java.util.Arrays.stream;

import com.mikosik.stork.model.Module;
import com.mikosik.stork.model.Namespace;
import com.mikosik.stork.model.Operator;

public class OperatorModule {
  public static Module operatorModule() {
    return join(
        asModule(Manipulator.class),
        asModule(MathOperator.class));
  }

  private static Module asModule(Class<? extends Operator> enumClass) {
    return stream(enumClass.getEnumConstants())
        .map(operator -> definition(
            identifier(NAMESPACE, variable(operator.toString().toLowerCase())),
            operator))
        .collect(toSequenceThen(Module::module));
  }

  private static final Namespace NAMESPACE = namespaceOf("lang", "op");
}
