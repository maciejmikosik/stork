package com.mikosik.stork.tool.common;

import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Definition.definition;
import static com.mikosik.stork.model.Lambda.lambda;
import static com.mikosik.stork.model.Module.module;

import java.util.function.Function;

import com.mikosik.stork.model.Application;
import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Lambda;
import com.mikosik.stork.model.Model;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.model.Parameter;
import com.mikosik.stork.model.Quote;
import com.mikosik.stork.model.Variable;

public class Morph {
  private final Class<? extends Model> type;
  private final Function<? extends Model, Model> function;

  private Morph(Class<? extends Model> type, Function<? extends Model, Model> function) {
    this.type = type;
    this.function = function;
  }

  public static Morph morphDefinitions(Function<Definition, Model> function) {
    return new Morph(Definition.class, function);
  }

  public static Morph morphIdentifiers(Function<Identifier, Model> function) {
    return new Morph(Identifier.class, function);
  }

  public static Morph morphVariables(Function<Variable, Model> function) {
    return new Morph(Variable.class, function);
  }

  public static Morph morphQuotes(Function<Quote, Model> function) {
    return new Morph(Quote.class, function);
  }

  public static Morph morphParameters(Function<Parameter, Model> function) {
    return new Morph(Parameter.class, function);
  }

  public static Morph morphLambdas(Function<Lambda, Model> function) {
    return new Morph(Lambda.class, function);
  }

  public <M extends Model> M in(Model model) {
    if (model instanceof Module module) {
      model = module(module.definitions.map(this::in));
    } else if (model instanceof Definition definition) {
      model = definition(
          in(definition.identifier),
          in(definition.body));
    } else if (model instanceof Lambda lambda) {
      model = lambda(
          in(lambda.parameter),
          in(lambda.body));
    } else if (model instanceof Application application) {
      model = application(
          in(application.function),
          in(application.argument));
    }
    if (type.isAssignableFrom(model.getClass())) {
      model = ((Function<Model, Model>) function).apply(model);
    }
    return (M) model;
  }
}
