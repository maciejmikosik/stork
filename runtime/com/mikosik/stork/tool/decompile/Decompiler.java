package com.mikosik.stork.tool.decompile;

import static com.mikosik.stork.common.io.Output.output;
import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Computation.computation;
import static com.mikosik.stork.model.Variable.variable;
import static java.nio.charset.StandardCharsets.US_ASCII;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.model.Application;
import com.mikosik.stork.model.Combinator;
import com.mikosik.stork.model.Computation;
import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Innate;
import com.mikosik.stork.model.Integer;
import com.mikosik.stork.model.Lambda;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.model.Parameter;
import com.mikosik.stork.model.Quote;
import com.mikosik.stork.model.Stack;
import com.mikosik.stork.model.Variable;
import com.mikosik.stork.tool.common.Traverser;

public class Decompiler {
  private boolean local = false;

  private Decompiler(boolean local) {
    this.local = local;
  }

  public static Decompiler decompiler() {
    return new Decompiler(false);
  }

  public Decompiler local() {
    return new Decompiler(true);
  }

  public String decompile(Object code) {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    Traverser printer = printer(output(buffer).asPrintStream(US_ASCII));
    if (code instanceof Expression) {
      printer.traverse((Expression) code);
    } else if (code instanceof Definition) {
      printer.traverse((Definition) code);
    } else if (code instanceof Module) {
      printer.traverse((Module) code);
    } else if (code instanceof Computation) {
      printer.traverse(abort(mark((Computation) code)));
    }
    return new String(buffer.toByteArray(), US_ASCII);
  }

  private Traverser printer(PrintStream output) {
    return new Traverser() {
      public Module traverse(Module module) {
        Chain<Definition> definitions = module.definitions;
        if (!definitions.isEmpty()) {
          traverse(definitions.head());
          for (Definition definition : definitions.tail()) {
            output.print(' ');
            traverse(definition);
          }
        }
        return null;
      }

      public Definition traverse(Definition definition) {
        traverse(definition.variable);
        traverseBody(definition.expression);
        return null;
      }

      protected Expression traverse(Integer integer) {
        output.print(integer.value.toString());
        return null;
      }

      protected Expression traverse(Quote quote) {
        output.print('\"');
        output.print(quote.string);
        output.print('\"');
        return null;
      }

      protected Expression traverse(Innate innate) {
        output.print(innate.toString());
        return null;
      }

      protected Expression traverse(Combinator combinator) {
        output.print("$" + combinator.toString());
        return null;
      }

      protected Variable traverse(Variable variable) {
        output.print(local
            ? variable.toLocal().name
            : variable.name);
        return null;
      }

      protected Variable traverseDefinitionName(Variable variable) {
        return traverse(variable);
      }

      protected Expression traverse(Parameter parameter) {
        output.print(parameter.name);
        return null;
      }

      protected Expression traverse(Lambda lambda) {
        output.print('(');
        traverse(lambda.parameter);
        output.print(')');
        traverseBody(lambda.body);
        return null;
      }

      protected Expression traverse(Application application) {
        traverse(application.function);
        output.print('(');
        traverse(application.argument);
        output.print(')');
        return null;
      }

      protected Expression traverseBody(Expression body) {
        boolean isLambda = body instanceof Lambda;
        output.print(isLambda ? "" : "{");
        traverse(body);
        output.print(isLambda ? "" : "}");
        return null;
      }
    };
  }

  private static Computation mark(Computation computation) {
    return computation(
        application(variable("@"), computation.expression),
        computation.stack);
  }

  private static Expression abort(Computation computation) {
    Expression expression = computation.expression;
    Stack stack = computation.stack;
    while (true) {
      if (stack.hasArgument()) {
        expression = application(expression, stack.argument());
      } else if (stack.hasFunction()) {
        expression = application(stack.function(), expression);
      } else {
        break;
      }
      stack = stack.pop();
    }
    return expression;
  }
}
