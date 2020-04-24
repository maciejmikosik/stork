package com.mikosik.stork.data.model;

public interface Verb extends Expression {
  Expression apply(Expression argument);
}
