package com.mikosik.stork.tool.compile;

import static com.mikosik.stork.common.Output.output;
import static com.mikosik.stork.tool.compile.Printer.printer;
import static java.nio.charset.StandardCharsets.US_ASCII;

import java.io.ByteArrayOutputStream;

import com.mikosik.stork.tool.common.Scope;

public class Decompiler {
  private final Scope scope;

  private Decompiler(Scope scope) {
    this.scope = scope;
  }

  public static Decompiler decompiler(Scope scope) {
    return new Decompiler(scope);
  }

  public String decompile(Object code) {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    printer(scope, output(buffer)).print(code);
    return new String(buffer.toByteArray(), US_ASCII);
  }
}
