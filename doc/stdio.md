# Stdio #

Stork programs cannot have side-effects. They can only interact with standard streams: stdin and stdout.

Access to stdin is enabled by passing `stdin` stream as a parameter to `main` function. Whatever is returned from `main` function is written to `stdout`. You can create program that does nothing by passing stdin to stdout.

`source.stork`

    main(stdin) {
      stdin
    }

console

    $ printf "Hello World!" | stork
    Hello World!

Both `stdin` and `stdout` are [lists](../stork/core/lang/list/doc.md) so you can perform list operations on them, like `reverse` or `append`.

`import.stork`

    lang/list/reverse
    lang/list/append

`source.stork`

    main(stdin) {
      stdin
        .reverse
        .append(stdin)
    }

console

    $ printf "abc" | stork
    cbaabc

Both, `stdin` and `stdout` are lists of [integers](../stork/core/lang/integer/doc.md). Those integers represent bytes being read/written. You can perform integer arithmetics on those bytes.

`import.stork`

    lang/list/each
    lang/integer/add

`source.stork`

    main(stdin) {
      stdin.each(add(1))
    }

console

    $ printf "HAL" | stork
    IBM

String literal is a list of integers representing printable (range 32-126) ASCII characters, so you can write them to `stdout` as bytes without encoding characters.

`source.stork`

    main(stdin) {
      "Hello World!"
    }

console

    $ stork
    Hello World!

