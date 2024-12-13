package com.mikosik.stork.model;

import com.mikosik.stork.common.Sequence;

public class Linkage {
  public final Sequence<Link> links;

  private Linkage(Sequence<Link> links) {
    this.links = links;
  }

  public static Linkage linkage(Sequence<Link> links) {
    return new Linkage(links);
  }
}
