# Application #

Syntax for calling function `f` with single argument `x` is `f(x)`. For example, function `lang.stream.reverse` has one parameter of type stream. It reverses order of elements of that stream.

    main(stdin) {
      reverse("!dlroW olleH")
    }

prints `Hello World!`.

Syntax for calling a function `f` with 2 arguments `x`, `y` is `f(x)(y)`. For example `lang.stream.append` has 2 parameters. First is a stream to append at the end of second stream parameter.

    main(stdin) {
      append("!")("Hello World")
    }

prints `Hello World!`.

Syntax for calling a function `f` with 3 arguments `x`, `y`, `z` is `f(x)(y)(z)` and so on.

### Definition ###

Syntax for defining a function with single parameter is `f(x) { ... }`, where `f` is the name of the function, `x` is the name of parameter and `...` is function's body. Body is an expression that is returned from a function. Let's extract a function `shout` for appending exclamation mark.

    main(stdin) {
      shout("Hello World")
    }
    
    shout(message) {
      append("!")(message)
    }

Syntax for defining a function with 2 parameters is `f(x)(y) { ... }`, where `y` is second parameter. Let's create a function that adds same prefix and suffix to a message.

    main(stdin) {
      surround("*")("Hello World")
    }
    
    surround(affix)(message) {
      append(affix)(prepend(affix)(message))
    }

prints `*Hello World*`

Syntax for defining a function with 3 parameters is `f(x)(y)(z) { ... }` and so on.

If you define no parameters, function turns into constant, like `message` below.

    main(stdin) {
      message
    }
    
    message {
      "Hello World!"
    }

### Currying ###

Stork supports [currying](https://en.wikipedia.org/wiki/Currying), which means that all functions in reality have one parameter. When you call `f(x)(y)`, in reality you call function `f` with one argument `x` and that call returns some new function `g`. Then you call function `g` with argument `y`. This means you can apply functions partially, by providing less arguments than necessary. Result will be some function that can be passed around as argument to other functions.

Here we have partial application `append("!")` with only one argument. This returns new function that can be passed around. This function is passed to `twice` function where second argument `object` is provided.

    main(stdin) {
      twice(append("!"))("Hello World")
    }
    
    twice(action)(object) {
      action(action(object))
    }

prints `Hello World!!`.

Using this feature can make some definitions simpler by removing dangling parameter through [η-reduction](https://en.wikipedia.org/wiki/Combinatory_logic#Simplifications_of_the_transformation).

    shout(message) {                shout {
      append("!")(message)            append("!")
    }                               }

Notice the order of parameters in `append` function. Deliberately, string which we append is first and string which we append *to* is last. This makes partial application useful and readable. If order was different, dangling parameter couldn't be removed.

### Chain ###

In stork all functions are static, there are no instance methods like in object-oriented languages. This can make some code look awkward to write and hard to read, like `surround` function we defined before. To solve this problem, stork offers compiler-sugar that allows you to invoke static functions in instance-method style. Also, it allows you to chain invocations just like in object-oriented languages.

 - `object.function` = `function(object)`
 - `object.f(x)` = `f(x)(object)`
 - `object.f(x)(y)` = `f(x)(y)(object)`
 - `object.f(x).g(y).h(z)` = `h(z)(g(y)(f(x)(object)))`
 - `function(object).f(x)` = `f(x)(function(object))`

Instance can be any expression including
 - application `reverse("Hello World").append("!")`
 - string literal `"Hello World".append("!")`
 - integer literal `1.add(2)`

This can greatly simplify your code. Let's rewrite previous example.

    main(stdin) {
      "Hello World".surround("*")
    }
    
    surround(affix)(message) {
      message.prepend(affix).append(affix)
    }

Notice again that order of parameters matters. For example, functions operating on streams have stream as its last parameter. This allows creation of readable chain of invocations.

    main(stdin) {
      shoutTimes(3)("Hello World")
    }
    
    shoutTimes(n)(message) {
      message
        .repeat
        .limit(n)
        .each(append("!"))
        .flatten
    }

prints `Hello World!Hello World!Hello World!`.

Long chains of invocations can be extracted into pipes. Pipe is a composition of several functions into one function. Syntax is similar to chaining instance methods, but without initial instance.

    main(stdin) {
      shoutTimes(3)("Hello World")
    }
    
    shoutTimes(n) {
      .repeat
      .limit(n)
      .each(append("!"))
      .flatten
    }

### Lambda abstraction ###

Stork supports [anonymous functions/lambdas](https://en.wikipedia.org/wiki/Lambda_calculus#lambdaAbstr). Lambda is a function that doesn't have a name and cannot be reused. It's defined ad hoc right in a place where it is passed as an argument.

Below we remove exclamation mark from string.

    main(stdin) {
      quiet("Hello World!")
    }
    
    quiet {
      filter((character) { 
        not(equal(exclamationMark)(character))
      })
    }
    
    exclamationMark { 33 }

Above code uses lambda with single parameter `character`.

    (character) { 
      not(equal(exclamationMark)(character))
    }

You can extract lambda and turn it into named function by simply copying lambda's definition and adding name of function before it. Equivalent code below.

    main(stdin) {
      quiet("Hello World!")
    }
    
    quiet {
      filter(isQuietCharacter)
    }
    
    isQuietCharacter(character) { 
      not(equal(exclamationMark)(character))
    }
    
    exclamationMark { 33 }

Lambda with multiple parameters is achievable by nesting lambdas with single parameter: `(x) { (y) { ... } }`. Nested curly brackets `{}` are redundant in that case so you can simplify to `(x)(y){ ... }`, similarly like with function definitions.
