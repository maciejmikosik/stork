package com.mikosik.stork.debug;

import static com.mikosik.stork.common.Chain.chainFrom;
import static java.util.stream.Collectors.toList;

import java.util.List;

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
      List<?> decorated = chain.stream()
          .map(decorator::decorate)
          .collect(toList());
      return (T) chainFrom(decorated);
    } else {
      return decorator.decorate(decorable);
    }
  }
}
