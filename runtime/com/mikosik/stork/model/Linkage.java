package com.mikosik.stork.model;

import static com.mikosik.stork.common.Chain.chain;

import com.mikosik.stork.common.Chain;

public class Linkage {
  public final Chain<Link> links;

  private Linkage(Chain<Link> links) {
    this.links = links;
  }

  public static Linkage linkage() {
    return new Linkage(chain());
  }

  public static Linkage linkage(Chain<Link> links) {
    return new Linkage(links);
  }

  public Linkage add(Link link) {
    return linkage(links.add(link));
  }
}
