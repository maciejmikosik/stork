package com.mikosik.stork.tool.link;

import static com.mikosik.stork.model.Module.module;
import static java.util.Arrays.stream;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.model.Module;

public class Linkers {
  public static Linker linker(Weaver preWeaver, Weaver postWeaver) {
    return modules -> postWeaver.weave(join(modules.map(preWeaver::weave)));
  }

  public static Module join(Chain<Module> modules) {
    return module(modules.flatMap(module -> module.definitions));
  }

  public static Weaver compose(Weaver weaver, Weaver... weavers) {
    return stream(weavers).reduce(weaver, Linkers::compose);
  }

  private static Weaver compose(Weaver first, Weaver second) {
    return module -> second.weave(first.weave(module));
  }
}
