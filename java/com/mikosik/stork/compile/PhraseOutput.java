package com.mikosik.stork.compile;

import static com.mikosik.stork.common.err.Throwables.runtimeException;
import static com.mikosik.stork.common.io.AsciiStrings.tryToAsciiBytesOrFail;

import com.mikosik.stork.common.io.Output;
import com.mikosik.stork.model.exp.Application;
import com.mikosik.stork.model.exp.Definition;
import com.mikosik.stork.model.exp.Expression;
import com.mikosik.stork.model.exp.Integer;
import com.mikosik.stork.model.exp.Lambda;
import com.mikosik.stork.model.exp.Operator;
import com.mikosik.stork.model.exp.Parameter;
import com.mikosik.stork.model.exp.Quote;
import com.mikosik.stork.model.exp.Reference;
import com.mikosik.stork.model.exp.Variable;

public class PhraseOutput {
  private final Output output;

  private PhraseOutput(Output output) {
    this.output = output;
  }

  public static PhraseOutput phrase(Output output) {
    return new PhraseOutput(output);
  }

  public void write(Definition definition) {
    write(definition.reference);
    writeBody(definition.body);
  }

  public void write(Expression expression) {
    switch (expression) {
      case Variable variable -> {
        write(variable.name);
      }
      case Reference reference -> {
        for (var component : reference.namespace.components) {
          write(component);
          write('/');
        }
        write(reference.variable.name);
      }
      case Integer integer -> {
        write(integer.value.toString());
      }
      case Quote quote -> {
        write('"');
        write(quote.string);
        write('"');
      }
      case Operator operator -> {
        write('$');
        write(operator.toString());
      }
      case Parameter parameter -> {
        write(parameter.name);
      }
      case Lambda lambda -> {
        write('(');
        write(lambda.parameter.name);
        write(')');
        writeBody(lambda.body);
      }
      case Application application -> {
        write(application.function);
        write('(');
        write(application.argument);
        write(')');
      }
      default -> throw runtimeException("unknown expression", expression);
    }
  }

  private void writeBody(Expression body) {
    switch (body) {
      case Lambda lambda -> write(lambda);
      default -> {
        write('{');
        write(body);
        write('}');
      }
    }
  }

  private void write(char character) {
    output.write((byte) character);
  }

  private void write(String string) {
    output.write(tryToAsciiBytesOrFail(string));
  }
}
