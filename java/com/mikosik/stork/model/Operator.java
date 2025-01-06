package com.mikosik.stork.model;

import java.util.Optional;

import com.mikosik.stork.compute.Computation;
import com.mikosik.stork.compute.Stack;

public interface Operator extends Expression {
  Optional<Computation> compute(Stack stack);
}
