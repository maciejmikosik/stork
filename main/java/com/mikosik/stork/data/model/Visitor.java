package com.mikosik.stork.data.model;

public class Visitor<T> {
  protected T visit(Variable variable) {
    return visitDefault(variable);
  }

  protected T visit(Primitive primitive) {
    return visitDefault(primitive);
  }

  protected T visit(Core core) {
    return visitDefault(core);
  }

  protected T visit(Application application) {
    return visitDefault(application);
  }

  protected T visit(Lambda lambda) {
    return visitDefault(lambda);
  }

  protected T visitDefault(Expression expression) {
    throw new RuntimeException();
  }

  public static <T> T visit(Expression expression, Visitor<T> visitor) {
    if (expression instanceof Variable) {
      return visitor.visit((Variable) expression);
    } else if (expression instanceof Primitive) {
      return visitor.visit((Primitive) expression);
    } else if (expression instanceof Core) {
      return visitor.visit((Core) expression);
    } else if (expression instanceof Application) {
      return visitor.visit((Application) expression);
    } else if (expression instanceof Lambda) {
      return visitor.visit((Lambda) expression);
    } else {
      return visitor.visitDefault(expression);
    }
  }
}
