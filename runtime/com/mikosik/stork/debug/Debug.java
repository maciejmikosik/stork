package com.mikosik.stork.debug;

import static com.mikosik.stork.common.Chain.chain;
import static com.mikosik.stork.common.io.Node.node;
import static com.mikosik.stork.common.io.Output.output;
import static com.mikosik.stork.compile.Bind.join;
import static com.mikosik.stork.compile.CheckCollisions.checkCollisions;
import static com.mikosik.stork.compile.CheckUndefined.checkUndefined;
import static com.mikosik.stork.compile.CombinatoryModule.combinatoryModule;
import static com.mikosik.stork.compile.Decompiler.decompiler;
import static com.mikosik.stork.compile.MathModule.mathModule;
import static com.mikosik.stork.compile.Stars.moduleFromDirectory;
import static com.mikosik.stork.compile.Unlambda.unlambda;
import static com.mikosik.stork.compile.Unquote.unquote;
import static com.mikosik.stork.debug.HandlingChainDecorator.handlingChain;
import static com.mikosik.stork.model.change.Changes.inModule;
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

import com.mikosik.stork.common.io.Output;
import com.mikosik.stork.compute.Computer;
import com.mikosik.stork.model.Module;

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

  private static AtomicInteger countPrintModule = new AtomicInteger();

  public static void printModule() {
    Module module = join(chain(
        mathModule(),
        combinatoryModule(),
        moduleFromDirectory(node("core_star"))));

    Path fileName = Paths.get("/tmp/stork_core_star_" + countPrintModule.getAndIncrement());
    try (Output output = output(fileName);) {
      decompiler().to(output).decompile(module);
    }

    checkCollisions(module);
    checkUndefined(module);

    module = inModule(unlambda)
        .andThen(inModule(unquote))
        .apply(module);
  }
}
