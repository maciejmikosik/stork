package com.mikosik.stork.test.cases.language;

import static com.mikosik.stork.compile.err.Problems.unexpectedToken;
import static com.mikosik.stork.model.token.IntegerLiteral.literal;
import static com.mikosik.stork.model.token.Label.label;
import static com.mikosik.stork.model.token.Punctuator.DOT;
import static com.mikosik.stork.model.token.Punctuator.LEFT_CURLY_BRACKET;
import static com.mikosik.stork.model.token.Punctuator.LEFT_ROUND_BRACKET;
import static com.mikosik.stork.model.token.Punctuator.RIGHT_CURLY_BRACKET;
import static com.mikosik.stork.model.token.Punctuator.RIGHT_ROUND_BRACKET;
import static com.mikosik.stork.test.ProgramTest.programTest;
import static org.quackery.Suite.suite;

import org.quackery.Test;

import com.mikosik.stork.model.token.Token;

public class TestParserProblems {
  public static Test testParserProblems() {
    var y = label("y");
    var eight = literal(8);
    return suite("parser reports unexpected token in")
        .add(suite("invocation")
            .add(suite("argument")
                .add(snippet("f y", y))
                .add(snippet("f(x y", y))
                .add(snippet("f(x{", LEFT_CURLY_BRACKET))
                .add(snippet("f(x}", RIGHT_CURLY_BRACKET))
                .add(snippet("f(x 8", eight)))
            .add(suite("body")
                .add(snippet("f(x))", RIGHT_ROUND_BRACKET))
                .add(snippet("f(x)}", RIGHT_CURLY_BRACKET))))
        .add(suite("lambda")
            .add(suite("parameter")
                .add(snippet("(8", eight))
                .add(snippet("((", LEFT_ROUND_BRACKET))
                .add(snippet("()", RIGHT_ROUND_BRACKET))
                .add(snippet("({", LEFT_CURLY_BRACKET))
                .add(snippet("(}", RIGHT_CURLY_BRACKET))
                .add(snippet("(.", DOT))
                .add(snippet("(x y", y))
                .add(snippet("(x 8", eight))
                .add(snippet("(x(", LEFT_ROUND_BRACKET))
                .add(snippet("(x{", LEFT_CURLY_BRACKET))
                .add(snippet("(x}", RIGHT_CURLY_BRACKET))
                .add(snippet("(x.", DOT)))
            .add(suite("body")
                .add(snippet("(x) y", y))
                .add(snippet("(x) )", RIGHT_ROUND_BRACKET))
                .add(snippet("(x) }", RIGHT_CURLY_BRACKET))
                .add(snippet("(x) { x y", y)))
            .add(suite("application")
                // TODO this should be degraded to warning
                .add(snippet("(x){x}('ok')", LEFT_ROUND_BRACKET))))
        .add(suite("chain")
            .add(snippet("x.)", RIGHT_ROUND_BRACKET))
            .add(snippet("x.{", LEFT_CURLY_BRACKET))
            .add(snippet("x.}", RIGHT_CURLY_BRACKET))
            .add(snippet("x..", DOT)))
        .add(suite("pipe")
            .add(snippet(".x.)", RIGHT_ROUND_BRACKET))
            .add(snippet(".x.{", LEFT_CURLY_BRACKET))
            .add(snippet(".x.}", RIGHT_CURLY_BRACKET))
            .add(snippet(".)", RIGHT_ROUND_BRACKET))
            .add(snippet(".}", RIGHT_CURLY_BRACKET))
            .add(snippet("..", DOT))
            .add(snippet(".x..", DOT)))
        .add(suite("function definition")
            .add(source("8", eight))
            .add(source("(", LEFT_ROUND_BRACKET))
            .add(source(")", RIGHT_ROUND_BRACKET))
            .add(source("{", LEFT_CURLY_BRACKET))
            .add(source("}", RIGHT_CURLY_BRACKET))
            .add(source(".", DOT))
            .add(source("main(stdin) y", y))
            .add(source("main(stdin) { x y", y)));
  }

  private static Test snippet(String snippet, Token token) {
    return programTest(snippet)
        .source(("main(stdin) { %s }").formatted(snippet))
        .expect(unexpectedToken().object(token));
  }

  private static Test source(String definition, Token token) {
    return programTest(definition)
        .source(definition)
        .expect(unexpectedToken()
            .object(token));
  }

}
