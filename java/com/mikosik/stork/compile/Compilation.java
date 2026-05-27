package com.mikosik.stork.compile;

import static com.mikosik.stork.common.ImmutableList.join;
import static com.mikosik.stork.common.ImmutableList.none;

import java.util.List;

import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.StorkFile;

public class Compilation {
  public final List<StorkFile> storkFiles;
  public final List<Definition> definitions;

  private Compilation(List<StorkFile> storkFiles, List<Definition> definitions) {
    this.storkFiles = storkFiles;
    this.definitions = definitions;
  }

  public static Compilation compilation() {
    return new Compilation(
        none(),
        none());
  }

  public Compilation storkFiles(List<StorkFile> storkFiles) {
    return new Compilation(
        join(this.storkFiles, storkFiles),
        definitions);
  }

  public Compilation definitions(List<Definition> definitions) {
    return new Compilation(
        storkFiles,
        join(this.definitions, definitions));
  }
}
