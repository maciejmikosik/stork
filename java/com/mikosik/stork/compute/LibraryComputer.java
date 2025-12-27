package com.mikosik.stork.compute;

import static com.mikosik.stork.compute.Computation.computation;
import static com.mikosik.stork.problem.compute.FunctionMissing.functionMissing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Identifier;

public class LibraryComputer extends TypedComputer<Identifier> {
  private final Map<Identifier, Expression> table;

  private LibraryComputer(Map<Identifier, Expression> table) {
    super(Identifier.class);
    this.table = table;
  }

  public static Computer computer(List<Definition> library) {
    Map<Identifier, Expression> table = new HashMap<>();
    for (Definition definition : library) {
      table.put(definition.identifier, definition.body);
    }
    return new LibraryComputer(table);
  }

  public Computation compute(Identifier identifier, Stack stack) {
    if (table.containsKey(identifier)) {
      return computation(
          table.get(identifier),
          stack);
    } else {
      throw functionMissing(identifier);
    }
  }
}
