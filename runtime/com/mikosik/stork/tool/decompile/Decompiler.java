package com.mikosik.stork.tool.decompile;

import static com.mikosik.stork.common.io.Buffer.newBuffer;
import static java.nio.charset.StandardCharsets.US_ASCII;

import com.mikosik.stork.common.io.Buffer;
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

  public Decompilation to(Output output) {
    return new Decompilation(local, output);
  }

  public String decompile(Model model) {
    Buffer buffer = newBuffer();
    to(buffer.asOutput()).decompile(model);
    return new String(buffer.bytes(), US_ASCII);
  }
}
