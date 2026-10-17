# lang/function/* #

### Functions ###

`identity` - does nothing, returns argument.
   - `identity(x)` = `x`

`constant` - always returns first argument, ignoring second.
   - `constant(x)(y)` = `x`

`then(after)(before)` - composes functions `after` and `before`. `before` will be applied first, then `after`.
   - `before.then(after)` = `.before.after`

`flip(f)` - swaps first and second arguments applied to function `f`.
   - `flip(f)(x)(y)` = `f(y)(x)`
