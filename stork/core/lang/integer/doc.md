# lang/integer/* #

Integer is primitive type representing an integer number. There is no limit on the size of integer, so overflow or underflow never happens. Integers are only expressions that cannot be applied like functions. Invoking `123(x)` causes an error.

### Data ###

Integer can be defined using literal expression: `0`, `123`, `-123`. Also non-canonical literals are allowed: `+123` = `123`, `-0` = `0`, `0123` = `123`.

### Functions ###

`equal(numberA)(numberB)` - tests 2 numbers for equality

`moreThan(threshold)(number)` - true if `number` is greater than `threshold` 

`lessThan(threshold)(number)` - true if `number` is lower than `threshold` 

`atLeast(threshold)(number)` - true if `number` is equal or greater than `threshold`

`atMost(threshold)(number)` - true if `number` is equal or lower than `threshold`

`negate(number)` - returns opposite number
   - `negate(5)` = `-5`
   - `negate(-3)` = `3`
   - `negate(0)` = `0`

`add(numberA)(numberB)` - returns sum of 2 numbers: `numberA` + `numberB`.

`subtract(numberA)(numberB)` - returns difference of 2 numbers: `numberB` - `numberA`.
   - `subtract(1)(10)` = `9`

`increment(number)` - increases `number` by `1`.

`decrement(number)` - decreases `number` by `1`.

`multiply(numberA)(numberB)` - multiplies 2 numbers.

`divideBy(divisor)(dividend)` and `modulo(divisor)(dividend)` are 2 complementary binary operations. Implementations guarantee that if `a/n=q remainder r` then `a=n*q+r` and `|r|<|n|`. Stork implements [truncated division](https://en.wikipedia.org/wiki/Modulo). Dividing by `0` causes error.
   - `divideBy(3)(7)` = `2`, `modulo(3)(7)` = `1`
   - `divideBy(3)(-7)` = `-2`, `modulo(3)(-7)` = `-1`
   - `divideBy(-3)(7)` = `-2`, `modulo(-3)(7)` = `1`
   - `divideBy(-3)(-7)` = `2`, `modulo(-3)(-7)` = `-1`

`sign(number)` - returns `1`, `0`, `-1` if `number` is respectively positive, equal to 0, negative.

`absolute(number)` - negates negative `number`, leaves other values unchanged.

`makeAtLeast(minimum)(number)` - returns `minimum` if `number` < `minimum`. Returns `number` otherwise.

`makeAtMost(maximum)(number)` - returns `maximum` if `number` > `maximum`. Returns `number` otherwise.

`relu(number)` - returns `0` if `number` is negative. Returns `number` otherwise.

`format(number)` - converts number to string in base 10.

`formatBase(base)(number)` - converts number to string in specified `base`. Maximum value of `base` can be 36.
