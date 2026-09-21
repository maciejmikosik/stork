package com.mikosik.stork.problem.compile.importing;

import static com.mikosik.stork.compile.Problem.problem;

import com.mikosik.stork.compile.Problem;
import com.mikosik.stork.model.exp.Namespace;

public class MalformedImportLine {
  public static Problem malformedImportLine(
      Namespace namespace,
      String line) {
    return problem("malformed import")
        .location(namespace)
        .object(line)
        .build();
  }
}
