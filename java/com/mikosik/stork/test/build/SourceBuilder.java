package com.mikosik.stork.test.build;

import static com.mikosik.stork.common.io.AsciiStrings.tryToAsciiBytesOrFail;
import static com.mikosik.stork.common.io.BytesBuilder.bytesBuilder;

public class SourceBuilder {
  public static byte[] mainWithBody(byte... body) {
    return bytesBuilder()
        .appendAscii("main(stdin) {\n")
        .appendAscii("  ").append(body).appendAscii("\n")
        .appendAscii("}\n")
        .build();
  }

  public static byte[] mainWithBody(String body) {
    return mainWithBody(tryToAsciiBytesOrFail(body));
  }

  public static byte[] stringLiteral(byte... content) {
    return bytesBuilder()
        .appendAscii("\"")
        .append(content)
        .appendAscii("\"")
        .build();
  }

  public static byte[] stringLiteral(String content) {
    return stringLiteral(tryToAsciiBytesOrFail(content));
  }
}
