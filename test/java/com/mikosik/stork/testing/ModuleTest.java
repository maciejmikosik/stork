package com.mikosik.stork.testing;

import static com.mikosik.stork.common.Chain.chainOf;
import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.common.Throwables.fail;
import static com.mikosik.stork.core.CoreModule.coreModule;
import static com.mikosik.stork.core.Repository.repository;
import static com.mikosik.stork.tool.common.Invocation.asInvocation;
import static com.mikosik.stork.tool.common.Scope.GLOBAL;
import static com.mikosik.stork.tool.common.Scope.LOCAL;
import static com.mikosik.stork.tool.common.Translate.asJavaString;
import static com.mikosik.stork.tool.compile.Decompiler.decompiler;
import static com.mikosik.stork.tool.compute.WirableComputer.computer;
import static com.mikosik.stork.tool.link.WirableLinker.linker;
import static java.lang.String.format;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;
import static org.quackery.help.Helpers.traverseNames;

import java.util.Iterator;

import org.quackery.Suite;
import org.quackery.Test;
import org.quackery.report.AssertException;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.core.Repository;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Module;
import com.mikosik.stork.tool.common.Invocation;
import com.mikosik.stork.tool.compile.Decompiler;
import com.mikosik.stork.tool.compute.CompleteComputer;
import com.mikosik.stork.tool.link.Linker;

public class ModuleTest {
  private static final CompleteComputer computer = computer()
      .moduling(coreModule())
      .opcoding()
      .substituting()
      .stacking()
      .interruptible()
      .humane()
      .looping()
      .complete();
  private static final Decompiler decompiler = decompiler(GLOBAL);
  private static final Decompiler localDecompiler = decompiler(LOCAL);
  private static final Repository repository = repository();

  public static Test testModule(String fileName) {
    Linker linker = linker()
        .building()
        .unique();
    Module module = linker.link(chainOf(repository.module(fileName)));
    return suite(fileName)
        .addAll(module.definitions
            .map(definition -> definition.expression)
            .map(ModuleTest::buildTest));
  }

  private static Test buildTest(Expression expression) {
    Invocation invocation = asInvocation(expression);
    if (invocation.function.name.startsWith("case")) {
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
    return invocation.function.name.equals("case")
        ? buildCase(invocation.arguments)
        : invocation.function.name.equals("caseNamed")
            ? rename(
                asJavaString(invocation.arguments.head()),
                buildCase(invocation.arguments.tail()))
            : fail("");
  }

  private static Test rename(String name, Test test) {
    return traverseNames(test, oldName -> name);
  }

  private static Test buildCase(Chain<Expression> arguments) {
    check(arguments.tail().tail().isEmpty());
    Expression question = arguments.head();
    Expression answer = arguments.tail().head();

    String name = format("%s = %s",
        localDecompiler.decompile(question),
        localDecompiler.decompile(answer));
    return newCase(name, () -> {
      String questionCode = decompiler.decompile(question);
      String answerCode = decompiler.decompile(answer);
      String questionComputed = decompiler.decompile(computer.compute(question));
      String answerComputed = decompiler.decompile(computer.compute(answer));
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
}
