package com.mikosik.stork.program;

import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Library;

public class Program {
  public final Expression main;
  public final Library library;

  private Program(Expression main, Library library) {
    this.main = main;
    this.library = library;
  }

  public static Program program(Expression main, Library library) {
    return new Program(main, library);
  }
}
