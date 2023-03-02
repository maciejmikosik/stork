package com.mikosik.stork.debug;

import static com.mikosik.stork.compile.Decompiler.decompiler;
import static com.mikosik.stork.debug.HandlingChainDecorator.handlingChain;
import static org.logbuddy.decorator.ComposedDecorator.compose;
import static org.logbuddy.decorator.InjectingDecorator.injecting;
import static org.logbuddy.decorator.InvocationDecorator.invocationDecorator;
import static org.logbuddy.decorator.TraversingDecorator.traversing;
import static org.logbuddy.logger.InvocationDepthLogger.invocationDepth;
import static org.logbuddy.logger.wire.FileLogger.fileLogger;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.logbuddy.Decorator;
import org.logbuddy.Logger;
import org.logbuddy.Renderer;

import com.mikosik.stork.compute.Computer;

public class Debug {
  public static Decorator configuredDecorator(Path logFile) {
    Renderer<String> renderer = new StorkTextRenderer(decompiler().local());
    Logger logger = invocationDepth(fileLogger(logFile, renderer));
    Decorator decorator = handlingChain(compose(
        invocationDecorator(logger),
        injecting(logger)));
    return traversing(decorator)
        .filter(field -> !Map.class.isAssignableFrom(field.getDeclaringClass()));
  }

  private static AtomicInteger countDebuggingComputer = new AtomicInteger();

  public static Computer debugging(Computer computer) {
    String fileName = "/tmp/stork_debugging_computer_" + countDebuggingComputer.getAndIncrement();
    return configuredDecorator(Paths.get(fileName)).decorate(computer);
  }
}
