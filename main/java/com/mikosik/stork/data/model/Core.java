package com.mikosik.stork.data.model;

import com.mikosik.stork.tool.Runner;

public interface Core extends Expression {
  Expression run(Expression argument, Runner runner);
}
