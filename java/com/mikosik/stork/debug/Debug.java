package com.mikosik.stork.debug;

import static com.mikosik.stork.debug.ClassLoadingDecorator.classLoading;
import static org.logbuddy.decorator.InvocationDecorator.invocationDecorator;
import static org.logbuddy.decorator.Rich.traversing;
import static org.logbuddy.logger.InvocationDepthLogger.invocationDepth;
import static org.logbuddy.logger.wire.FileLogger.fileLogger;

import java.nio.file.Path;

import org.logbuddy.Decorator;
import org.logbuddy.Logger;
import org.logbuddy.Renderer;

import com.mikosik.stork.compute.Computer;
import com.mikosik.stork.program.Program;

// TODO Computer that detects infinite loops. Should cache Computation and Stack.
// TODO profiler Computer that shows hotspots
public class Debug {
  public static Decorator configuredDecorator(Path logFile) {
    Renderer<String> renderer = new StorkTextRenderer();
    Logger logger = invocationDepth(fileLogger(logFile, renderer));
    var classLoader = new ClassLoader(Debug.class.getClassLoader()) {};
    return classLoading(classLoader, traversing(filteringTypes(invocationDecorator(logger))));
  }

  private static Decorator filteringTypes(Decorator decorator) {
    return new Decorator() {
      public <T> T decorate(T decorable) {
        return shouldLog(decorable)
            ? decorator.decorate(decorable)
            : decorable;
      }
    };
  }

  private static boolean shouldLog(Object decorable) {
    return decorable instanceof Program
        || decorable instanceof Computer;
  }
}
