# lang/stream/count/* #

### Functions ###

`count` - creates an infinite stream of integers starting from `0`.
   - `count` : `0`, `1`, `2`, `3`, ...

`countFrom(n)` - creates an infinite stream of integers starting from `n`.
   - `countFrom(5)` : `5`, `6`, `7`, ...

`countTo(n)` - creates a finite stream of `n` integers from `0` (inclusively) to `n` (exclusively)
   - `countTo(5)` : `0`, `1`, `2`, `3`, `4`

`countDownFrom(n)` - creates a finite stream of `n` integers from `n` (exclusively) to `0` (inclusively)
   - `countDownFrom(5)` : `4`, `3`, `2`, `1`, `0`

### Idioms ###

There are no functions to iterate over ranges of integers. Those functions are often confusing about inclusivity/exclusivity of bounds. Instead more explicit alternative is available using `while` and `until` functions.

To iterate over following ranges use given idiom.

 - `[a` - `countFrom(a)`
 - `(a` - `countFrom(a).skip(1)`
 - `b)` - `.while(lessThan(b))`
 - `b]` - `.while(atMost(b))` or `.until(equal(b))`

example:

`[a, b)` = `countFrom(a).while(lessThan(b))`
