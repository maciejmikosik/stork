# Function #

### Invocation ###

Syntax for calling function `f` with single argument `x` is `f(x)`. For example, function `lang/list/reverse` has one parameter of type list. It reverses order of elements in that list.

    main(stdin) {
      reverse("!dlroW olleH")
    }

Syntax for calling function `f` with 2 arguments `x`, `y` is `f(x)(y)`. For example `lang/list/append` has 2 parameters. First is a list to append. Second is a list to append to.

    main(stdin) {
      append("!")("Hello World")
    }

Result of function invocation can be nested as an argument to another function.

    main(stdin) {
      append("!")(prepend("Hello ")("World"))
    }

Syntax for calling a function `f` with 3 arguments `x`, `y`, `z` is `f(x)(y)(z)` and so on.

### Chain ###

In stork, all functions are static-like. There are no instance methods like in object-oriented languages. This can make some invocations look awkward, like in previous nested example. To solve this problem, stork offers compiler-sugar that allows you to invoke functions in instance-method style.

    main(stdin) {
      "Hello World".append("!")
    }

Also, it allows you to chain invocations just like in object-oriented languages.

    main(stdin) {
      "World"
        .prepend("Hello ")
        .append("!")
    }

`object` is applied to `function` as last argument.

    `object.function` = `function(object)`
    `object.f(x)(y)` = `f(x)(y)(object)`
    `function(object).f(x)` = `f(x)(function(object))`

Function application `f(x)` has stronger association than dot operator `.`.

    `object.f(x).g(y).h(z)` = `h(z)(g(y)(f(x)(object)))`

Instance can be any expression including
 - application `reverse("abc").append("!")`
 - string literal `"abc".append("!")`
 - integer literal `1.add(2)`

### Definition ###

Syntax for defining a function with single parameter is `f(x) { ... }`, where `f` is function's name, `x` is function's parameter and `...` is function's body. Body is an expression that is returned from a function.

`shout` is a function that takes one parameter `message` and appends an exclamation mark to it.

    main(stdin) {
      shout("Hello World")
    }
    
    shout(message) {
      message.append("!")
    }

Syntax for defining a function with 2 parameters is `f(x)(y) { ... }`, where `y` is second parameter.

`yell` takes 2 parameters. Integer representing `loudness` and `message` to yell.

    main(stdin) {
      yell(3)("Hello World")
    }
    
    yell(loudness)(message) {
      message
        .append(cycle("!").limit(loudness))
    }

prints `Hello World!!!`

Syntax for defining a function with 3 parameters is `f(x)(y)(z) { ... }` and so on.

If you define function with no parameters, it turns into a constant, like `message` below.

    main(stdin) {
      message
    }
    
    message {
      "Hello World!"
    }

### Currying ###

Stork supports [currying](https://en.wikipedia.org/wiki/Currying), which means that all functions have only single parameter. When you call `f(x)(y)`, function `f` is called with one argument `x` and that call returns some new function `g`. Then function `g` is called with argument `y`. This means you can apply functions partially, by providing less arguments than necessary. Result will be some function that can be passed around as an argument to other functions.

Here we have partial application `append("!")` with only one argument. This returns new function that can be passed around. This function is passed to `twice` function where second argument `object` is provided.

    main(stdin) {
      "Hello World"
        .twice(append("!"))
    }
    
    twice(action)(object) {
      object
        .action
        .action
    }

Using this feature can make some definitions simpler by removing dangling parameter through [η-reduction](https://en.wikipedia.org/wiki/Combinatory_logic#Simplifications_of_the_transformation).

We can rewrite some of previous examples using η-reduction.

    shout {
      append("!")
    }

    yell(loudness) {
      append(cycle("!").limit(loudness))
    }


Notice the order of parameters in `prepend`, `append` and other list functions. Deliberately, string which we append is first and string which we append to is last. Think about last parameter like it's `this` object in object-oriented programming. This makes partial application grammatical and human-readable. Also it makes chaining possible. For example, functions operating on lists have list as its last parameter.

    main(stdin) {
      shoutTimes(3)("Hello World")
    }
    
    shoutTimes(n)(message) {
      message
        .repeat
        .limit(n)
        .each(append("!"))
        .each(append(" "))
        .flatten
    }

prints `Hello World! Hello World! Hello World! `.

### Pipe ###

Chains of invocations can be extracted into pipes. Pipe is a composition of several functions into one function. Syntax is similar to chaining instance methods, but without initial instance.

We can rewrite previous examples.

    twice(action)(object) {
      .action
      .action
    }
    
    shoutTimes(n) {
      .repeat
      .limit(n)
      .each(append("!"))
      .each(append(" "))
      .flatten
    }

### Lambda abstraction ###

Stork supports [anonymous functions/lambdas](https://en.wikipedia.org/wiki/Lambda_calculus#lambdaAbstr). Lambda is a function that doesn't have a name and cannot be reused. It's defined ad hoc right in a place where it is passed as an argument.

    main(stdin) {
      "a1b2c3d4e5"
        .filter((character) {
          and
            (atLeast(a)(character))
            (atMost(z)(character))
      })
    }
    
    a { 97 }
    z { a.add(25) }

prints `abcde`.

Above code uses lambda with single parameter `character`. Lambda returns `true` if character is in range `a` to `z`. This filters lowercase letters from given string.

Lambdas has same syntax as function definition, just without name. You can extract lambda and turn it into named function by simply copying lambda expression and adding name of function in front. Equivalent code below.

    main(stdin) {
      "a1b2c3d4e5"
        .filter(isLetter)
    }
    
    isLetter(character) {
      and
        (atLeast(a)(character))
        (atMost(z)(character))
    }
    
    a { 97 }
    z { a.add(25) }

Lambda with multiple parameters is constructed by nesting lambdas with single parameter: `(x) { (y) { ... } }`. Nested curly brackets `{}` are redundant so you can simplify to `(x)(y) { ... }`, just like with function definitions with multiple parameters.
