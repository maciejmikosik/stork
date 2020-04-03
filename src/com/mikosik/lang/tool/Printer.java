package com.mikosik.lang.tool;

import static java.lang.String.format;

import com.mikosik.lang.model.runtime.Application;
import com.mikosik.lang.model.runtime.Expression;
import com.mikosik.lang.model.runtime.Lambda;
import com.mikosik.lang.model.runtime.Primitive;
import com.mikosik.lang.model.runtime.Variable;

public class Printer {
  private Printer() {}

  public static Printer printer() {
    return new Printer();
  }

  public String print(Expression expression) {
    if (expression instanceof Primitive) {
      Primitive primitive = (Primitive) expression;
      return primitive.object.toString();
    } else if (expression instanceof Variable) {
      Variable variable = (Variable) expression;
      return variable.name;
    } else if (expression instanceof Application) {
      Application application = (Application) expression;
      return format("%s(%s)", print(application.function), print(application.argument));
    } else if (expression instanceof Lambda) {
      Lambda lambda = (Lambda) expression;
      return lambda.body instanceof Lambda
          ? format("(%s)%s", lambda.parameter, print(lambda.body))
          : format("(%s){ %s }", lambda.parameter, print(lambda.body));
    } else {
      throw new RuntimeException();
    }
  }
}
