package com.mikosik.stork.testing;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Core;
import com.mikosik.stork.data.model.Expression;

public class Mock implements Core {
  private final String name;
  private final Chain<Expression> arguments;

  private Mock(String name, Chain<Expression> arguments) {
    this.name = name;
    this.arguments = arguments;
  }

  public static Mock mock(String name) {
    return new Mock(name, Chain.chain());
  }

  public Expression apply(Expression argument) {
    return new Mock(name, arguments.add(argument));
  }

  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(name);
    for (Expression argument : arguments.reverse()) {
      builder.append("(").append(argument).append(")");
    }
    return builder.toString();
  }
}
