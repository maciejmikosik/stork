package com.mikosik.stork;

import static com.mikosik.stork.Project.project;
import static com.mikosik.stork.common.StandardOutput.out;
import static com.mikosik.stork.common.io.Directories.homeDirectory;
import static com.mikosik.stork.common.io.Directories.newTemporaryDirectory;
import static com.mikosik.stork.common.proc.Javac.javac;
import static com.mikosik.stork.common.proc.Zip.zip;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.nio.file.attribute.PosixFilePermission.OWNER_EXECUTE;

/**
 * This is build of stork compiler. It can be run as multi-file source-code
 * program (<a href="https://openjdk.org/jeps/458">JEP-458</a>) (requires java
 * 22) or from Eclipse IDE.
 */
public class Build {
  public static void main(String... args) throws Exception {
    var project = project();
    var homeDirectory = homeDirectory();
    var buildDirectory = newTemporaryDirectory("stork_build_");

    out("building stork binary");
    out("project: %s", project.root);
    out("script: %s", project.sourceFileOf(Build.class));
    out("java source directory: %s", project.javaSourceDirectory);
    out("core directory: %s", project.coreDirectory);
    out("temp build directory: %s", buildDirectory);

    var stageDirectory = buildDirectory
        .directory("stage")
        .create();

    javac()
        .source(project.javaSourceDirectory)
        .destination(stageDirectory)
        .seed(project.sourceFileOf(Stork.class))
        .release(21)
        .execute();

    stageDirectory
        .directory("META-INF")
        .create()
        .file("MANIFEST.MF")
        .create()
        .append("Main-Class: %s\n"
            .formatted(Stork.class.getName())
            .getBytes(US_ASCII));

    zip()
        .source(project.coreDirectory)
        .destination(stageDirectory.file("core.star"))
        .execute();

    var storkJarFile = buildDirectory.file("stork.jar");
    zip()
        .source(stageDirectory)
        .destination(storkJarFile)
        .execute();

    var storkShebangFile = buildDirectory.file("stork")
        .create()
        .append("#!/usr/bin/java -jar\n".getBytes(US_ASCII))
        .append(storkJarFile)
        .addPermission(OWNER_EXECUTE)
        .move(homeDirectory);

    out("");
    out("created stork binary: %s".formatted(storkShebangFile));
  }
}
