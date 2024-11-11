package com.mikosik.stork.build;

import static com.mikosik.stork.build.Build.out;
import static com.mikosik.stork.common.io.InputOutput.path;
import static java.lang.String.format;
import static java.lang.String.join;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.writeString;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.util.Arrays.asList;
import static java.util.Comparator.reverseOrder;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import com.mikosik.stork.Stork;
import com.mikosik.stork.common.StandardOutput;
import com.mikosik.stork.common.io.InputOutput;

/**
 * Don't run this script using Eclipse. Eclipse puts compiled class files to bin
 * directory. At runtime it's hard to get access to java source files from
 * there.
 *
 * This is build of stork compiler. It is single script file (JEP-330). It can't
 * reference other files because it is compatible with java 21. Using external
 * classes requires java 22 (JEP-458).
 *
 * TODO use {@link StandardOutput#out}
 *
 * TODO use helper methods from {@link InputOutput}
 */
//
public class Build {
  public static void main(String... args) throws Exception {
    var thisFile = sourceFileOf(Build.class);
    out("building stork shebang binary by running script\n  %s", thisFile);

    // TODO find better way of locating java source directory
    var javaSourceDirectory = thisFile
        .getParent().getParent().getParent().getParent().getParent();
    out("java source directory: %s", javaSourceDirectory);
    var buildDirectory = newTempDirectory("stork_build_");
    out("created build directory: %s", buildDirectory);
    var jarDirectory = createDirectories(buildDirectory.resolve("assembling_jar"));

    // compile sources
    var exitCode = new Javac()
        .workingDirectory(javaSourceDirectory)
        .sourcepath(".")
        .destination(jarDirectory)
        .sourcefile(sourceFileOf(Stork.class))
        .start()
        .waitFor();
    checkExitCode(exitCode);

    // create manifest file
    writeString(
        createDirectories(jarDirectory.resolve("META-INF")).resolve("MANIFEST.MF"),
        "Main-Class: %s\n".formatted(Stork.class.getName()),
        UTF_8);

    // zip everything to jar file
    var jarFile = buildDirectory.resolve("stork.jar");
    exitCode = new Zip()
        .sourceDirectory(jarDirectory)
        .destinationFile(jarFile)
        .start()
        .waitFor();
    checkExitCode(exitCode);

    // add shebang header
    var storkBinaryFile = buildDirectory.resolve("stork");
    writeString(storkBinaryFile, "#!/usr/bin/java -jar\n", UTF_8);
    Files.write(storkBinaryFile, Files.readAllBytes(jarFile), CREATE, APPEND);

    // move stork product to home directory
    var productFile = path("/home/fafkulec/stork");
    if (Files.exists(productFile)) {
      out("overwriting ~/stork");
    }
    Files.move(storkBinaryFile, productFile, REPLACE_EXISTING);

    out("done");
  }

  public static Path path(String name) {
    return Paths.get(name);
  }

  private static Path sourceFileOf(Class<?> type) {
    String sourceFileName = type.getSimpleName() + ".java";
    return path(type.getResource(sourceFileName).getFile());
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

  public static void checkExitCode(int exitValue) {
    if (exitValue > 0) {
      out("exit value: %s", exitValue);
      System.exit(exitValue);
    }
  }
}

class Javac {
  private Path workingDirectory;
  private Path sourcepath;
  private Path destination;
  private Path sourcefile;

  public Javac workingDirectory(Path workingDirectory) {
    this.workingDirectory = workingDirectory;
    return this;
  }

  public Javac workingDirectory(String workingDirectory) {
    return workingDirectory(path(workingDirectory));
  }

  public Javac sourcepath(Path sourcepath) {
    this.sourcepath = sourcepath;
    return this;
  }

  public Javac sourcepath(String sourcepath) {
    return sourcepath(path(sourcepath));
  }

  public Javac destination(Path destination) {
    this.destination = destination;
    return this;
  }

  public Javac destination(String destination) {
    return destination(path(destination));
  }

  public Javac sourcefile(Path sourcefile) {
    this.sourcefile = sourcefile;
    return this;
  }

  public Javac sourcefile(String sourcefile) {
    return sourcefile(path(sourcefile));
  }

  public Process start() throws IOException {
    var command = asList("javac",
        "-sourcepath", sourcepath.toString(),
        "-d", destination.toString(),
        sourcefile.toString());
    return new ProcessBuilder(command)
        .directory(workingDirectory.toFile())
        .inheritIO()
        .start();
  }

  public void dryRun() {
    out("dry run: javac -sourcepath %s -d %s %s",
        sourcepath.toString(),
        destination.toString(),
        sourcefile.toString());
  }
}

class Zip {
  private Path sourceDirectory;
  private Path destinationFile;

  public Zip sourceDirectory(Path sourceDirectory) {
    this.sourceDirectory = sourceDirectory;
    return this;
  }

  public Zip sourceDirectory(String sourceDirectory) {
    return sourceDirectory(path(sourceDirectory));
  }

  public Zip destinationFile(Path destinationFile) {
    this.destinationFile = destinationFile;
    return this;
  }

  public Zip destinationFile(String destinationFile) {
    return destinationFile(path(destinationFile));
  }

  public Process start() throws IOException, InterruptedException {
    var command = shellExpansion(
        "zip",
        "--quiet", "-X", "--recurse-paths",
        destinationFile.toString(), "./*");
    return new ProcessBuilder(command)
        .directory(sourceDirectory.toFile())
        .inheritIO()
        .start();
  }

  private static List<String> shellExpansion(String... command) {
    return asList("sh", "-c", join(" ", command));
  }
}
