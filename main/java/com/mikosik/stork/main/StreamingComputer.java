package com.mikosik.stork.main;

import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.data.model.Application.application;
import static com.mikosik.stork.data.model.Lambda.lambda;
import static com.mikosik.stork.data.model.Parameter.parameter;
import static com.mikosik.stork.data.model.Variable.variable;
import static com.mikosik.stork.data.model.comp.Computation.computation;
import static com.mikosik.stork.data.model.comp.Function.function;
import static com.mikosik.stork.main.Streamed.streamed;
import static com.mikosik.stork.tool.common.Computations.isComputable;
import static com.mikosik.stork.tool.common.Translate.asJavaBigInteger;

import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Parameter;
import com.mikosik.stork.data.model.Variable;
import com.mikosik.stork.data.model.comp.Argument;
import com.mikosik.stork.data.model.comp.Computation;
import com.mikosik.stork.tool.comp.Computer;

public class StreamingComputer implements Computer {
  public static final Variable writeByte = variable("program.writeByte");
  public static final Variable writeStream = variable("program.writeStream");

  private final Computer nextComputer;

  private StreamingComputer(Computer nextComputer) {
    this.nextComputer = nextComputer;
  }

  public static Computer streaming(Computer nextComputer) {
    return new StreamingComputer(nextComputer);
  }

  /**
   * <pre>
   *   (stream) {
   *     stream
   *       ((head)(tail) {
   *         program.writeByte(head)(program.writeStream(tail))
   *       })
   *       (streamed(-1))
   *   }
   * </pre>
   */
  public static Expression writeStreamFunction() {
    Parameter head = parameter("head");
    Parameter tail = parameter("tail");
    Parameter stream = parameter("stream");
    return lambda(stream,
        ap(stream,
            lam(head, tail,
                ap(writeByte, head, ap(writeStream, tail))),
            streamed(-1)));
  }

  public Computation compute(Computation computation) {
    if (computation.expression instanceof Variable) {
      Variable variable = (Variable) computation.expression;
      if (variable == writeByte) {
        Argument argument = (Argument) computation.stack;
        Expression expression = argument.expression;
        return isComputable(expression)
            ? computation(
                expression,
                function(computation.expression, argument.stack))
            : computation(
                streamed(asJavaByte(argument.expression)),
                argument.stack);
      }
      if (variable == writeStream) {
        return computation(
            writeStreamFunction(),
            computation.stack);
      }
    } else if (computation.expression instanceof Streamed) {
      Parameter x = parameter("x");
      Expression identity = lambda(x, x);
      return computation(identity, computation.stack);
    }
    return nextComputer.compute(computation);
  }

  private static int asJavaByte(Expression expression) {
    int oneByte = asJavaBigInteger(expression).intValueExact();
    check(0 <= oneByte && oneByte <= 255);
    return oneByte;
  }

  private static Expression lam(Parameter p1, Parameter p2, Expression body) {
    return lambda(p1, lambda(p2, body));
  }

  private static Expression ap(Expression function, Expression... arguments) {
    Expression result = function;
    for (Expression argument : arguments) {
      result = application(result, argument);
    }
    return result;
  }
}
