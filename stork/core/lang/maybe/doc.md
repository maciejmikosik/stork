# lang/maybe/* #

Maybe represents data that can contain a single element or be empty. It is similar to `Maybe` in haskell or `Optional` in java.

### Data ###

Maybe has 2 constructors: `something(element)`, `nothing`.

### Functions ###

`default(defaultElement)(maybe)` - returns the `element` from `something(element)`, or `defaultElement` if `maybe` is `nothing`.
   - `default(defaultElement)(something(element))` = `element`
   - `default(defaultElement)(nothing)` = `defaultElement`
