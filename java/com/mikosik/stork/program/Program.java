package com.mikosik.stork.program;

import com.mikosik.stork.common.Sequence;
import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Expression;

public class Program {
  public final Expression main;
  public final Sequence<Definition> library;

  private Program(Expression main, Sequence<Definition> library) {
    this.main = main;
    this.library = library;
  }

  public static Program program(Expression main, Sequence<Definition> library) {
    return new Program(main, library);
  }
}
