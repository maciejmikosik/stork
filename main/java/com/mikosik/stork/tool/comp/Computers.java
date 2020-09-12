package com.mikosik.stork.tool.comp;

import static com.mikosik.stork.tool.comp.DefaultComputer.computer;
import static com.mikosik.stork.tool.comp.InterruptibleComputer.interruptible;
import static com.mikosik.stork.tool.comp.OpcodingComputer.opcoding;
import static com.mikosik.stork.tool.comp.StackingComputer.stacking;
import static com.mikosik.stork.tool.comp.SubstitutingComputer.substituting;
import static com.mikosik.stork.tool.comp.VariableComputer.variable;

import com.mikosik.stork.data.model.Module;

public class Computers {
  public static Computer steppingComputer(Module module) {
    return interruptible(stacking(substituting(opcoding(variable(module, computer())))));
  }
}
