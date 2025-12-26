package com.mikosik.stork.compile;

import static com.mikosik.stork.common.Collections.stream;
import static com.mikosik.stork.common.Sequence.toSequence;
import static com.mikosik.stork.common.Sequence.toSequenceThen;
import static com.mikosik.stork.common.io.Input.input;
import static com.mikosik.stork.common.io.InputOutput.walk;
import static com.mikosik.stork.model.Source.source;
import static java.util.Arrays.stream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

import com.mikosik.stork.common.Sequence;
import com.mikosik.stork.common.io.Directory;
import com.mikosik.stork.model.Namespace;
import com.mikosik.stork.model.Source;
import com.mikosik.stork.model.Source.Kind;

public class SourceReader {
  private SourceReader() {}

  public static SourceReader sourceReader() {
    return new SourceReader();
  }

  public Sequence<Source> read(Directory directory) {
    // TODO implement custom walking algorithm
    // TODO skip inaccessible nodes
    // TODO skip names with dots, like hidden directories/files
    return walk(directory.path)
        .filter(Files::isRegularFile)
        .filter(path -> maybeKindOf(path).isPresent())
        .map(file -> source(
            maybeKindOf(file).orElseThrow(),
            namespaceFor(directory.path, file.getParent()),
            input(file).readAllBytes()))
        .collect(toSequence());
  }

  private static Optional<Kind> maybeKindOf(Path path) {
    return stream(Kind.values())
        .filter(kind -> Objects.equals(
            kind.fileName,
            path.getFileName().toString()))
        .findFirst();
  }

  private static Namespace namespaceFor(Path directory, Path subdirectory) {
    return stream(subdirectory.iterator())
        .skip(directory.getNameCount())
        .map(path -> path.toString())
        .collect(toSequenceThen(Namespace::namespace));
  }
}
