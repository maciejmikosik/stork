Stork is untyped functional programming language.

    main(stdin) {
      "Hello World!"
    }

# boolean logic #

There are no keywords like `if`, `else`, `true` or `false`. Boolean values are just ordinary functions in core library. They use [Church encodings](https://en.wikipedia.org/wiki/Church_encoding#Church_Booleans).

    true(a)(b) { a }
    false(a)(b) { b }

Consider function that returns `"yes"` or `"no"` depending if argument is `true` or `false`. In javascript you would write

    function yesOrNo(bool) {
      return bool ? "yes" : "no";
    }

In stork you write

    yesOrNo(bool) {
      bool("yes")("no")
    }

and using it in main

    main(stdin) {
      yesOrNo(true)
    }

prints `yes`.

# functional features #

Stork supports [currying](https://en.wikipedia.org/wiki/Currying) and [anonymous functions/lambdas](https://en.wikipedia.org/wiki/Lambda_calculus#lambdaAbstr).

Increment function can be constructed as lambda

    (x) { add(1)(x) }

or using currying

    add(1)

or defined as named function

    inc(x) {
      add(1)(x)
    }

or simpler

    inc {
      add(1)
    }

Syntax is designed so putting name before lambda turns it into function definition.

       (x) { add(1)(x) }
    inc(x) { add(1)(x) } 

All functions are static, but you can invoke them like instance methods.

 - `x.add(1)` = `add(1)(x)`
 - `inc(x).add(5)` = `add(5)(inc(x))`=
 - `"Hello World".append("!")` = `append("!")("Hello World")`

Instance invocations can be chained.

    main(stdin) {
      surround("*")("Hello World")
    }
    
    surround(affix)(string) {
      string
        .prepend(affix)
        .append(affix)
    }

Or composed into pipes.

    surround(affix) {
      .prepend(affix)
      .append(affix)
    }

# namespaces #

Source code file is always named `source.stork`. Directory path defines namespace of all functions defined inside. Using functions from other namespace require imports. Imports are in separate file named `import.stork`. By default main function is named `main` and is in root directory/namespace.

Example stork program that takes first 10 characters from standard input, reverses them and returns to standard output.

`source.stork`

    main(stdin) {
      stdin
        .limit(10)
        .reverse
    }

`import.stork`

    lang.stream.limit
    lang.stream.reverse

# big integers #

Integers can be arbitrarily big.

    main(stdin) {
        format(-123456789012345678901234567890)
    }

prints `-123456789012345678901234567890`.

# data as function #

Structures are encoded using [Mogensen-Scott encoding](https://en.wikipedia.org/wiki/Mogensen%E2%80%93Scott_encoding)

    something(element)(vSomething)(vNothing) {
      vSomething(element)
    }

    nothing(vSomething)(vNothing) {
      vNothing
    }

    valueOf(maybeInteger) {
      maybeInteger
        ((value){ value })
        (0)
    }

`valueOf(something(5))` is `5`. `valueOf(nothing)` is `0`.

# Tutorial #

Learn basic features from [tutorial](doc/tutorial.md).
