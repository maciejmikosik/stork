# lang/stream/* #

### Data ###

Stream has 2 constructors: `some(head)(tail)`, `none`.

Stream represents an ordered collection of elements. Stream can be a node that contains an element (`head`) and a reference to another node (`tail`), or it can be empty (`none`).

Stream can be created by manually linking nodes. Example of fixed sized streams would be:

    some(❶)(none) = ❶
    some(❶)(some(❷)(none)) = ❶❷
    some(❶)(some(❷)(some(❸)(none))) = ❶❷❸

Stream can be finite or infinite. Finite stream (like examples above) terminates with a node that references an empty stream (`none`) as its tail. Some operations on infinite stream are not possible. For example calling `length` on infinite stream hangs the program.

Stream is similar to singly linked list. This means accessing random element is not constant-time operation since accessing an element requires iterating through all elements ahead of it.

### Functions ###

           Stream<?> ➝ Boolean
           hasSome() = false
          hasSome(○) = true
        hasSome(○○○) = true
     hasSome(○○○...) = true
           hasNone() = true
          hasNone(○) = false
        hasNone(○○○) = false
     hasNone(○○○...) = false

##### generating #####

                E ➝ Stream<E>
        single(○) = ○
        repeat(○) = ○○○...

        Stream<E> ➝ Stream<E>
       cycle(❶❷❸) = ❶❷❸❶❷❸...

         (E➝E)(E) ➝ Stream<E>
    iterate(f)(○) = ○, f(○), f(f(○)), f(f(f(○))), ...

##### filtering #####

    (E➝Boolean)(Stream<E>) ➝ Stream<E>
    stream ●●○●○●●○○●○●○●●○○○
    filter ●● ● ●●  ● ● ●●

    (E➝Boolean)(Stream<E>) ➝ Stream<E>

    ▏ while ▕
    ●●●●●●●●●○●●●●●●●●●

    ▏ until  ▕
    ○○○○○○○○○●○○○○○○○○○

##### cutting #####

    (Integer)(Stream<E>) ➝ Stream<E>
                  stream = ○○○○○○○
        limit(3)(stream) = ○○○
         skip(3)(stream) =    ○○○○

If `Integer` is out of range, it is clamped.

##### processing #####

            (E➝R)(Stream<E>) ➝ Stream<R>
                each(f)(❶❷❸) = f(❶),f(❷),f(❸)

    (E)((E)(E)➝E)(Stream<E>) ➝ Stream<E>
           reduce(❶)(f)(❷❸❹) = ❶.f(❷).f(❸).f(❹)

`reduce` is simliar to [fold](https://en.wikipedia.org/wiki/Fold_(higher-order_function)). It goes through `stream` starting from the first element like `foldl`, but element from stream is the first argument to `f` like in `foldr`.

##### more than one stream #####

    (Stream<E>)(Stream<E>) ➝ Stream<E>
          append(○○○)(●●●) = ●●●○○○
         prepend(○○○)(●●●) = ○○○●●●

    Stream<Stream<E>> ➝ Stream<E>
    flatten(❶❷❸,❹,❺❻) = ❶❷❸❹❺❻

      Stream<Stream<E>> ➝ Stream<Stream<E>>
    transpose(❶❷❸,❹,❺❻) = ❶❹❺,❷❻,❸

##### other #####

      Stream<?> ➝ Integer
    length(○○○) = 3

       Stream<E> ➝ Stream<E>
    reverse(❶❷❸) = ❸❷❶

##### experimental #####

`equal(equalElements)(streamA)(streamB)` - returns true if `streamA` and `streamB` contain the same elements in the same order. `equalElements` function is used to compare elements. It takes 2 parameters and returns boolean. It is intended to use with finite streams since infinite streams that are equal would hang the program.
   - `equal(equalInteger)("abc")("abc")` = `true`
   - `equal(equalInteger)("abc")("abx")` = `false`
   - `equal(equalInteger)("abc")("ab")` = `false`

`startsWith(equal)(prefix)(stream)` - checks if `stream` starts with `prefix` stream. Function `equal` is used to compare elements in those streams.
   - `startsWith(equalInteger)("a")("abc")` = `true`
   - `startsWith(equalInteger)("a")("a")` = `true`
   - `startsWith(equalInteger)("a")("xyz")` = `false`
   - `startsWith(equalInteger)("abc")("a")` = `false`
   - `startsWith(equalInteger)("")("abc")` = `true`

`contains(predicate)(stream)` - checks if `stream` has any element that matches `predicate`.
   - `contains(isDigit)("ab8cd")` = `true`
   - `contains(isDigit)("abcd")` = `false`

`sort(comparator)(stream)` - sorts elements in `stream` in order defined by `comparator`.
   - `sort(moreThan)("1928374650")` = `"0123456789"`
