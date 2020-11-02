package com.mikosik.stork.data.model;

import com.mikosik.stork.data.model.comp.Computation;
import com.mikosik.stork.data.model.comp.Stack;

public interface Opcode extends Expression {
  Computation compute(Stack stack);
}
