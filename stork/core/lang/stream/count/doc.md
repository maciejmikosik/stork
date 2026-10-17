# lang/stream/count/* #

### Functions ###

`count` - creates an infinite stream of integers increasing from `0` upwards.
   - `count` : `0`, `1`, `2`, ...

`countFrom(n)` - creates an infinite stream of integers increasing from `n` upwards.
   - `countFrom(5)` : `5`, `6`, `7`, ...

`countDownFrom(n)` - creates an infinite stream of integers decreasing from `n` downwards.
   - `countDownFrom(5)` : `4`, `3`, `2`, ...

### Idioms ###

There are no functions to iterate over ranges of integers. Those functions are often confusing about inclusivity/exclusivity of bounds. Instead you can combine `count` with other stream functions.

To achieve something similiar to imperative loop

    for(int i = 0; i < n; i++) {
      ...
    }

use

    count.limit(n)

To iterate over ranges, combine function.

 - `[a` - `countFrom(a)`
 - `(a` - `countFrom(a).skip(1)`
 - `b)` - `.while(lessThan(b))`
 - `b]` - `.while(atMost(b))` or `.until(equal(b))`

example:

`[a, b)` = `countFrom(a).while(lessThan(b))`
