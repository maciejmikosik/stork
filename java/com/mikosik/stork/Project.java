package com.mikosik.stork;

import static com.mikosik.stork.common.io.Directory.directory;
import static com.mikosik.stork.common.io.File.file;
import static java.lang.String.format;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;

import com.mikosik.stork.common.io.Directory;
import com.mikosik.stork.common.io.File;

public class Project {
  public final FileSystem fileSystem;
  public final Directory root;
  public final Directory javaSourceDirectory;
  public final Directory coreLibraryDirectory;

  private Project(FileSystem fileSystem, Directory root) {
    this.fileSystem = fileSystem;
    this.root = root;
    this.javaSourceDirectory = root.directory("java");
    this.coreLibraryDirectory = root.directory("core_library");
  }

  public static Project project() {
    var fileSystem = FileSystems.getDefault();
    var classLoaderPath = Project.class.getClassLoader().getResource("").getPath();
    var classOrSourcePath = directory(fileSystem.getPath(classLoaderPath));
    var root = findRoot(classOrSourcePath);
    return new Project(fileSystem, root);
  }

  private static Directory findRoot(Directory directory) {
    while (!directory.directory(".git").exists()) {
      directory = directory.parent();
    }
    return directory;
  }

  public File sourceFileOf(Class<?> type) {
    var path = format("%s/%s.java",
        javaSourceDirectory,
        type.getName().replace('.', '/'));
    return file(fileSystem.getPath(path));
  }
}
