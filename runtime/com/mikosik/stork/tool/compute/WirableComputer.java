package com.mikosik.stork.tool.compute;

import java.util.function.Function;

import com.mikosik.stork.common.io.Output;
import com.mikosik.stork.model.Computation;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.tool.decompile.Decompiler;

public class WirableComputer implements Computer {
  private final Computer delegate;

  private WirableComputer(Computer delegate) {
    this.delegate = delegate;
  }

  public static WirableComputer computer() {
    return new WirableComputer(computation -> computation);
  }

  public WirableComputer wire(Function<Computer, Computer> wrapper) {
    return new WirableComputer(wrapper.apply(delegate));
  }

  public WirableComputer moduling(Module module) {
    return wire(computer -> ModulingComputer.moduling(module, computer));
  }

  public WirableComputer innate() {
    return wire(InnateComputer::innate);
  }

  public WirableComputer combinatorial() {
    return wire(CombinatorialComputer::combinatorial);
  }

  public WirableComputer stacking() {
    return wire(StackingComputer::stacking);
  }

  public WirableComputer caching() {
    return wire(CachingComputer::caching);
  }

  public WirableComputer interruptible() {
    return wire(InterruptibleComputer::interruptible);
  }

  public WirableComputer logging(Output stream, Decompiler decompiler) {
    return wire(computer -> LoggingComputer.logging(stream, decompiler, computer));
  }

  public WirableComputer progressing() {
    return wire(ProgressingComputer::progressing);
  }

  public Computation compute(Computation computation) {
    return delegate.compute(computation);
  }

  public Computer looping() {
    return wire(LoopingComputer::looping);
  }
}