package com.mikosik.stork.model;

import static com.mikosik.stork.common.Chain.chain;

import com.mikosik.stork.common.Chain;

public class Linkage {
  public final Chain<Identifier> links;

  private Linkage(Chain<Identifier> links) {
    this.links = links;
  }

  public static Linkage linkage() {
    return new Linkage(chain());
  }

  public static Linkage linkage(Chain<Identifier> links) {
    return new Linkage(links);
  }

  public Linkage add(Identifier link) {
    return linkage(links.add(link));
  }
}
