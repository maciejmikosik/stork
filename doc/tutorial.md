# Tutorial #

This Tutorial teaches you basic features of stork:
   - building stork binary
   - writing hello world program
   - using core library
   - imports and namespaces
   - functions: definition, currying

All programs in this tutorial print `Hello World!`

### Hello World! ###

Build stork compiler by running bash script (requires Java 22).

    $ ./run/build

It creates stork binary in local bin directory `~/.local/bin/stork`.

Or you can download built binary from project's [release page](https://github.com/maciejmikosik/stork/releases).

Create a file named `source.stork` with following contents.

    main(stdin) {
      "Hello World!"
    }

Run it by executing stork binary in same directory (requires Java 21).

    $ stork
    Hello World!

By default stork binary searches for file named `source.stork` in current directory and launches function named `main`.

There is no need for `return` keyword since function definition contains single expression of what is returned. There are no local variables. Function has no return type since stork is untyped.

### [Namespace](namespace.md) ###

Stork ships with [core library](../stork/core/lang/doc.md) for basic operations like integer math, boolean operators, processing lists. To import a function from core library, create a file named `import.stork` in the same directory as `source.stork` file and specify fully qualified name of a function you want to import.

`import.stork`

    lang/list/append

Now you can use `append` function in your `source.stork` file.

    main(stdin) {
      append("!")("Hello World")
    }

### [Function](function.md) ###

Syntax for calling function `f` with argument `x` is `f(x)`. Syntax for calling function with many arguments is `f(x)(y)(z)`. Syntax for defining function `f` with parameter `x` is `function(x) { ... }`. Syntax for defining function with many arguments is `function(x)(y)(z) { ... }`.

Let's define `shout` function that appends exclamation mark.

    main(stdin) {
      shout("Hello World")
    }
    
    shout(string) {
      append("!")(string)
    }

Stork supports [currying](https://en.wikipedia.org/wiki/Currying) so you can apply functions partially. Here we have function `append` that has 2 parameters. We call it with 2 arguments: `"!"` and `"Hello World"`. They can be applied in 2 separate places.

    main(stdin) {
      shout("Hello World")
    }
    
    shout {
      append("!")
    }

Functions are static-like, but they can be called like instance methods.

    main(stdin) {
      "Hello World".append("!")
    }

Instance calls can be chained.

    main(stdin) {
      "World"
        .prepend("Hello ")
        .append("!")
    }

Chain of instance calls can be composed into a pipe.

    main(stdin) {
      greet("World")
    }
    
    greet {
      .prepend("Hello ")
      .append("!")
    }

### other concepts ###

 - [Stdio](stdio.md)
 - [Data](data.md)
 - [Interface](interface.md)
