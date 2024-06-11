Stork is functional programming language with minimalistic syntax. It has no keywords and no operators. 

    main(stdin) {
      "Hello World!"
    }

### keywords as functions

There are no keywords like `if`, `else`, `true` or `false`. Bool values are just ordinary functions in core library. They use [Church encodings](https://en.wikipedia.org/wiki/Church_encoding#Church_Booleans).

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

**Structure with fields**

Structure with fields can be represented by implementing single constructor function (`person(name)(age)`) with parameters of fields (`name`, `age`) and then taking visitor function as next parameter (`visitor`). Visitor function is invoked and constructor fields are passed as arguments.

    person(name)(age)(visitor) {
      visitor(name)(age)
    }

Visitor anonymous function (`(name)(age) { atLeast(18)(age) }`) takes fields as parameters.

    isAdult(person) {
      person((name)(age) {
        atLeast(18)(age)
      })
    }


    main(stdin) {
      yesOrNo(isAdult(person("John")(23)))
    }

prints `yes`.

Reuse of words `person`, `name`, `age` as lambda parameters is just a convention and does no interfere with `person(name)(age)` constructor definition. It could just as well be

    isAdult(p) {
      p((n)(a) {
        atLeast(18)(a)
      })
    }

**Multiple constructors**

There can be many constructors (`something(element)`, `nothing`) with different parameters (`element`, none). You just need to provide that many visitors (`vSomething`, `vNothing`) as number of constructors, each taking as many parameters as given constructor have. Example of maybe/optional would be


    something(element)(vSomething)(vNothing) {
      vSomething(element)
    }

    nothing(vSomething)(vNothing) {
      vNothing
    }

Function that returns integer from `something` or else `0` (if `nothing`) looks like this

    valueOf(maybeInteger) {
      maybeInteger
        ((value){ value })
        (0)
    }

`valueOf(something(5))` is `5`. `valueOf(nothing)` is `0`.

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

    cd demo

Run stork (update path if you moved binary from home directory)

    ~/stork

Core libraries are available in `core_library` directory.
