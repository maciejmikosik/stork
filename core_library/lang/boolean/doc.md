# lang.boolean.* #

Stork has no keywords like `if`, `else`, `true` or `false`.  Instead it uses [church encodings](https://en.wikipedia.org/wiki/Church_encoding#Church_Booleans) to implement boolean algebra.

### Data ###

Boolean is enum with 2 constants: `true`, `false`.

Example usage:

    yesOrNo(bool) {
      bool("yes")("no")
    }

    main(stdin) {
      yesOrNo(true)
    }

prints `yes`.

### Functions ###

Basic boolean algebra functions are available.

Unary. They have 1 boolean parameter and return boolean
 - `not` - negation

Binary. They have 2 boolean parameters and return boolean
 - `equal` - equivalence
 - `and` - conjuction
 - `or` - disjuction
 - `xor` - exclusive or

To convert boolean value to string use `format(bool)`. It returns either `"true"` or `"false"`.

### Short-circuiting ###

Since all arguments to functions are computed lazily, boolean operators support [short-circuiting](https://en.wikipedia.org/wiki/Short-circuit_evaluation) by default. They compute first argument and skip second if unnecessary. Following code `and(false)(longComputation(argument))` will not invoke `longComputation`. This applies to operators `and` and `or`.
