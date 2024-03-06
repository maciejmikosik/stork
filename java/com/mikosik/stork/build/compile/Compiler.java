package com.mikosik.stork.build.compile;

import static com.mikosik.stork.build.compile.Compilation.compilation;
import static com.mikosik.stork.build.compile.Parser.parse;
import static com.mikosik.stork.common.PeekableIterator.peekable;

import java.util.Iterator;

import com.mikosik.stork.model.Module;

public class Compiler {
  public Module compile(Iterator<Byte> input) {
    return compilation(peekable(parse(input))).compile();
  }
}
