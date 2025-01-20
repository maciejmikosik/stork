# lang.function.* #

### Functions ###

`identity` - does nothing, returns argument.
   - `identity(x)` = `x`

`constant` - always returns first argument, ignoring second.
   - `constant(x)(y)` = `x`

`compose(f)(g)` - composes functions `f` and `g`. `g` will be applied first, then `f`.
   - `compose(f)(g)(x)` = `f(g(x))`

`flip(f)` - swaps first and second arguments applied to function `f`.
   - `flip(f)(x)(y)` = `f(y)(x)`
