package com.mikosik.stork.compile;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;

import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.StorkDirectory;

public class Codebase {
  public final List<StorkDirectory> directories;
  public final List<Definition> dependencies;

  private Codebase(
      List<StorkDirectory> directories,
      List<Definition> definitions) {
    this.directories = directories;
    this.dependencies = definitions;
  }

  public static CodebaseBuilder codebase() {
    return new CodebaseBuilder();
  }

  public static class CodebaseBuilder {
    private final List<StorkDirectory> directories = new ArrayList<>();
    private final List<Definition> dependencies = new ArrayList<>();

    public CodebaseBuilder directories(List<StorkDirectory> directories) {
      this.directories.addAll(directories);
      return this;
    }

    public CodebaseBuilder dependencies(List<Definition> dependencies) {
      this.dependencies.addAll(dependencies);
      return this;
    }

    public Codebase build() {
      return new Codebase(
          unmodifiableList(directories),
          unmodifiableList(dependencies));
    }
  }
}
