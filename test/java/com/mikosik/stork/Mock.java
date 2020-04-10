package com.mikosik.stork;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.model.runtime.Core;
import com.mikosik.stork.model.runtime.Expression;
import com.mikosik.stork.tool.Runner;

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

  public Expression run(Expression argument, Runner runner) {
    return new Mock(name, arguments.add(runner.run(argument)));
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
