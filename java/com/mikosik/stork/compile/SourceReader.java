package com.mikosik.stork.compile;

import static com.mikosik.stork.common.Collections.stream;
import static com.mikosik.stork.common.io.Input.input;
import static com.mikosik.stork.common.io.InputOutput.walk;
import static com.mikosik.stork.model.Namespace.namespace;
import static com.mikosik.stork.model.StorkFile.ImportFile.importFile;
import static com.mikosik.stork.model.StorkFile.SourceFile.sourceFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import com.mikosik.stork.common.io.Directory;
import com.mikosik.stork.model.Namespace;
import com.mikosik.stork.model.StorkFile;
import com.mikosik.stork.model.StorkFile.ImportFile;
import com.mikosik.stork.model.StorkFile.SourceFile;

public class SourceReader {
  private SourceReader() {}

  public static SourceReader sourceReader() {
    return new SourceReader();
  }

  public List<StorkFile> read(Directory directory) {
    // TODO implement custom walking algorithm
    // TODO skip inaccessible nodes
    // TODO skip names with dots, like hidden directories/files
    return walk(directory.path)
        .filter(Files::isRegularFile)
        .map(file -> storkFile(
            file.getFileName().toString(),
            namespaceFor(directory.path, file.getParent()),
            input(file).readAllBytes()))
        .flatMap(Optional::stream)
        .toList();
  }

  private Optional<StorkFile> storkFile(
      String fileName,
      Namespace namespace,
      byte[] content) {
    return switch (fileName) {
      case SourceFile.FILE_NAME -> Optional.of(sourceFile(namespace, content));
      case ImportFile.FILE_NAME -> Optional.of(importFile(namespace, content));
      default -> Optional.empty();
    };
  }

  private static Namespace namespaceFor(Path directory, Path subdirectory) {
    var components = stream(subdirectory.iterator())
        .skip(directory.getNameCount())
        .map(path -> path.toString())
        .toList();
    return namespace(components);
  }
}
