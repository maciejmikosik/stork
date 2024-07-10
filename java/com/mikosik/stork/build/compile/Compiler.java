package com.mikosik.stork.build.compile;

import static com.mikosik.stork.build.compile.Compilation.compilation;
import static com.mikosik.stork.build.compile.Parser.parse;

import java.util.Iterator;

import com.mikosik.stork.model.Module;

// TODO propose syntax for arrays
// TODO allow direct lambda application (x){x}(arg)
// TODO allow snake notation
public class Compiler {
  public Module compile(Iterator<Byte> input) {
    return compilation(parse(input)).compile();
  }
}
