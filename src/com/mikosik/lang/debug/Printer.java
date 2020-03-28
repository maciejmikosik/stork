package com.mikosik.lang.debug;

import static java.lang.String.format;

import com.mikosik.lang.model.Alias;
import com.mikosik.lang.model.Application;
import com.mikosik.lang.model.Expression;

public class Printer {
  private Printer() {}

  public static Printer printer() {
    return new Printer();
  }

  public String print(Expression expression) {
    if (expression instanceof Alias) {
      Alias alias = (Alias) expression;
      return alias.name;
    } else if (expression instanceof Application) {
      Application application = (Application) expression;
      return format("%s(%s)", print(application.function), print(application.argument));
    } else {
      throw new RuntimeException();
    }
  }
}
