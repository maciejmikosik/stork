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

### Import Inheritance ###

Import file should inherit from import files in parent directory. This should handle name shadowing. This allows to put commonly imported function once in root so when function moves or renames it would require only single line change to adjust.

### Shell Commands ###

Ability to call system shell commands from stork.

    exec(command)(arguments) {
      ...
    }

    exec("cat")(single("path/to/file"))

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

### Constant-time Array ###

Fixed size arrays with constant access time by index. Easy if they are immutable, but modification would require some kind of copy-on-write mechanism.

### Multi-line Strings ###

    "first line"
    "second line"

    "first line""second line"

### String Formatter ###

    greet(name) {
      "hello %name"
    }

### Other ###
   - library for writing unit tests
   - annotations
   - types
   - tail recursion using `EAGER`
   - imports that rename function
