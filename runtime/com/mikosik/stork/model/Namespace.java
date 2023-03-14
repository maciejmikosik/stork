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
}
