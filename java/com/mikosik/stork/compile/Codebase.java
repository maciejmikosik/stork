package com.mikosik.stork.compile;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;

import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.StorkFile;

public class Codebase {
  public final List<StorkFile> files;
  public final List<Definition> dependencies;

  private Codebase(
      List<StorkFile> files,
      List<Definition> definitions) {
    this.files = files;
    this.dependencies = definitions;
  }

  public static CodebaseBuilder codebase() {
    return new CodebaseBuilder();
  }

  public static class CodebaseBuilder {
    private final List<StorkFile> files = new ArrayList<>();
    private final List<Definition> dependencies = new ArrayList<>();

    public CodebaseBuilder files(List<StorkFile> files) {
      this.files.addAll(files);
      return this;
    }

    public CodebaseBuilder dependencies(List<Definition> dependencies) {
      this.dependencies.addAll(dependencies);
      return this;
    }

    public Codebase build() {
      return new Codebase(
          unmodifiableList(files),
          unmodifiableList(dependencies));
    }
  }
}
