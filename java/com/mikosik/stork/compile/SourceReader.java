package com.mikosik.stork.compile;

import static com.mikosik.stork.common.col.ImmutableList.join;
import static com.mikosik.stork.common.col.ImmutableList.single;
import static com.mikosik.stork.model.disk.StorkDirectory.storkDirectory;
import static com.mikosik.stork.model.exp.Namespace.namespaceRoot;

import java.util.List;

import com.mikosik.stork.common.io.Directory;
import com.mikosik.stork.model.disk.StorkDirectory;
import com.mikosik.stork.model.exp.Namespace;

// TODO rename to something better
public class SourceReader {
  public static final String IMPORT_FILENAME = "import.stork";
  public static final String SOURCE_FILENAME = "source.stork";

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
        // TODO report non-ascii characters
        directory.file(IMPORT_FILENAME)
            .tryInput()
            .readAllBytes(),
        directory.file(SOURCE_FILENAME)
            .tryInput()
            .readAllBytes());
  }
}
