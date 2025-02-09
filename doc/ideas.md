# Ideas #

Ideas for new features.

### Private Functions ###

Ability to make function private. Private functions cannot be imported from other namespaces. Implementation similar to `.gitignore` files in git. You create `ignore.stork` file that contains [glob](https://en.wikipedia.org/wiki/Glob_(programming) filters.

### Main Arguments ###

    main(args)(stdin) {
      ""
    }

### Exceptions ###

`exception(x)` = `exception`, `f(exception)` = `exception`

### Comments ###

     # this is a comment
    main(stdin) {
      "Hello World!" # another comment
    }

### Constructors ###

Compiler sugar for defining structures. Instead of writing

    some(head)(tail)(vSome)(vNone) {
      vSome(head)(tail)
    }

    none(vSome)(vNone) {
      vNone
    }

you write something like

    some(head)(tail) | none

### Multi-line Strings ###

    "first line"
    "second line"

    "first line""second line"

### String Formatter ###

    greet(name) {
      "hello %name"
    }

### Other ###

   - annotations
   - types
   - tail recursion using `EAGER`
   - imports that rename function
