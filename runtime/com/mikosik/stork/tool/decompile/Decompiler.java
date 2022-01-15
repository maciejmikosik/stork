package com.mikosik.stork.tool.decompile;

import com.mikosik.stork.common.io.Output;
import com.mikosik.stork.model.Model;

public class Decompiler {
  private boolean local = false;

  private Decompiler(boolean local) {
    this.local = local;
  }

  public static Decompiler decompiler() {
    return new Decompiler(false);
  }

  public Decompiler local() {
    return new Decompiler(true);
  }

  public void decompile(Output output, Model model) {
    new Decompilation(local, output)
        .decompile(model);
  }
}
