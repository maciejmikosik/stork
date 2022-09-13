package com.mikosik.stork.model;

public interface Instruction extends Expression {
  Expression apply(Expression argument);
}
