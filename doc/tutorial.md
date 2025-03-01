# Tutorial #

This Tutorial teaches you basic features of stork:
   - building stork binary
   - writing hello world program
   - using core library
   - imports and namespaces
   - functions: definition, currying, lambdas
   - standard I/O
   - defining custom data structure

All programs in this tutorial print `Hello World!`, unless specified otherwise.

### Hello World! ###

Build stork compiler by running bash script (requires Java 22).

    $ ./run/build

It creates stork binary in your home directory `~/stork`.

Create a file named `source.stork` with following contents.

    main(stdin) {
      "Hello World!"
    }

Run it by executing stork binary in same directory (requires Java 21).

    $ ~/stork
    Hello World!

By default stork binary searches for file named `source.stork` in current directory and launches function named `main`.

There is no need for `return` keyword since function definition contains single expression of what is returned. There are no local variables. Function has no return type since stork is untyped.

### Namespace ###
Full doc: [Namespace](namespace.md).

Stork ships with [core library](../stork/core/lang/doc.md) for basic operations like integer math, boolean operators, processing streams. To import a function from core library, create a file named `import.stork` in the same directory as `source.stork` file and specify fully qualified name of a function you want to import.

`import.stork`

    lang.stream.append

Now you can use `append` function in your `source.stork` file.

    main(stdin) {
      append("!")("Hello World")
    }

### Function ###
Full doc: [Function](function.md).

Syntax for calling a function `f` with argument `x` is `f(x)`. Syntax for calling a function with many arguments is `f(x)(y)(z)`.

Let's define `shout` function that appends exclamation mark.

    main(stdin) {
      shout("Hello World")
    }
    
    shout(string) {
      append("!")(string)
    }

Stork supports [currying](https://en.wikipedia.org/wiki/Currying) so you can apply functions partially. `append` has 2 parameters `"!"` and `"Hello World"`. They can be applied in 2 separate places.

    main(stdin) {
      shout("Hello World")
    }
    
    shout {
      append("!")
    }

Functions, although they are static, can still be called like instance methods. Instance call is converted to static call and instance becomes its last argument.

    main(stdin) {
      "Hello World".append("!")
    }

Instance calls can be chained.

    main(stdin) {
      "World"
        .prepend("Hello ")
        .append("!")
    }

Chain of instance calls can be composed into pipe.

    main(stdin) {
      greet("World")
    }
    
    greet {
      .prepend("Hello ")
      .append("!")
    }

Lambdas have same syntax as function definition, just without name. `(x) { ... }` or `(x)(y) { ... }` for more parameters.

### Standard I/O ###

String literal like `"Hello World!"` is [stream](../stork/core/lang/stream/doc.md) of [integers](../stork/core/lang/integer/doc.md) from ASCII table. However, `main` function is expected to return stream of bytes. Since string literals contain only ascii characters, you can return them from `main` to be printed on standard output without encoding. `stdin` is stream of integers representing bytes from standard input.

Running

`import.stork`

    lang.stream.append

`source.stork`

    main(stdin) {
      shout(stdin)
    }
    
    shout {
      append("!")
    }

console:

    echo -n "Hello World" | ~/stork

Since strings are stream of integers, you can perform integer arithmetics on characters.

`import.stork`

    lang.integer.add
    lang.stream.each

`source.stork`

    main(stdin) {
      each(add(1))("ace")
    }

prints `bde`.

### Data ###
Full doc: [Data](data.md).

Data structures are encoded as functions using [Mogensen-Scott encoding](https://en.wikipedia.org/wiki/Mogensen%E2%80%93Scott_encoding). This includes: enums, structures with fields, collections etc.

Example of enum with 2 values: `true` and `false`.

    true(vTrue)(vFalse) {
      vTrue
    }
    
    false(vTrue)(vFalse) {
      vFalse
    }

Example of structure with 1 constructor `person` and 2 fields: `name` and `age`.

    person(name)(age)(visitor) {
      visitor(name)(age)
    }

### Interface ###
Full doc: [Interface](interface.md).

In stork you can't formally define interfaces, but they exist through conventions. Simple examples are: equal, comparator, predicate.
