package com.mikosik.stork.compile.link;

import static com.mikosik.stork.common.Sequence.toSequence;
import static com.mikosik.stork.model.Library.library;

import java.util.Arrays;
import java.util.List;

import com.mikosik.stork.model.Library;

public class Libraries {
  public static Library join(List<Library> libraries) {
    return library(libraries.stream()
        .flatMap(library -> library.definitions.stream())
        .collect(toSequence()));
  }

  public static Library join(Library... libraries) {
    return join(Arrays.asList(libraries));
  }
}
