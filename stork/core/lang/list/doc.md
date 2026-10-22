# lang/list/* #

### Data ###

List has 2 constructors: `some(head)(tail)`, `none`.

List represents an ordered collection of elements. List can be a node that contains an element (`head`) and a reference to another node (`tail`), or it can be empty (`none`).

List can be created by manually linking nodes. Example of fixed sized lists would be:

    some(❶)(none) = ❶
    some(❶)(some(❷)(none)) = ❶❷
    some(❶)(some(❷)(some(❸)(none))) = ❶❷❸

List can be finite or infinite. Finite list (like examples above) terminates with a node that references an empty list (`none`) as its tail. Some operations on infinite list are not possible. For example calling `length` on infinite list hangs the program.

List is singly linked. This means accessing random element is not constant-time operation since accessing an element requires iterating through all elements ahead of it.

### Functions ###

             List<?> ➝ Boolean
           hasSome() = false
          hasSome(○) = true
        hasSome(○○○) = true
     hasSome(○○○...) = true
           hasNone() = true
          hasNone(○) = false
        hasNone(○○○) = false
     hasNone(○○○...) = false

##### generating #####

                E ➝ List<E>
        single(○) = ○
        repeat(○) = ○○○...

          List<E> ➝ List<E>
       cycle(❶❷❸) = ❶❷❸❶❷❸...

         (E➝E)(E) ➝ List<E>
    iterate(f)(○) = ○, f(○), f(f(○)), f(f(f(○))), ...

##### filtering #####

    (E➝Boolean)(List<E>) ➝ List<E>
                    list = ❶❷①❸②❹❺③④❻⑤❼⑥❽❾⑦⑧⑨
         filter(●)(list) = ❶❷❸❹❺❻❼❽❾

##### cutting #####

      (Integer)(List<E>) ➝ List<E>
                    list = ❶❷❸❹❺❻❼❽❾
          limit(3)(list) = ❶❷❸
           skip(3)(list) = ❹❺❻❼❽❾

If `Integer` is out of range, it is clamped.

      (E➝Boolean)(List<E>) ➝ List<E>
    
    ▏ while ▕
    ●●●●●●●●●○●●●●●●●●●
    
    ▏ before▕▏  since ▕
    ○○○○○○○○○●○○○○○○○○○
    ▏ until  ▕▏ after ▕

`before` and `until` returns the entire list if no matching element was found.

##### processing #####

              (E➝R)(List<E>) ➝ List<R>
                each(f)(❶❷❸) = f(❶),f(❷),f(❸)

      (E)((E)(E)➝E)(List<E>) ➝ List<E>
           reduce(❶)(f)(❷❸❹) = ❶.f(❷).f(❸).f(❹)

`reduce` is simliar to [fold](https://en.wikipedia.org/wiki/Fold_(higher-order_function)). It goes through `list` starting from the first element like `foldl`, but element from list is the first argument to `f` like in `foldr`.

##### more than one list #####

        (List<E>)(List<E>) ➝ List<E>
          append(○○○)(●●●) = ●●●○○○
         prepend(○○○)(●●●) = ○○○●●●

      (E->Boolean)(List<E>) ➝ List<List<E>>
    splitAround(●)(①❷③④❺❻⑦) = ①,③④,|,⑦
        splitAround(●)(❶❷❸) = |,|,|,|
        splitAround(●)(①②③) = ①②③

        List<List<E>> ➝ List<E>
    flatten(❶❷❸,❹,❺❻) = ❶❷❸❹❺❻

          List<List<E>> ➝ List<List<E>>
    transpose(❶❷❸,❹,❺❻) = ❶❹❺,❷❻,❸

          List<E> ➝ List<List<E>
    prefixes(❶❷❸) = |,❶,❶❷,❶❷❸
    suffixes(❶❷❸) = ❶❷❸,❷❸,❸,|

    (List<E➝Boolean>)(List<E>) ➝ Boolean
           hasPrefix(●●)(❶❷③④) = true
           hasPrefix(●○)(❶❷③④) = false
            hasInfix(●●)(❶❷③④) = true
            hasInfix(●○)(❶❷③④) = true
            hasInfix(○○)(❶❷③④) = true
            hasInfix(○●)(❶❷③④) = false
           hasSuffix(○○)(❶❷③④) = true
           hasSuffix(●○)(❶❷③④) = false
           matches(●●○○)(❶❷③④) = true

##### other #####

        List<?> ➝ Integer
    length(○○○) = 3

         List<E> ➝ List<E>
    reverse(❶❷❸) = ❸❷❶

    ((E)(E)➝Boolean)(List<E>) ➝ List<E>
    sort(moreThan)(❽❼❹❷❶❸❻❾❺) = ❶❷❸❹❺❻❼❽❾
