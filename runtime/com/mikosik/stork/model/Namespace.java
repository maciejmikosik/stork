package com.mikosik.stork.model;

import com.mikosik.stork.common.Chain;

public class Namespace {
  public final Chain<String> path;

  private Namespace(Chain<String> path) {
    this.path = path;
  }

  public static Namespace namespace(Chain<String> path) {
    return new Namespace(path);
  }
}
