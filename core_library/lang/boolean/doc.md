# lang.boolean.* #

Stork has no keywords like `if`, `else`, `true` or `false`.  Instead it uses [church encodings](https://en.wikipedia.org/wiki/Church_encoding#Church_Booleans) to implement boolean algebra.

### Data ###

Boolean values, `true` and `false`, are just ordinary functions. 

    true(a)(b) { a }
    false(a)(b) { b }

Consider function that returns `"yes"` or `"no"` depending if argument is `true` or `false`. In javascript you write

    function yesOrNo(bool) {
      return bool ? "yes" : "no";
    }

in stork you write

    yesOrNo(bool) {
      bool("yes")("no")
    }
    
and using it in main

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

`format` - Takes boolean and converts it to string, either `"true"` or `"false"`.
