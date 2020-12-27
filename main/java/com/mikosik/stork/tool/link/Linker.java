package com.mikosik.stork.tool.link;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.model.Module;

public interface Linker {
  Module link(Chain<Module> modules);
}
