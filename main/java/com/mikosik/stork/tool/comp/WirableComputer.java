package com.mikosik.stork.tool.comp;

import static com.mikosik.stork.tool.common.Computations.print;

import java.io.OutputStream;
import java.util.function.Function;

import com.mikosik.stork.data.model.Module;
import com.mikosik.stork.data.model.comp.Computation;

public class WirableComputer implements Computer {
  private final Computer delegate;

  private WirableComputer(Computer delegate) {
    this.delegate = delegate;
  }

  public static WirableComputer computer() {
    return new WirableComputer(computation -> {
      throw new RuntimeException(print(computation));
    });
  }

  public WirableComputer wire(Function<Computer, Computer> wrapper) {
    return new WirableComputer(wrapper.apply(delegate));
  }

  public WirableComputer module(Module module) {
    return new WirableComputer(VariableComputer.variable(module, delegate));
  }

  public WirableComputer opcoding() {
    return new WirableComputer(OpcodingComputer.opcoding(delegate));
  }

  public WirableComputer substituting() {
    return new WirableComputer(SubstitutingComputer.substituting(delegate));
  }

  public WirableComputer stacking() {
    return new WirableComputer(StackingComputer.stacking(delegate));
  }

  public WirableComputer interruptible() {
    return new WirableComputer(InterruptibleComputer.interruptible(delegate));
  }

  public WirableComputer logging(OutputStream stream) {
    return new WirableComputer(LoggingComputer.logging(stream, delegate));
  }

  public WirableComputer exhausted() {
    return new WirableComputer(ExhaustedComputer.exhausted(delegate));
  }

  public WirableComputer humane() {
    return new WirableComputer(HumaneComputer.humane(delegate));
  }

  public WirableComputer looping() {
    return new WirableComputer(LoopingComputer.looping(delegate));
  }

  public Computation compute(Computation computation) {
    return delegate.compute(computation);
  }
}
