package com.mikosik.stork.common.io;

import static com.mikosik.stork.common.io.InputOutput.unchecked;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.newInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Node {
  private final Path path;

  private Node(Path path) {
    this.path = path;
  }

  public static Node node(Path path) {
    return path.isAbsolute()
        ? new Node(path)
        : new Node(path.toAbsolutePath());
  }

  public static Node node(String path) {
    return node(Paths.get(path));
  }

  public Path relativeToNested(Node node) {
    return path.relativize(node.path);
  }

  public String name() {
    return path.getFileName().toString();
  }

  public boolean isRegularFile() {
    return Files.isRegularFile(path);
  }

  public boolean isDirectory() {
    return Files.isDirectory(path);
  }

  public Input input() {
    try {
      return Input.input(newInputStream(path));
    } catch (IOException e) {
      throw unchecked(e);
    }
  }

  public Input tryInput() {
    return exists(path)
        ? input()
        : Input.input(new ByteArrayInputStream(new byte[0]));
  }

  public Node parent() {
    return node(path.getParent());
  }

  public Node child(String name) {
    return node(path.resolve(name));
  }

  public Stream<Node> children() {
    try {
      return Files.list(path).map(Node::node);
    } catch (IOException e) {
      throw unchecked(e);
    }
  }

  public Stream<Node> nested() {
    try {
      return Files.walk(path).map(Node::node);
    } catch (IOException e) {
      throw unchecked(e);
    }
  }
}
