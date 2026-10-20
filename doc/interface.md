# Interface #


In stork you can't formally define interfaces, but they exist through conventions. They exist as informal contract for a function, that is required to have certain parameters and return type.

### Predicate ###

Predicate is a function with 1 parameter that returns boolean. It tells if that argument matches predicate.

Predicates can be defined as standalone function.

    divisibleBy(divisor)(number) {
      equal(0)(modulo(divisor)(number))
    }

They can be built by currying binary integer operators: `equal(3)`, `atLeast(5)`. 

They are used in list functions `filter`, `while`, `contains`.

### Comparator ###

Comparator is a function with 2 parameters that returns boolean. It tells if they are in ascending order. It is used as parameter in sorting algorithms. Order of sorting can be reversed using `lang/function/flip` on comparator.
