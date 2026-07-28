package com.mikosik.stork.test;

import static com.mikosik.stork.common.Strings.split;

import com.mikosik.stork.model.exp.Identifier;
import com.mikosik.stork.model.exp.Namespace;
import com.mikosik.stork.model.exp.Variable;

public class Factories {
  public static Namespace namespace(String code) {
    return Namespace.namespace(split("/", code));
  }

  public static Identifier identifier(String code) {
    var split = split("/", code);
    return Identifier.identifier(
        Namespace.namespace(split.subList(0, split.size() - 1)),
        Variable.variable(split.getLast()));
  }
}
