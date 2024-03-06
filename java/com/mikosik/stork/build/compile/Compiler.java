package com.mikosik.stork.build.compile;

import static com.mikosik.stork.build.compile.Compilation.compilation;
import static com.mikosik.stork.build.compile.Parser.parse;
import static com.mikosik.stork.common.Peekerator.peekerator;

import java.util.Iterator;

import com.mikosik.stork.model.Module;

public class Compiler {
  public Module compile(Iterator<Byte> input) {
    return compilation(peekerator(parse(input))).compile();
  }
}
