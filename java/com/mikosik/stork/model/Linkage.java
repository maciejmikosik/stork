package com.mikosik.stork.model;

import java.util.List;

public class Linkage {
  public final List<Link> links;

  private Linkage(List<Link> links) {
    this.links = links;
  }

  public static Linkage linkage(List<Link> links) {
    return new Linkage(links);
  }
}
