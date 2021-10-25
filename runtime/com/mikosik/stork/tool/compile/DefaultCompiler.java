package com.mikosik.stork.tool.compile;

import com.mikosik.stork.model.Module;

public class DefaultCompiler {
  public static Compiler<Module> defaultCompiler() {
    WhitespaceCompiler whitespace = new WhitespaceCompiler();
    AlphanumericCompiler alphanumeric = new AlphanumericCompiler();
    IntegerCompiler integer = new IntegerCompiler();
    QuoteCompiler quote = new QuoteCompiler();

    integer.alphanumeric = alphanumeric;

    VariableCompiler variable = new VariableCompiler();
    LambdaCompiler lambda = new LambdaCompiler();
    ScopeCompiler scope = new ScopeCompiler();
    BodyCompiler body = new BodyCompiler();
    InvocationCompiler invocation = new InvocationCompiler();
    ExpressionCompiler expression = new ExpressionCompiler();

    variable.alphanumeric = alphanumeric;
    lambda.whitespace = whitespace;
    lambda.alphanumeric = alphanumeric;
    lambda.body = body;
    scope.whitespace = whitespace;
    scope.expression = expression;
    body.lambda = lambda;
    body.scope = scope;
    invocation.whitespace = whitespace;
    invocation.variable = variable;
    invocation.expression = expression;
    expression.integer = integer;
    expression.lambda = lambda;
    expression.quote = quote;
    expression.invocation = invocation;

    DefinitionCompiler definition = new DefinitionCompiler();
    ModuleCompiler module = new ModuleCompiler();

    definition.whitespace = whitespace;
    definition.variable = variable;
    definition.body = body;
    module.whitespace = whitespace;
    module.definition = definition;

    return module;
  }
}
