package com.mikosik.stork.tool.compile;

import static com.mikosik.stork.common.InputOutput.printStream;
import static com.mikosik.stork.data.model.Application.application;
import static com.mikosik.stork.data.model.Variable.variable;
import static com.mikosik.stork.data.model.comp.Computation.computation;
import static com.mikosik.stork.tool.common.Computations.abort;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;

import com.mikosik.stork.data.model.Application;
import com.mikosik.stork.data.model.Definition;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Integer;
import com.mikosik.stork.data.model.Lambda;
import com.mikosik.stork.data.model.Module;
import com.mikosik.stork.data.model.Parameter;
import com.mikosik.stork.data.model.Variable;
import com.mikosik.stork.data.model.comp.Computation;

public class Decompiler {
  protected Decompiler() {}

  public static Decompiler decompiler() {
    return new Decompiler();
  }

  public String decompile(Object code) {
    Charset charset = UTF_8;
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    PrintStream output = printStream(buffer, charset);
    print(code, output);
    return new String(buffer.toByteArray(), charset);
  }

  protected void print(Object code, PrintStream output) {
    if (code instanceof Expression) {
      print((Expression) code, output);
    } else if (code instanceof Definition) {
      print((Definition) code, output);
    } else if (code instanceof Module) {
      print((Module) code, output);
    } else if (code instanceof Computation) {
      print((Computation) code, output);
    } else {
      throw new RuntimeException();
    }
  }

  protected void print(Expression expression, PrintStream output) {
    if (expression instanceof Integer) {
      print((Integer) expression, output);
    } else if (expression instanceof Variable) {
      print((Variable) expression, output);
    } else if (expression instanceof Parameter) {
      print((Parameter) expression, output);
    } else if (expression instanceof Application) {
      print((Application) expression, output);
    } else if (expression instanceof Lambda) {
      print((Lambda) expression, output);
    } else {
      throw new RuntimeException();
    }
  }

  protected void print(Integer integer, PrintStream output) {
    output.print(integer.value.toString());
  }

  protected void print(Variable variable, PrintStream output) {
    output.print(variable.name);
  }

  protected void print(Parameter parameter, PrintStream output) {
    output.print(parameter.name);
  }

  protected void print(Application application, PrintStream output) {
    print(application.function, output);
    output.print('(');
    print(application.argument, output);
    output.print(')');
  }

  protected void print(Lambda lambda, PrintStream output) {
    boolean isBodyALambda = lambda.body instanceof Lambda;
    output.write('(');
    print(lambda.parameter, output);
    output.write(')');
    if (!isBodyALambda) {
      output.write('{');
    }
    print(lambda.body, output);
    if (!isBodyALambda) {
      output.write('}');
    }
  }

  protected void print(Definition definition, PrintStream output) {
    if (definition.expression instanceof Lambda) {
      print(definition.variable, output);
      print(definition.expression, output);
    } else {
      print(definition.variable, output);
      output.print('{');
      print(definition.expression, output);
      output.print('}');
    }
  }

  protected void print(Module module, PrintStream output) {
    for (Definition definition : module.definitions) {
      output.print('\n');
      print(definition, output);
      output.print('\n');
    }
  }

  protected void print(Computation computation, PrintStream output) {
    print(abort(mark(computation)), output);
  }

  private static Computation mark(Computation computation) {
    return computation(
        application(variable("@"), computation.expression),
        computation.stack);
  }
}
