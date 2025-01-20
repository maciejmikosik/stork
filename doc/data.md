# Data #

Data structures are encoded as functions. This includes: enums, structures with fields, collections etc.

### Enums ###

Simple example would be enum with 2 constants, like boolean. It has 2 constants: `true` or `false`. Boolean is encoded using [Church encodings](https://en.wikipedia.org/wiki/Church_encoding#Church_Booleans). `true` and `false` are functions with 2 parameters and return either first or second.

    true(vTrue)(vFalse) { vTrue }
    false(vTrue)(vFalse) { vFalse }


Let's implement function that returns `"yes"` or `"no"` depending if argument is `true` or `false`. In javascript you would write

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

Computation:

    yesOrNo(true)
    true("yes")("no")
    "yes"

Since boolean enum has 2 constants, each constants is a function that has 2 parameters called visitors. In case with more constants, we need to provide as many visitors.

Let's implement `TrafficLight` enum with 3 constants: `red`, `yellow` and `green`.

    red(vRed)(vYellow)(vGreen) { vRed }
    yellow(vRed)(vYellow)(vGreen) { vYellow }
    green(vRed)(vYellow)(vGreen) { vGreen }

    canGoOn(trafficLight) {
      trafficLight
        (false)
        (false)
        (true)
    }

    main(stdin) {
      yesOrNo(canGoOn(yellow))
    }

prints `no`.

Computation:

    yesOrNo(canGoOn(yellow))
    yesOrNo(yellow(false)(false)(true))
    yesOrNo(false)
    false("yes")("no")
    "no"

### Structures with fields ###

Structure can be encoded as function with parameters that represents fields. It has one extra parameter that is a visitor function. Visitor function is invoked by passing fields as arguments. Let's implement data of a person that has name and age.

    person(name)(age)(visitor) {
      visitor(name)(age)
    }

You can create an instance of person by invoking constructor: `person("John")(23)`. You may pass that instance around and any code that wants to inspect values of fields can invoke that instance like it's a function by providing visitor. In this case, visitor is a function with 2 parameters.

Let's implement functions that checks if given person is an adult.

    isAdult(person) {
      person((name)(age) {
        atLeast(18)(age)
      })
    }

    main(stdin) {
      yesOrNo(isAdult(person("John")(23)))
    }

prints `yes`.

Computation:

    yesOrNo(isAdult(person("John")(23)))
    yesOrNo(person("John")(23)((name)(age) { atLeast(18)(age) }))
    yesOrNo((name)(age){ atLeast(18)(age) }("John")(23))
    yesOrNo((age) { atLeast(18)(age) }(23))
    yesOrNo(atLeast(18)(23))
    yesOrNo(true)
    "yes"

It takes a person structure and invokes it like a function by providing visitor. In this case visitor is provided as anonymous function that takes fields as arguments. `(name)(age) { atLeast(18)(age) }`.

Reuse of words `person`, `name`, `age` as lambda parameters is just a convention and does not interfere with `person(name)(age)` constructor definition. It could just as well be

    isAdult(p) {
      p((n)(a) {
        atLeast(18)(a)
      })
    }

### Structures with multiple constructors ###

Structures and enums can be seen as specific cases of more general [Mogensen-Scott encoding](https://en.wikipedia.org/wiki/Mogensen%E2%80%93Scott_encoding). In case of structure we had one constructor with parameters. In case of enum we had multiple constructors with no parameters. But we can have multiple constructors with parameters and each constructor can even have different number of parameters. 

Simple example is [maybe](../core_library/lang/maybe/doc.md). It has 2 constructors. One with parameter `something(element)`. Other without `nothing`. That's why we will have 2 visitors, one with one parameter, other with no parameters.

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

Another example is [stream](../core_library/lang/stream/doc.md). Stream can have element (`head`) and a pointer to rest of the stream (`tail`) or be empty.

    some(head)(tail)(vSome)(vNone) {
      vSome(head)(tail)
    }

    none(vSome)(vNone) {
      vNone
    }
