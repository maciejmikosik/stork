package com.mikosik.stork.test;

import static com.mikosik.stork.common.Chain.chain;
import static com.mikosik.stork.common.io.Ascii.ascii;
import static com.mikosik.stork.common.io.Buffer.newBuffer;
import static com.mikosik.stork.common.io.Input.input;
import static com.mikosik.stork.common.io.InputOutput.createTempDirectory;
import static com.mikosik.stork.common.io.Output.output;
import static com.mikosik.stork.compile.Bind.join;
import static com.mikosik.stork.compile.Stars.build;
import static com.mikosik.stork.compile.Stars.moduleFromDirectory;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Link.link;
import static com.mikosik.stork.model.Linkage.linkage;
import static com.mikosik.stork.program.Program.program;
import static com.mikosik.stork.test.Reuse.LANG_AND_PROGRAM_MODULE;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.quackery.Case.newCase;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

import org.quackery.Body;
import org.quackery.Test;
import org.quackery.report.AssertException;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.common.io.Output;
import com.mikosik.stork.model.Link;
import com.mikosik.stork.model.Linkage;
import com.mikosik.stork.model.Module;

public class StreamTest implements Test {
  private final String name;
  private Linkage linkage = linkage();
  private Chain<Test> tests = chain();

  private StreamTest(String name) {
    this.name = name;
  }

  public static StreamTest streamTest(String name) {
    return new StreamTest(name);
  }

  public StreamTest importing(String global) {
    linkage = linkage.add(link(identifier(global)));
    return this;
  }

  public StreamTest test(String actual, String expected) {
    var escapedActual = actual.replace('\'', '\"');
    tests = tests.add(newCase(
        format("%s = %s", escapedActual, expected),
        () -> run(escapedActual, expected)));
    return this;
  }

  private final void run(String actual, String expected) {
    Path directory = createTempDirectory("stork_test_program_");

    Output linkageOutput = output(directory.resolve("import"));
    for (Link link : linkage.links) {
      linkageOutput.write(bytes(link.identifier.name()));
      linkageOutput.write(bytes("\n"));
    }
    linkageOutput.close();

    Output storkOutput = output(directory.resolve("stork"));
    storkOutput.write(bytes("""
        main(stdin) {
          %s
        }
        """.formatted(actual)));
    storkOutput.close();

    Module module = join(chain(
        build(moduleFromDirectory(directory)),
        LANG_AND_PROGRAM_MODULE));
    var program = program(identifier("main"), module);
    var buffer = newBuffer();
    program.run(input(new byte[0]), buffer.asOutput());
    var actualStdout = buffer.bytes();
    var expectedStdout = bytes(expected);
    if (!Arrays.equals(actualStdout, expectedStdout)) {
      throw new AssertException(format(""
          + "expected output\n"
          + "  %s\n"
          + "but was\n"
          + "  %s\n",
          ascii(expectedStdout),
          ascii(actualStdout)));
    }
  }

  public <R> R visit(BiFunction<String, Body, R> caseHandler,
      BiFunction<String, List<Test>, R> suiteHandler) {
    return suiteHandler.apply(name, tests.reverse().toLinkedList());
  }

  private byte[] bytes(String string) {
    return string.getBytes(UTF_8);
  }
}
