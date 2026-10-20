# lang/list/count/* #

### Functions ###

creates an infinite lists of integers

                       List<Integer>
               count = 0, 1, 2, 3, ...
        countFrom(3) = 3, 4, 5, 6, ...
    countDownFrom(5) = 5, 4, 3, 2, ...

### Idioms ###

There are no functions to iterate over ranges of integers. Those functions are often confusing about inclusivity/exclusivity of bounds. Instead you can combine `count` with other list functions.

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
