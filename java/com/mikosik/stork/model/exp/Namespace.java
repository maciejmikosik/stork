package com.mikosik.stork.model.exp;

import static com.mikosik.stork.common.ImmutableList.join;
import static com.mikosik.stork.common.ImmutableList.none;
import static com.mikosik.stork.common.ImmutableList.single;

import java.util.List;

import com.mikosik.stork.common.Model;

public class Namespace extends Model {
  public final List<String> components;

  private Namespace(List<String> components) {
    this.components = components;
  }

  public static Namespace namespaceRoot() {
    return namespace(none());
  }

  public static Namespace namespace(List<String> components) {
    return new Namespace(components);
  }

  public Namespace add(String component) {
    return namespace(join(components, single(component)));
  }
}
