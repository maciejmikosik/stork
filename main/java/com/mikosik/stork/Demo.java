package com.mikosik.stork;

import static com.mikosik.stork.Program.program;
import static com.mikosik.stork.common.Chains.chainOf;
import static com.mikosik.stork.lib.Modules.module;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Module;

public class Demo {
  public static void main(String[] args) {
    Chain<Module> modules = chainOf(
        module("function.stork"),
        module("stream.stork"),
        module("integer.stork"),
        module("boolean.stork"),
        module("demo.stork"));
    Program program = program("main", modules);
    program.run();
  }
}
