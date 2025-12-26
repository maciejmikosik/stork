package com.mikosik.stork.compile;

import static com.mikosik.stork.compile.SourceReader.sourceReader;
import static java.util.stream.Collectors.toCollection;

import java.util.LinkedList;
import java.util.List;

import com.mikosik.stork.common.io.Directory;
import com.mikosik.stork.model.Library;
import com.mikosik.stork.model.Source;

public class Compilation {
  private final List<Directory> sourceDirectories = new LinkedList<>();
  private final List<Source> sources = new LinkedList<>();
  public final List<Library> libraries = new LinkedList<>();

  public static Compilation compilation() {
    return new Compilation();
  }

  public Compilation source(Directory directory) {
    sourceDirectories.add(directory);
    return this;
  }

  public Compilation source(Source source) {
    sources.add(source);
    return this;
  }

  public List<Source> sources() {
    var sourceReader = sourceReader();
    var allSources = sourceDirectories.stream()
        .flatMap(directory -> sourceReader.read(directory).stream())
        .collect(toCollection(LinkedList::new));
    allSources.addAll(sources);
    return allSources;
  }

  public Compilation library(Library library) {
    libraries.add(library);
    return this;
  }
}
