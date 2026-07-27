package com.mikosik.stork.compile;

import static com.mikosik.stork.common.ImmutableList.join;
import static com.mikosik.stork.common.ImmutableList.single;
import static com.mikosik.stork.model.disk.StorkDirectory.storkDirectory;
import static com.mikosik.stork.model.exp.Namespace.namespaceRoot;

import java.util.List;

import com.mikosik.stork.common.io.Directory;
import com.mikosik.stork.model.disk.StorkDirectory;
import com.mikosik.stork.model.exp.Namespace;

// TODO rename to something better
public class SourceReader {
  private SourceReader() {}

  public static SourceReader sourceReader() {
    return new SourceReader();
  }

  public List<StorkDirectory> read(Directory directory) {
    return readDeep(directory, namespaceRoot());
  }

  private static List<StorkDirectory> readDeep(
      Directory directory,
      Namespace namespace) {
    return join(
        single(readCurrent(directory, namespace)),
        directory.directories()
            // TODO skip names with illegal characters
            // TODO skip inaccessible directories
            .map(subdirectory -> readDeep(
                subdirectory,
                namespace.add(subdirectory.name())))
            .flatMap(List::stream)
            .toList());
  }

  private static StorkDirectory readCurrent(
      Directory directory,
      Namespace namespace) {
    return storkDirectory(
        namespace,
        directory.file("import.stork")
            .tryInput()
            .readAllBytes(),
        directory.file("source.stork")
            .tryInput()
            .readAllBytes());
  }
}
