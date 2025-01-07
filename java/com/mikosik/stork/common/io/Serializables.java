package com.mikosik.stork.common.io;

import static com.mikosik.stork.common.io.Ascii.bytes;
import static com.mikosik.stork.common.io.Buffer.newBuffer;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.util.Arrays.asList;

public class Serializables {
  public static Serializable serializable(byte[] bytes) {
    return output -> output.write(bytes);
  }

  public static Serializable serializable(byte oneByte) {
    return output -> output.write(oneByte);
  }

  public static Serializable serializable(String string) {
    return serializable(bytes(string));
  }

  public static Serializable serializable(char character) {
    return serializable((byte) character);
  }

  public static Serializable serializable(Object object) {
    return output -> output.write(object.toString().getBytes(US_ASCII));
  }

  public static Serializable join(Serializable... serializables) {
    return join(asList(serializables));
  }

  public static Serializable join(Iterable<Serializable> serializables) {
    return output -> serializables.forEach(serializable -> serializable.serialize(output));
  }

  public static String ascii(Serializable serializable) {
    Buffer buffer = newBuffer();
    serializable.serialize(buffer.asOutput());
    return Ascii.ascii(buffer.bytes());
  }
}
