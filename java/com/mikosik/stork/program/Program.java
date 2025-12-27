package com.mikosik.stork.program;

import java.util.List;

import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Expression;

public class Program {
  public final Expression main;
  public final List<Definition> library;

  private Program(Expression main, List<Definition> library) {
    this.main = main;
    this.library = library;
  }

  public static Program program(Expression main, List<Definition> library) {
    return new Program(main, library);
  }
}
