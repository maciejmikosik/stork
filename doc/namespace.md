# Namespace #

To avoid collisions between function names, functions live in namespaces. Functions defined in one namespace are not accessible to functions from other namespace by default. Stork offers mechanics to define which namespace your functions live in and which functions from other namespaces are accessible to them.

Stork doesn't have syntax for declaring namespace/package inside source code file like other languages. Instead namespace is inferred from path of source file on filesystem. Stork requires all functions to be defined in a file named `source.stork`, so only one file is allowed per directory. That means all functions in that file live in the same namespace, defined by relative path of that file from the root of source directory.

Functions in the same `source.stork` file, since they live in the same namespace, can access each other by default. You can call one from the other without any extra work. This includes function recursively calling itself. Here you can see call from `main` function to `shout` function just by referring to its unqualified name.

    main(stdin) {
      shout("Hello World")
    }
    
    shout(message) {
      append("!")(message)
    }

However, `append` function is not defined in the same `source.stork` file. It is defined in core library in `lang/stream/source.stork` file, so it's qualified name is `lang.stream.append`. In order to access it, it has to be imported. Functions defined in `source.stork` file can access functions from other namespaces by listing qualified names of those functions in `import.stork` file in the same directory. To make above example work we need to create `import.stork` file in the same directory as `source.stork` file.

    ┣━import.stork━━━━━━━━━┓
    ┃ ┃ lang.stream.append ┃
    ┃ ┗━━━━━━━━━━━━━━━━━━━━┛
    ┗━source.stork━━━━━━━━━━━━━┓
      ┃ main(stdin) {          ┃
      ┃   shout("Hello World") ┃
      ┃ }                      ┃
      ┃                        ┃
      ┃ shout(message) {       ┃
      ┃   append("!")(message) ┃
      ┃ }                      ┃
      ┗━━━━━━━━━━━━━━━━━━━━━━━━┛

[Core library](../stork/core/lang/doc.md) is organized into several namespaces that represent different domains of programming. By default, entire core directory is included in stork binary and attached to every stork program at runtime. You can access any function from core library in your program by adding its qualified name in import file.

Your functions can access your other functions from different namespace the same way as functions from core library, by simply importing it. Every import file applies only to source file in the same directory.

So far our program fits single source file in root directory. As it grows you can split it into several namespaces. Let's move `shout` function into separate file `common/text/source.stork`.

    ┣━common
    ┃ ┗━text
    ┃   ┣━import.stork━━━━━━━━━┓
    ┃   ┃ ┃ lang.stream.append ┃
    ┃   ┃ ┗━━━━━━━━━━━━━━━━━━━━┛
    ┃   ┗━source.stork━━━━━━━━━━━━━┓
    ┃     ┃ shout(message) {       ┃
    ┃     ┃   append("!")(message) ┃
    ┃     ┃ }                      ┃
    ┃     ┗━━━━━━━━━━━━━━━━━━━━━━━━┛
    ┣━import.stork━━━━━━━━┓
    ┃ ┃ common.text.shout ┃
    ┃ ┗━━━━━━━━━━━━━━━━━━━┛
    ┗━source.stork━━━━━━━━━━━━━┓
      ┃ main(stdin) {          ┃
      ┃   shout("Hello World") ┃
      ┃ }                      ┃
      ┗━━━━━━━━━━━━━━━━━━━━━━━━┛

There is corner case when there is same namespace used in 2 separate source roots, for example your project directory and core directory. In this case functions in one source file cannot access functions from other source file. You still need to import them. Following code causes compilation error because `lang.stream.shout` cannot access core function `lang.stream.append` despite being in the same namespace.

    ┗━lang
      ┗━stream
        ┗━source.stork━━━━━━━━━━━━━┓
          ┃ shout(message) {       ┃
          ┃   append("!")(message) ┃
          ┃ }                      ┃
          ┗━━━━━━━━━━━━━━━━━━━━━━━━┛

Also you cannot define your function with the same name and namespace as core function. Following code causes compilation error.

    ┗━lang
      ┗━stream
        ┗━source.stork━━━━━━━┓
          ┃ append(stream) { ┃
          ┃   stream         ┃
          ┃ }                ┃
          ┗━━━━━━━━━━━━━━━━━━┛
