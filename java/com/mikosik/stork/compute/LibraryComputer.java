package com.mikosik.stork.compute;

import static com.mikosik.stork.compute.Computation.computation;
import static com.mikosik.stork.compute.err.ComputerException.exception;
import static com.mikosik.stork.compute.err.FunctionMissing.functionMissing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mikosik.stork.model.exp.Definition;
import com.mikosik.stork.model.exp.Expression;
import com.mikosik.stork.model.exp.Reference;

public class LibraryComputer extends TypedComputer<Reference> {
  private final Map<Reference, Expression> table;

  private LibraryComputer(Map<Reference, Expression> table) {
    super(Reference.class);
    this.table = table;
  }

  public static TypedComputer<Reference> computer(List<Definition> library) {
    Map<Reference, Expression> table = new HashMap<>();
    for (Definition definition : library) {
      table.put(definition.reference, definition.body);
    }
    return new LibraryComputer(table);
  }

  public Computation compute(Reference reference, Stack stack) {
    if (table.containsKey(reference)) {
      return computation(
          table.get(reference),
          stack);
    } else {
      throw exception(functionMissing(reference));
    }
  }
}
