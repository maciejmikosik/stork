package com.mikosik.stork.data.model;

public class ExpressionVisitor<T> {
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

  protected T visit(Parameter parameter) {
    return visitDefault(parameter);
  }

  protected T visitDefault(Expression expression) {
    throw new RuntimeException();
  }

  public T visit(Expression expression) {
    if (expression instanceof Variable) {
      return visit((Variable) expression);
    } else if (expression instanceof Primitive) {
      return visit((Primitive) expression);
    } else if (expression instanceof Core) {
      return visit((Core) expression);
    } else if (expression instanceof Application) {
      return visit((Application) expression);
    } else if (expression instanceof Lambda) {
      return visit((Lambda) expression);
    } else if (expression instanceof Parameter) {
      return visit((Parameter) expression);
    } else {
      return visitDefault(expression);
    }
  }
}
