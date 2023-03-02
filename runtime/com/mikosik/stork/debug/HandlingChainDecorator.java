package com.mikosik.stork.debug;

import org.logbuddy.Decorator;

import com.mikosik.stork.common.Chain;

public class HandlingChainDecorator implements Decorator {
  private final Decorator decorator;

  private HandlingChainDecorator(Decorator decorator) {
    this.decorator = decorator;
  }

  public static Decorator handlingChain(Decorator decorator) {
    return new HandlingChainDecorator(decorator);
  }

  public <T> T decorate(T decorable) {
    if (decorable instanceof Chain<?> chain) {
      return (T) chain.map(decorator::decorate);
    } else {
      return decorator.decorate(decorable);
    }
  }
}
