package com.mikosik.stork.compile;

import static com.mikosik.stork.common.ImmutableList.join;
import static com.mikosik.stork.common.ImmutableList.none;

import java.util.List;

import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Source;

public class Compilation {
  public final List<Source> sources;
  public final List<Definition> definitions;

  private Compilation(List<Source> sources, List<Definition> definitions) {
    this.sources = sources;
    this.definitions = definitions;
  }

  public static Compilation compilation() {
    return new Compilation(
        none(),
        none());
  }

  public Compilation sources(List<Source> sources) {
    return new Compilation(
        join(this.sources, sources),
        definitions);
  }

  public Compilation definitions(List<Definition> definitions) {
    return new Compilation(
        sources,
        join(this.definitions, definitions));
  }
}
