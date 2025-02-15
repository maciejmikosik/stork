Stork is untyped functional programming language.

    main(stdin) {
      "Hello World!"
    }

# boolean logic #

There are no keywords like `if`, `else`, `true` or `false`. Boolean values are just ordinary functions in core library. They use [Church encodings](https://en.wikipedia.org/wiki/Church_encoding#Church_Booleans).

In javascript you would write

    function yesOrNo(bool) {
      return bool ? "yes" : "no";
    }
    
    console.log(yesOrNo(true));

In stork you write

    yesOrNo(bool) {
      bool("yes")("no")
    }
    
    main(stdin) {
      yesOrNo(true)
    }

prints `yes`.

# functional features #

Increment function can be constructed as [anonymous functions (lambda abstraction)](https://en.wikipedia.org/wiki/Lambda_calculus#lambdaAbstr)

    (x) { add(1)(x) }

or using [currying](https://en.wikipedia.org/wiki/Currying)

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

All functions are static, but you can invoke them like instance methods and chain them.

    main(stdin) {
      "World"
        .prepend("Hello ")
        .append("!")
    }

And compose invocations into pipes.

    main(stdin) {
      greet("World")
    }
    
    greet {
      .prepend("Hello ")
      .append("!")
    }

# namespaces #

All imports are in separate files.

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
