Stork is functional programming language with minimalistic syntax. It has no keywords and no operators. 

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

More about encoding data as functions in [documentation](doc/data.md).

# lambdas and currying #

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


# namespaces #

Source code file is always named `source`. Directory path defines namespace of all functions defined inside. Using functions from other namespace require imports. Imports are in separate file named `import`. By default main function is named `main` and is in root directory/namespace.

Example stork program that takes first 10 characters from standard input, reverses them and returns to standard output.

file: source

    main(stdin) {
      reverse(limit(10)(stdin))
    }

file: import

    lang.stream.limit
    lang.stream.reverse

# quick start #

Clone project.

Build `stork` binary. Default destination is your home directory.

    ./run/build

Go to demo directory in stork project.

    cd demo

Run stork (update path if you moved binary from home directory)

    ~/stork

# documentation #

[Documentation](doc/stork.md)
