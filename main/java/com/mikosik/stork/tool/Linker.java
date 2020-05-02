package com.mikosik.stork.tool;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Module;

public interface Linker {

  Module link(Chain<Module> modules);

}
