package com.mikosik.stork.compile;

import static com.mikosik.stork.common.io.Ascii.ascii;
import static com.mikosik.stork.common.io.Buffer.newBuffer;

import com.mikosik.stork.common.io.Buffer;
import com.mikosik.stork.common.io.Output;
import com.mikosik.stork.model.Model;

public class Decompiler {
  private Decompiler() {}

  public static Decompiler decompiler() {
    return new Decompiler();
  }

  public Decompilation to(Output output) {
    return new Decompilation(output);
  }

  public String decompile(Model model) {
    Buffer buffer = newBuffer();
    to(buffer.asOutput()).decompile(model);
    return ascii(buffer.bytes());
  }
}
