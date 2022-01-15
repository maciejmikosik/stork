package com.mikosik.stork.tool.decompile;

import static com.mikosik.stork.common.io.Blob.blob;
import static java.nio.charset.StandardCharsets.US_ASCII;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.common.io.Output;
import com.mikosik.stork.model.Application;
import com.mikosik.stork.model.Combinator;
import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Innate;
import com.mikosik.stork.model.Integer;
import com.mikosik.stork.model.Lambda;
import com.mikosik.stork.model.Model;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.model.Parameter;
import com.mikosik.stork.model.Quote;
import com.mikosik.stork.model.Variable;

public class Decompilation {
  private final boolean local;
  private final Output output;

  public Decompilation(boolean local, Output output) {
    this.local = local;
    this.output = output;
  }

  public void decompile(Model model) {
    if (model instanceof Module) {
      decompile((Module) model);
    } else if (model instanceof Definition) {
      decompile((Definition) model);
    } else if (model instanceof Expression) {
      decompile((Expression) model);
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
    decompile(definition.identifier);
    decompileBody(definition.body);
  }

  private void decompile(Expression expression) {
    if (expression instanceof Identifier) {
      decompile((Identifier) expression);
    } else if (expression instanceof Integer) {
      decompile((Integer) expression);
    } else if (expression instanceof Quote) {
      decompile((Quote) expression);
    } else if (expression instanceof Innate) {
      decompile((Innate) expression);
    } else if (expression instanceof Combinator) {
      decompile((Combinator) expression);
    } else if (expression instanceof Variable) {
      decompile((Variable) expression);
    } else if (expression instanceof Parameter) {
      decompile((Parameter) expression);
    } else if (expression instanceof Lambda) {
      decompile((Lambda) expression);
    } else if (expression instanceof Application) {
      decompile((Application) expression);
    }
  }

  private void decompile(Identifier identifier) {
    decompile(local
        ? identifier.toVariable().name
        : identifier.name);
  }

  private void decompile(Integer integer) {
    decompile(integer.value.toString());
  }

  private void decompile(Quote quote) {
    decompile('\"');
    decompile(quote.string);
    decompile('\"');
  }

  private void decompile(Innate innate) {
    decompile(innate.toString());
  }

  private void decompile(Combinator combinator) {
    decompile("$" + combinator.toString());
  }

  private void decompile(Variable variable) {
    decompile(variable.name);
  }

  private void decompile(Parameter parameter) {
    decompile(parameter.name);
  }

  private void decompile(Lambda lambda) {
    decompile('(');
    decompile(lambda.parameter);
    decompile(')');
    decompileBody(lambda.body);
  }

  private void decompile(Application application) {
    decompile(application.function);
    decompile('(');
    decompile(application.argument);
    decompile(')');

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
