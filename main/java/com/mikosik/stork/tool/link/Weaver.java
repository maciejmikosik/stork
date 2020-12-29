package com.mikosik.stork.tool.link;

import com.mikosik.stork.model.Module;

public interface Weaver {
  Module weave(Module module);
}
