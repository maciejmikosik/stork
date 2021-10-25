package com.mikosik.stork.tool.compile;

import com.mikosik.stork.common.Input;

public interface Compiler<T> {
  T compile(Input input);
}
