package com.mikosik.stork.tool.link;

import static com.mikosik.stork.common.Strings.reverse;
import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Definition.definition;
import static com.mikosik.stork.model.Integer.integer;
import static com.mikosik.stork.model.Lambda.lambda;
import static com.mikosik.stork.model.Module.module;
import static com.mikosik.stork.model.Variable.variable;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.model.Application;
import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Lambda;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.model.Quote;
import com.mikosik.stork.model.Variable;

public class QuotingLinker implements Linker {
  private final Linker linker;

  private QuotingLinker(Linker linker) {
    this.linker = linker;
  }

  public static Linker quoting(Linker linker) {
    return new QuotingLinker(linker);
  }

  public Module link(Chain<Module> modules) {
    return replace(linker.link(modules));
  }

  private static Module replace(Module module) {
    return module(module.definitions.map(QuotingLinker::replace));
  }

  private static Definition replace(Definition definition) {
    return definition(
        definition.variable,
        replace(definition.expression));
  }

  private static Expression replace(Expression expression) {
    if (expression instanceof Quote) {
      Quote quote = (Quote) expression;
      return asStorkStream(quote.string);
    } else if (expression instanceof Application) {
      Application application = (Application) expression;
      return application(
          replace(application.function),
          replace(application.argument));
    } else if (expression instanceof Lambda) {
      Lambda lambda = (Lambda) expression;
      return lambda(lambda.parameter, replace(lambda.body));
    } else {
      return expression;
    }
  }

  private static Expression asStorkStream(String ascii) {
    Expression stream = NONE;
    for (char character : reverse(ascii).toCharArray()) {
      stream = application(SOME, integer(character), stream);
    }
    return stream;
  }

  private static final Variable SOME = variable("stork.stream.some");
  private static final Variable NONE = variable("stork.stream.none");
}
