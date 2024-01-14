Stork is functional programming language with minimalistic syntax. It has no keywords and no operators. 

    main(stdin) {
      "Hello World!"
    }

### keywords as functions

There are no keywords like `if`, `else`, `true` or `false`. Bool values are just ordinary functions in core library. They use [Church encodings](https://en.wikipedia.org/wiki/Church_encoding).

    true(a)(b) { a }
    false(a)(b) { b }

Consider function that returns `"yes"` or `"no"` depending if argument is `true` or `false`. In java you would write

    public static String yesOrNo(boolean bool) {
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

### lambdas and currying

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

### Data structures as functions

Other data structures are represented using [Mogensen-Scott encoding](https://en.wikipedia.org/wiki/Mogensen%E2%80%93Scott_encoding). To get data from "fields" you need to provide visitor (one for each constructor variant) which is a function that takes "fields" as parameters.

Something similar to `java.util.Optional` can be implemented by defining two constructors `present(element)` and `absent`

    present(element)(visitPresent)(visitAbsent) {
      visitPresent(element)
    }

    absent(visitPresent)(visitAbsent) {
      visitAbsent
    }

Function that returns present integer or else 0 looks like this

    valueOf(maybeInteger) {
      maybeInteger
        ((value){ value })
        (0)
    }

`valueOf(present(5))` is `5`. `valueOf(absent)` is `0`.

### namespaces

Source code file is always named `source`. Directory path defines namespace of all functions defined inside. Using functions from other namespace require imports. Imports are in separate file named `import`. By default main function is named `main` and is in root directory/namespace.

Example stork program that takes first 10 characters from standard input, reverses them and returns to standard output.

file: source

    main(stdin) {
      reverse(limit(10)(stdin))
    }

file: import

    lang.stream.limit
    lang.stream.reverse

### Quick start

Clone project.

Build `stork` binary. Default destination is your home directory.

    ./run/build

Go to demo directory in stork project.

    cd demo/com/mikosik/stork/demo

Run stork (update path if you moved binary from home directory)

    ~/stork

Core libraries are available in `core_library` directory.
