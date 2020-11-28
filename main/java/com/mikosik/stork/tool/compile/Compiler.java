package com.mikosik.stork.tool.compile;

import static com.mikosik.stork.common.InputOutput.readAllBytes;
import static com.mikosik.stork.tool.compile.Modeler.modelModule;
import static com.mikosik.stork.tool.compile.Parser.parse;
import static java.nio.charset.StandardCharsets.US_ASCII;

import java.io.InputStream;

import com.mikosik.stork.data.model.Module;

public class Compiler {
  private Compiler() {}

  public static Compiler compiler() {
    return new Compiler();
  }

  public Module compile(InputStream input) {
    byte[] bytes = readAllBytes(input);
    String source = new String(bytes, US_ASCII);
    return modelModule(parse(source));
  }
}
