package com.mikosik.stork;

import static com.mikosik.stork.Snippet.snippet;
import static org.quackery.Suite.suite;

import org.quackery.Suite;

public class TestSimpleFunctions {
  public static Suite testSimpleFunctions() {
    return suite("test simple functions")
        .add(snippet("returning constant")
            .define("main{5}")
            .launch("main")
            .expect("5"))
        .add(snippet("returning argument")
            .define("main(argument){argument}")
            .launch("main(10)")
            .expect("10"))
        .add(snippet("forwarding argument and result")
            .define("identity(x){x}")
            .define("main(argument){identity(argument)}")
            .launch("main(3)")
            .expect("3"));
  }
}
