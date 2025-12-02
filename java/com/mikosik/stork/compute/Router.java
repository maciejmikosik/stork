package com.mikosik.stork.compute;

import java.util.LinkedList;
import java.util.List;

import com.mikosik.stork.model.Expression;

public class Router implements Computer {
  private final List<Route> routes = new LinkedList<>();

  private Router() {}

  public static Router router() {
    return new Router();
  }

  public Router route(
      Class<? extends Expression> type,
      Computer computer) {
    routes.add(new Route(type, computer));
    return this;
  }

  public Computation compute(Computation computation) {
    return routes.stream()
        .filter(route -> route.type.isInstance(computation.expression))
        .findFirst()
        .map(route -> route.computer.compute(computation))
        // TODO misconfigured computer, throw internal error
        .orElseThrow();
  }

  private static class Route {
    public final Class<? extends Expression> type;
    public final Computer computer;

    private Route(Class<? extends Expression> type, Computer computer) {
      this.type = type;
      this.computer = computer;
    }
  }
}
