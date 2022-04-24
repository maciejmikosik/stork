package com.mikosik.stork.debug;

import static com.mikosik.stork.debug.HandlingChainDecorator.handlingChain;
import static com.mikosik.stork.tool.decompile.Decompiler.decompiler;
import static org.logbuddy.decorator.InvocationDecorator.invocationDecorator;
import static org.logbuddy.decorator.TraversingDecorator.traversing;
import static org.logbuddy.logger.InvocationDepthLogger.invocationDepth;
import static org.logbuddy.logger.wire.FileLogger.fileLogger;

import java.nio.file.Path;
import java.util.Map;

import org.logbuddy.Decorator;
import org.logbuddy.Logger;
import org.logbuddy.Renderer;

public class Debug {
  public static Decorator configuredDecorator(Path logFile) {
    Renderer<String> renderer = new StorkTextRenderer(decompiler().local());
    Logger logger = fileLogger(logFile, renderer);
    Decorator decorator = handlingChain(invocationDecorator(invocationDepth(logger)));
    return traversing(decorator)
        .filter(field -> !Map.class.isAssignableFrom(field.getDeclaringClass()));
  }
}
