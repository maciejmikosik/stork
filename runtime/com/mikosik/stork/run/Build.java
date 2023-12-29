package com.mikosik.stork.run;

import static java.nio.file.Files.createTempDirectory;
import static java.util.Arrays.asList;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Locale;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

public class Build {

  public static void main(String[] args) throws IOException {
    Path projectRoot = Paths.get(System.getProperty("user.dir"));
    System.out.println("project=" + projectRoot);

    Path javaBinaries = createTempDirectory("stork_build_java_bin_");
    System.out.println("compiling to " + javaBinaries);

    var compiler = ToolProvider.getSystemJavaCompiler();
    var diagnostics = new DiagnosticCollector<JavaFileObject>();
    Locale locale = null; // default locale
    Charset charset = null; // platform default
    var fileManager = compiler.getStandardFileManager(diagnostics, locale, charset);

    var storkFile = projectRoot.resolve("runtime/com/mikosik/stork/run/Stork.java");
    var filesToCompile = new File[] { storkFile.toFile() };

    Iterable<? extends JavaFileObject> fileObjectsToCompile = fileManager
        .getJavaFileObjectsFromFiles(Arrays.asList(filesToCompile));

    fileManager.setLocation(StandardLocation.CLASS_OUTPUT, asList(javaBinaries.toFile()));

    Writer compilerOutput = null; // null means System.err
    Iterable<String> compilerOptions = null; // no options
    Iterable<String> classNamesForAnnotationProcessing = null;

    Boolean wasCompiled = compiler.getTask(
        compilerOutput,
        fileManager,
        diagnostics,
        compilerOptions,
        classNamesForAnnotationProcessing,
        fileObjectsToCompile)
        .call();

    System.out.println("wasCompiled=" + wasCompiled);

    fileManager.close();
  }
}
