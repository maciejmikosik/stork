package com.mikosik.stork.model.runtime;

import com.mikosik.stork.tool.Runner;

public interface Core extends Expression {
  Expression run(Expression argument, Runner runner);
}
