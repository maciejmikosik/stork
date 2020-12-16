package com.mikosik.stork.tool.compile;

import com.mikosik.stork.common.PeekingInput;

public interface Compiler<T> {
  T compile(PeekingInput input);
}
