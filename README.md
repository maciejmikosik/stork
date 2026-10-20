Stork is untyped functional programming language.


### Hello World! ###

    main(stdin) {
      "Hello World!"
    }

    main(stdin) {
      append("!")("Hello World")
    }

    main(stdin) {
      "Hello World".append("!")
    }

    main(stdin) {
      "World"
        .prepend("Hello ")
        .append("!")
    }

### shout Hello World ###

    shout(string) {              main(stdin) {
      append("!")(string)          shout("Hello World")
    }                            }

    shout {                      main(stdin) {
      append("!")                  "Hello World".shout
    }                            }

### greet World ###

    greet(string) {              main(stdin) {
      string                       greet("World")
        .prepend("Hello ")       }
        .append("!")
    }                            main(stdin) {
                                   "World".greet
    greet {                      }
      .prepend("Hello ")
      .append("!")
    }

    greet {
      .prepend("Hello ")
      .shout
    }

[Tutorial](doc/tutorial.md)
