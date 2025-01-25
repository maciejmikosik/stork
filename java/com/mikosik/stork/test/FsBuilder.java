package com.mikosik.stork.test;

import java.util.HashMap;
import java.util.Map;

import com.mikosik.stork.common.io.Directory;

// TODO implement some kind of in-memory filesystem
public class FsBuilder {
  private Directory directory;
  private final Map<String, byte[]> files = new HashMap<>();

  private FsBuilder() {}

  public static FsBuilder fsBuilder() {
    return new FsBuilder();
  }

  public FsBuilder directory(Directory directory) {
    this.directory = directory;
    return this;
  }

  public FsBuilder file(String path, byte[] content) {
    files.put(path, content);
    return this;
  }

  public FsBuilder create() {
    files.entrySet().stream()
        .forEach(entry -> {
          var path = entry.getKey();
          var content = entry.getValue();
          directory.file(path)
              .createDeep()
              .append(content);
        });
    return this;
  }

  public FsBuilder delete() {
    directory.deleteRecursively();
    return this;
  }
}
