package com.mikosik.stork.testing;

import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.data.model.comp.Computation.computation;
import static com.mikosik.stork.tool.common.Computations.abort;
import static com.mikosik.stork.tool.common.Invocation.asInvocation;
import static com.mikosik.stork.tool.common.Translate.asJavaString;
import static com.mikosik.stork.tool.compile.Decompiler.decompiler;
import static com.mikosik.stork.tool.compute.WirableComputer.computer;
import static com.mikosik.stork.tool.link.Linker.coreModule;
import static com.mikosik.stork.tool.link.Repository.repository;
import static java.lang.String.format;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;
import static org.quackery.help.Helpers.traverseNames;

import java.util.Iterator;

import org.quackery.Suite;
import org.quackery.Test;
import org.quackery.report.AssertException;

import com.mikosik.stork.data.model.Definition;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Module;
import com.mikosik.stork.tool.common.Invocation;
import com.mikosik.stork.tool.compile.Decompiler;
import com.mikosik.stork.tool.compute.Computer;
import com.mikosik.stork.tool.link.Repository;

public class ModuleTest {
  private static final Computer computer = computer()
      .moduling(coreModule())
      .opcoding()
      .substituting()
      .stacking()
      .interruptible()
      .humane()
      .looping();
  private static final Decompiler decompiler = decompiler();
  private static final Repository repository = repository();

  public static Test testModule(String fileName, String functionName) {
    return buildTest(repository.module(fileName), functionName);
  }

  private static Test buildTest(Module module, String functionName) {
    Definition testDefinition = module.definitions.stream()
        .filter(definition -> definition.variable.name.equals(functionName))
        .findFirst()
        .get();
    return buildTest(testDefinition.expression);
  }

  private static Test buildTest(Expression expression) {
    Invocation invocation = asInvocation(expression);
    if (invocation.function.name.equals("case")) {
      return buildCase(invocation);
    } else if (invocation.function.name.equals("suite")) {
      return buildSuite(invocation);
    } else {
      throw new RuntimeException(invocation.function.name);
    }
  }

  private static Test buildSuite(Invocation invocation) {
    Iterator<Expression> iterator = invocation.arguments.iterator();
    Suite suite = suite(asJavaString(iterator.next()));
    while (iterator.hasNext()) {
      suite = suite.add(buildTest(iterator.next()));
    }
    return suite;
  }

  private static Test buildCase(Invocation invocation) {
    Iterator<Expression> iterator = invocation.arguments.iterator();
    Expression first = iterator.next();
    Expression second = iterator.next();
    if (!iterator.hasNext()) {
      return buildCase(first, second);
    }
    Expression third = iterator.next();
    check(!iterator.hasNext());
    return rename(asJavaString(first), buildCase(second, third));
  }

  private static Test rename(String name, Test test) {
    return traverseNames(test, oldName -> name);
  }

  private static Test buildCase(Expression question, Expression answer) {
    String questionCode = decompiler.decompile(question);
    String answerCode = decompiler.decompile(answer);
    return newCase(format("%s = %s", questionCode, answerCode), () -> {
      String questionComputed = compute(question);
      String answerComputed = compute(answer);
      if (!questionComputed.equals(answerComputed)) {
        throw new AssertException(format(""
            + "expected that expression\n"
            + "  %s\n"
            + "is equal to\n"
            + "  %s\n"
            + "which computes to\n"
            + "  %s\n"
            + "but expression computed to\n"
            + "  %s\n",
            questionCode,
            answerCode,
            answerComputed,
            questionComputed));
      }
    });
  }

  private static String compute(Expression expression) {
    return decompiler.decompile(abort(computer.compute(computation(expression))));
  }
}
