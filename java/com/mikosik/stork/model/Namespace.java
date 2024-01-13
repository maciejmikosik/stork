package com.mikosik.stork.model;

import static com.mikosik.stork.common.Sequence.sequence;

import java.util.List;

public class Namespace {
  public final List<String> path;

  private Namespace(List<String> path) {
    this.path = path;
  }

  public static Namespace namespace(List<String> path) {
    return new Namespace(sequence(path));
  }

  public boolean equals(Object object) {
    return object instanceof Namespace namespace
        && equals(namespace);
  }

  private boolean equals(Namespace that) {
    return path.equals(that.path);
  }

  public int hashCode() {
    return path.hashCode();
  }
}
