package com.mikosik.stork.debug;

import org.logbuddy.Decorator;

/**
 * TODO implement in logbuddy
 */
public class ClassLoadingDecorator implements Decorator {
  private final ClassLoader classLoader;
  private final Decorator decorator;

  private ClassLoadingDecorator(ClassLoader classLoader, Decorator decorator) {
    this.classLoader = classLoader;
    this.decorator = decorator;
  }

  public static Decorator classLoading(ClassLoader classLoader, Decorator decorator) {
    return new ClassLoadingDecorator(classLoader, decorator);
  }

  public <T> T decorate(T decorable) {
    var thread = Thread.currentThread();
    var original = thread.getContextClassLoader();
    thread.setContextClassLoader(classLoader);
    var decorated = decorator.decorate(decorable);
    thread.setContextClassLoader(original);
    return decorated;
  }
}
