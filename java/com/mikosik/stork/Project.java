package com.mikosik.stork;

import static java.nio.file.Files.isDirectory;

import java.nio.file.FileSystem;
import java.nio.file.Path;

public class Project {
  public final Path root;
  public final Path javaSourceDirectory;
  public final Path coreLibraryDirectory;

  private Project(Path root, Path javaSourceDirectory, Path coreLibraryDirectory) {
    this.root = root;
    this.javaSourceDirectory = javaSourceDirectory;
    this.coreLibraryDirectory = coreLibraryDirectory;
  }

  public static Project project(FileSystem fileSystem) {
    var thisFileName = Project.class.getSimpleName() + ".java";
    var thisFile = fileSystem.getPath(Project.class.getResource(thisFileName).getFile());
    var root = findRoot(thisFile);
    var javaSourceDirectory = root.resolve("java");
    var coreLibraryDirectory = root.resolve("core_library");
    return new Project(root, javaSourceDirectory, coreLibraryDirectory);
  }

  private static Path findRoot(Path path) {
    var root = path.getParent();
    while (!isDirectory(root.resolve(".git"))) {
      root = root.getParent();
    }
    return root;
  }

  public Path sourceFileOf(Class<?> type) {
    var sourceFileName = type.getSimpleName() + ".java";
    var file = type.getResource(sourceFileName).getFile();
    return root.getFileSystem().getPath(file);
  }
}
