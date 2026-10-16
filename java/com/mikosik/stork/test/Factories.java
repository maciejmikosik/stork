package com.mikosik.stork.test;

import static com.mikosik.stork.common.text.Strings.split;

import com.mikosik.stork.model.exp.Namespace;
import com.mikosik.stork.model.exp.Reference;
import com.mikosik.stork.model.exp.Variable;

public class Factories {
  public static Namespace namespace(String code) {
    return Namespace.namespace(split("/", code));
  }

  public static Reference reference(String code) {
    var split = split("/", code);
    return Reference.reference(
        Namespace.namespace(split.subList(0, split.size() - 1)),
        Variable.variable(split.getLast()));
  }
}
