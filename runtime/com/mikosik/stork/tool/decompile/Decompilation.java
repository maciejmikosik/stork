package com.mikosik.stork.tool.decompile;

import static com.mikosik.stork.common.io.Blob.blob;
import static java.nio.charset.StandardCharsets.US_ASCII;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.common.io.Output;
import com.mikosik.stork.model.Application;
import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.EagerInstruction;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Instruction;
import com.mikosik.stork.model.Integer;
import com.mikosik.stork.model.Lambda;
import com.mikosik.stork.model.Model;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.model.NamedInstruction;
import com.mikosik.stork.model.Parameter;
import com.mikosik.stork.model.Quote;
import com.mikosik.stork.program.Stdin;

public class Decompilation {
  private final boolean local;
  private final Output output;

  public Decompilation(boolean local, Output output) {
    this.local = local;
    this.output = output;
  }

  public void decompile(Model model) {
    if (model instanceof Module module) {
      decompile(module);
    } else if (model instanceof Definition definition) {
      decompile(definition);
    } else if (model instanceof Expression expression) {
      decompile(expression);
    }
  }

  private void decompile(Module module) {
    Chain<Definition> definitions = module.definitions;
    if (!definitions.isEmpty()) {
      decompile(definitions.head());
      for (Definition definition : definitions.tail()) {
        decompile(' ');
        decompile(definition);
      }
    }
  }

  private void decompile(Definition definition) {
    Identifier identifier = definition.identifier;
    decompile(local
        ? identifier.toLocal().name
        : identifier.name);
    decompileBody(definition.body);
  }

  private void decompile(Expression expression) {
    if (expression instanceof Identifier identifier) {
      decompile(local
          ? identifier.toLocal().name
          : identifier.name);
    } else if (expression instanceof Integer integer) {
      decompile(integer.value.toString());
    } else if (expression instanceof Quote quote) {
      decompile('\"');
      decompile(quote.string);
      decompile('\"');
    } else if (expression instanceof EagerInstruction eager) {
      decompile(eager.visited
          ? "eagerVisited("
          : "eager(");
      decompile(eager.instruction);
      decompile(")");
    } else if (expression instanceof NamedInstruction instruction) {
      decompile("<");
      decompile(instruction.name);
      decompile(">");
    } else if (expression instanceof Instruction instruction) {
      decompile("<>");
    } else if (expression instanceof Parameter parameter) {
      decompile(parameter.name);
    } else if (expression instanceof Lambda lambda) {
      decompile('(');
      decompile(lambda.parameter.name);
      decompile(')');
      decompileBody(lambda.body);
    } else if (expression instanceof Application application) {
      decompile(application.function);
      decompile('(');
      decompile(application.argument);
      decompile(')');
    } else if (expression instanceof Stdin stdin) {
      decompile("stdin");
      decompile('(');
      decompile("" + stdin.index);
      decompile(')');
    }
  }

  private void decompileBody(Expression body) {
    boolean isLambda = body instanceof Lambda;
    if (!isLambda) {
      decompile('{');
    }
    decompile(body);
    if (!isLambda) {
      decompile('}');
    }
  }

  private void decompile(String string) {
    output.write(blob(string.getBytes(US_ASCII)));
  }

  private void decompile(char character) {
    output.write((byte) character);
  }
}
