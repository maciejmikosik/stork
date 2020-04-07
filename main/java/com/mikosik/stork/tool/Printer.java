package com.mikosik.stork.tool;

import static com.mikosik.stork.model.runtime.Visitor.visit;
import static java.lang.String.format;

import com.mikosik.stork.model.runtime.Application;
import com.mikosik.stork.model.runtime.Expression;
import com.mikosik.stork.model.runtime.Lambda;
import com.mikosik.stork.model.runtime.Primitive;
import com.mikosik.stork.model.runtime.Variable;
import com.mikosik.stork.model.runtime.Visitor;

public class Printer {
  private Printer() {}

  public static Printer printer() {
    return new Printer();
  }

  public String print(Expression expression) {
    return visit(expression, new Visitor<String>() {
      protected String visit(Primitive primitive) {
        return primitive.object.toString();
      }

      protected String visit(Variable variable) {
        return variable.name;
      }

      protected String visit(Application application) {
        return format("%s(%s)", print(application.function), print(application.argument));
      }

      protected String visit(Lambda lambda) {
        return lambda.body instanceof Lambda
            ? format("(%s)%s", lambda.parameter, print(lambda.body))
            : format("(%s){ %s }", lambda.parameter, print(lambda.body));
      }
    });
  }
}