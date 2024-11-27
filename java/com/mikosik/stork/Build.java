package com.mikosik.stork;

import static com.mikosik.stork.Project.project;
import static com.mikosik.stork.common.io.Directories.homeDirectory;
import static com.mikosik.stork.common.io.Directories.newTemporaryDirectory;
import static com.mikosik.stork.common.proc.Javac.javac;
import static com.mikosik.stork.common.proc.Zip.zip;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.nio.file.attribute.PosixFilePermission.OWNER_EXECUTE;

import java.nio.file.FileSystems;

import com.mikosik.stork.common.StandardOutput;

/**
 * This is build of stork compiler. It can be run as multi-file source-code
 * program (<a href="https://openjdk.org/jeps/458">JEP-458</a>) (requires java
 * 22) or from Eclipse IDE.
 *
 * TODO use {@link StandardOutput#out}
 */
public class Build {
  public static void main(String... args) throws Exception {
    var fileSystem = FileSystems.getDefault();
    var project = project(fileSystem);
    var homeDirectory = homeDirectory();
    var buildDirectory = newTemporaryDirectory("stork_build_");

    out("building stork binary");
    out("project: %s", project.root);
    out("script: %s", project.sourceFileOf(Build.class));
    out("java source directory: %s", project.javaSourceDirectory);
    out("core library directory: %s", project.coreLibraryDirectory);
    out("temp build directory: %s", buildDirectory);

    var jarDirectory = buildDirectory
        .directory("assembling_jar")
        .create();

    javac()
        .sourcepath(project.javaSourceDirectory)
        .destination(jarDirectory)
        .sourcefile(project.sourceFileOf(Stork.class))
        .execute();

    jarDirectory
        .directory("META-INF")
        .create()
        .file("MANIFEST.MF")
        .create()
        .append("Main-Class: %s\n"
            .formatted(Stork.class.getName())
            .getBytes(US_ASCII));

    zip()
        .sourceDirectory(project.coreLibraryDirectory)
        .destinationFile(jarDirectory.file("core.star"))
        .execute();

    var jarFile = buildDirectory.file("stork.jar");
    zip()
        .sourceDirectory(jarDirectory)
        .destinationFile(jarFile)
        .execute();

    var storkBinaryFile = buildDirectory.file("stork")
        .create()
        .append("#!/usr/bin/java -jar\n".getBytes(US_ASCII))
        .append(jarFile)
        .addPermission(OWNER_EXECUTE)
        .move(homeDirectory);

    out("");
    out("created stork binary: %s".formatted(storkBinaryFile));
  }

  public static void out(String template, Object... items) {
    System.out.println(format(template, items));
  }
}
