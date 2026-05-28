package com.mikosik.stork.compile;

import static com.mikosik.stork.common.ImmutableList.join;
import static com.mikosik.stork.common.ImmutableList.single;
import static com.mikosik.stork.model.Namespace.namespaceOf;
import static com.mikosik.stork.model.StorkDirectory.storkDirectory;
import static com.mikosik.stork.model.StorkFile.ImportFile.importFile;
import static com.mikosik.stork.model.StorkFile.SourceFile.sourceFile;

import java.util.List;

import com.mikosik.stork.common.io.Directory;
import com.mikosik.stork.model.Namespace;
import com.mikosik.stork.model.StorkDirectory;
import com.mikosik.stork.model.StorkFile.ImportFile;
import com.mikosik.stork.model.StorkFile.SourceFile;

// TODO rename to something better
public class SourceReader {
  private SourceReader() {}

  public static SourceReader sourceReader() {
    return new SourceReader();
  }

  public List<StorkDirectory> read(Directory directory) {
    return readDeep(directory, namespaceOf());
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
        importFile(
            namespace,
            directory.file(ImportFile.FILE_NAME)
                .tryInput()
                .readAllBytes()),
        sourceFile(
            namespace,
            directory.file(SourceFile.FILE_NAME)
                .tryInput()
                .readAllBytes()));
  }
}
