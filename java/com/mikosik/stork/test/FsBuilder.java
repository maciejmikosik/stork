package com.mikosik.stork.test;

import com.mikosik.stork.common.io.Directory;

// TODO implement some kind of in-memory filesystem
public class FsBuilder {
  public final Directory directory;

  private FsBuilder(Directory directory) {
    this.directory = directory;
  }

  public static FsBuilder fsBuilder(Directory directory) {
    return new FsBuilder(directory);
  }

  public FsBuilder file(String path, byte[] content) {
    directory.file(path)
        .createDeep()
        .append(content);
    return this;
  }

  public FsBuilder delete() {
    directory.deleteRecursively();
    return this;
  }
}
