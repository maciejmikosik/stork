package com.mikosik.stork.tool.link;

import static com.mikosik.stork.tool.link.JoiningLinker.joiningLinker;

import java.util.function.Function;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Module;

public class WirableLinker implements Linker {
  private final Linker linker;

  private WirableLinker(Linker linker) {
    this.linker = linker;
  }

  public static WirableLinker linker() {
    return new WirableLinker(joiningLinker());
  }

  public WirableLinker wire(Function<Linker, Linker> wrapper) {
    return new WirableLinker(wrapper.apply(linker));
  }

  public WirableLinker unique() {
    return wire(UniqueLinker::unique);
  }

  public WirableLinker coherent() {
    return wire(CoherentLinker::coherent);
  }

  public Module link(Chain<Module> modules) {
    return linker.link(modules);
  }
}
