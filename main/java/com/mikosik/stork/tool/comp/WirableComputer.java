package com.mikosik.stork.tool.comp;

import java.io.OutputStream;
import java.util.function.Function;

import com.mikosik.stork.data.model.Module;
import com.mikosik.stork.data.model.comp.Computation;
import com.mikosik.stork.tool.Decompiler;

public class WirableComputer implements Computer {
  private final Computer delegate;

  private WirableComputer(Computer delegate) {
    this.delegate = delegate;
  }

  public static WirableComputer computer() {
    return new WirableComputer(computation -> {
      throw new RuntimeException();
    });
  }

  public WirableComputer wire(Function<Computer, Computer> wrapper) {
    return new WirableComputer(wrapper.apply(delegate));
  }

  public WirableComputer moduling(Module module) {
    return wire(computer -> ModulingComputer.moduling(module, computer));
  }

  public WirableComputer opcoding() {
    return wire(OpcodingComputer::opcoding);
  }

  public WirableComputer substituting() {
    return wire(SubstitutingComputer::substituting);
  }

  public WirableComputer stacking() {
    return wire(StackingComputer::stacking);
  }

  public WirableComputer interruptible() {
    return wire(InterruptibleComputer::interruptible);
  }

  public WirableComputer logging(OutputStream stream, Decompiler decompiler) {
    return wire(computer -> LoggingComputer.logging(stream, decompiler, computer));
  }

  public WirableComputer exhausted() {
    return wire(ExhaustedComputer::exhausted);
  }

  public WirableComputer humane() {
    return wire(HumaneComputer::humane);
  }

  public WirableComputer looping() {
    return wire(LoopingComputer::looping);
  }

  public Computation compute(Computation computation) {
    return delegate.compute(computation);
  }
}
