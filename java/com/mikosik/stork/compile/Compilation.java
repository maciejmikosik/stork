package com.mikosik.stork.compile;

import java.util.LinkedList;
import java.util.List;

import com.mikosik.stork.common.io.Directory;
import com.mikosik.stork.model.Library;

public class Compilation {
  public final List<Directory> sources = new LinkedList<>();
  public final List<Library> libraries = new LinkedList<>();

  public static Compilation compilation() {
    return new Compilation();
  }

  public Compilation source(Directory directory) {
    sources.add(directory);
    return this;
  }

  public Compilation library(Library library) {
    libraries.add(library);
    return this;
  }
}
