package com.mikosik.stork;

import static com.mikosik.stork.TestEverything.testEverything;
import static com.mikosik.stork.common.Chains.chainOf;
import static com.mikosik.stork.lib.Modules.module;
import static com.mikosik.stork.main.Program.program;
import static com.mikosik.stork.testing.MoreReports.filter;
import static com.mikosik.stork.testing.MoreReports.formatExceptions;
import static java.lang.String.format;
import static org.quackery.report.Reports.count;
import static org.quackery.run.Runners.run;

import org.quackery.Test;
import org.quackery.report.Reports;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Module;
import com.mikosik.stork.main.Program;

public class RunEverything {
  public static void main(String[] args) {
    Test test = testEverything();
    Test report = run(test);
    print(report);

    Chain<Module> modules = chainOf(
        module("function.stork"),
        module("stream.stork"),
        module("integer.stork"),
        module("boolean.stork"),
        module("demo.stork"));
    Program program = program("main", modules);
    program.run();
  }

  private static void print(Test report) {
    int total = count(Throwable.class, report);
    if (total == 0) {
      System.out.print(format(""
          + "%s\n"
          + "no failures\n"
          + "\n",
          Reports.format(report)));
    } else {
      Test failed = filter(Throwable.class, report).get();
      System.err.print(format(""
          + "%s\n"
          + "%s\n"
          + "\n",
          Reports.format(failed),
          formatExceptions(failed)));
    }
  }
}
