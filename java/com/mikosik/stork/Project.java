package com.mikosik.stork;

import static com.mikosik.stork.common.io.Directory.directory;
import static com.mikosik.stork.common.io.File.file;
import static java.lang.String.format;

import java.nio.file.FileSystem;

import com.mikosik.stork.common.io.Directory;
import com.mikosik.stork.common.io.File;

public class Project {
  public final Directory root;
  public final Directory javaSourceDirectory;
  public final Directory coreLibraryDirectory;

  private Project(Directory root) {
    this.root = root;
    this.javaSourceDirectory = root.directory("java");
    this.coreLibraryDirectory = root.directory("core_library");
  }

  public static Project project(FileSystem fileSystem) {
    var classLoaderPath = Project.class.getClassLoader().getResource("").getPath();
    var classOrSourcePath = directory(fileSystem.getPath(classLoaderPath));
    var root = findRoot(classOrSourcePath);
    return new Project(root);
  }

  private static Directory findRoot(Directory directory) {
    while (!directory.directory(".git").exists()) {
      directory = directory.parent();
    }
    return directory;
  }

  public File sourceFileOf(Class<?> type) {
    var fileSystem = root.path.getFileSystem();
    var path = format("%s/%s.java",
        javaSourceDirectory,
        type.getName().replace('.', '/'));
    return file(fileSystem.getPath(path));
  }
}
