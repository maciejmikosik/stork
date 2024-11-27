package com.mikosik.stork;

import static com.mikosik.stork.Project.project;
import static com.mikosik.stork.common.proc.Javac.javac;
import static com.mikosik.stork.common.proc.Zip.zip;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.writeString;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.attribute.PosixFilePermission.OWNER_EXECUTE;
import static java.util.Comparator.reverseOrder;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import com.mikosik.stork.common.StandardOutput;
import com.mikosik.stork.common.io.InputOutput;

/**
 * Don't run this script using Eclipse. Eclipse puts compiled class files to bin
 * directory. At runtime it's hard to get access to java source files from
 * there.
 *
 * This is build of stork compiler. It is multi-file source-code program
 * (<a href="https://openjdk.org/jeps/458">JEP-458</a>). It requires java 22.
 *
 * TODO use {@link StandardOutput#out}
 *
 * TODO use helper methods from {@link InputOutput}
 */
public class Build {
  public static void main(String... args) throws Exception {
    var fileSystem = FileSystems.getDefault();
    var project = project(fileSystem);

    out("building stork binary");
    out("script: %s", project.sourceFileOf(Build.class));
    out("java source directory: %s", project.javaSourceDirectory);
    out("core library directory: %s", project.coreLibraryDirectory);

    var buildDirectory = newTempDirectory("stork_build_");
    out("temp build directory: %s", buildDirectory);
    var jarDirectory = createDirectories(buildDirectory.resolve("assembling_jar"));

    // compile sources
    javac()
        .sourcepath(project.javaSourceDirectory)
        .destination(jarDirectory)
        .sourcefile(project.sourceFileOf(Stork.class))
        .execute();

    // create manifest file
    writeString(
        createDirectories(jarDirectory.resolve("META-INF")).resolve("MANIFEST.MF"),
        "Main-Class: %s\n".formatted(Stork.class.getName()),
        UTF_8);

    // add core.star to jar
    zip()
        .sourceDirectory(project.coreLibraryDirectory)
        .destinationFile(jarDirectory.resolve("core.star"))
        .execute();

    // zip everything to jar file
    var jarFile = buildDirectory.resolve("stork.jar");
    zip()
        .sourceDirectory(jarDirectory)
        .destinationFile(jarFile)
        .execute();

    // add shebang header
    var storkBinaryFile = buildDirectory.resolve("stork");
    writeString(storkBinaryFile, "#!/usr/bin/java -jar\n", UTF_8);
    Files.write(storkBinaryFile, Files.readAllBytes(jarFile), CREATE, APPEND);

    // make file executable
    var permissions = Files.getPosixFilePermissions(storkBinaryFile);
    permissions.add(OWNER_EXECUTE);
    Files.setPosixFilePermissions(storkBinaryFile, permissions);

    // move stork product to home directory
    var productFile = fileSystem.getPath(System.getProperty("user.home"), "stork");
    var productAction = Files.exists(productFile)
        ? " (overwritten)"
        : "";
    Files.move(storkBinaryFile, productFile, REPLACE_EXISTING);
    out("created%s stork binary: %s".formatted(productAction, productFile));
  }

  private static Path newTempDirectory(String prefix) {
    var directory = createTempDirectory(prefix);
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      walkBottomToTop(directory).forEach(Build::delete);
    }));
    return directory;
  }

  private static Path createTempDirectory(String prefix) {
    try {
      return Files.createTempDirectory(prefix);
    } catch (IOException e) {
      throw unchecked(e);
    }
  }

  private static Stream<Path> walkBottomToTop(Path directory) {
    try {
      return Files.walk(directory)
          .sorted(reverseOrder());
    } catch (IOException e) {
      throw unchecked(e);
    }
  }

  public static void delete(Path path) {
    try {
      Files.delete(path);
    } catch (IOException e) {
      throw unchecked(e);
    }
  }

  private static UncheckedIOException unchecked(IOException e) {
    return new UncheckedIOException(e);
  }

  public static void out(String template, Object... items) {
    System.out.println(format(template, items));
  }
}
