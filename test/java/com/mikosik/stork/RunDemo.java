package com.mikosik.stork;

import static com.mikosik.stork.common.Chains.chainOf;
import static com.mikosik.stork.common.InputOutput.pump;
import static com.mikosik.stork.lib.Modules.module;
import static com.mikosik.stork.main.Program.program;

import java.io.InputStream;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Module;
import com.mikosik.stork.main.Program;

public class RunDemo {
  public static void main(String[] args) {
    Chain<Module> modules = chainOf(
        module("function.stork"),
        module("stream.stork"),
        module("integer.stork"),
        module("boolean.stork"),
        module("program.stork"),
        module("demo.stork"));
    Program program = program("main", modules);
    InputStream input = program.run();
    pump(input, System.out);
    System.out.flush();
  }
}
