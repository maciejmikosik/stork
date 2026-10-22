package com.mikosik.stork.test.suites.core;

import static com.mikosik.stork.test.ProgramTest.programTest;
import static com.mikosik.stork.test.SnippetSuite.snippetSuite;
import static org.quackery.Suite.suite;

import org.quackery.Test;

public class TestList {
  public static Test testList() {
    return suite("list")
        .add(snippetSuite("constructor")
            .importing("lang/list/some")
            .importing("lang/list/none")
            .test("some(120)(some(121)(some(122)(none)))", "xyz")
            .test("none", "")
            .build())
        .add(snippetSuite("hasSome")
            .importing("lang/list/hasSome")
            .importing("lang/list/some")
            .importing("lang/list/none")
            .test("hasSome(none)", false)
            .test("hasSome(some(1)(none))", true)
            .test("hasSome(some(1)(some(2)(some(3)(none))))", true)
            .build())
        .add(snippetSuite("hasNone")
            .importing("lang/list/hasNone")
            .importing("lang/list/some")
            .importing("lang/list/none")
            .test("hasNone(none)", true)
            .test("hasNone(some(1)(none))", false)
            .test("hasNone(some(1)(some(2)(some(3)(none))))", false)
            .build())
        .add(snippetSuite("single")
            .importing("lang/list/single")
            .test("single(120)", "x")
            .build())
        .add(snippetSuite("repeat")
            .importing("lang/list/repeat")
            .importing("lang/list/limit")
            .test("limit(5)(repeat(120))", "xxxxx")
            .build())
        .add(snippetSuite("cycle")
            .importing("lang/list/cycle")
            .importing("lang/list/limit")
            .test("limit(10)(cycle('abc'))", "abcabcabca")
            .test("limit(10)(cycle('a'))", "aaaaaaaaaa")
            .test("cycle('')", "")
            .build())
        .add(snippetSuite("iterate")
            .importing("lang/list/iterate")
            .importing("lang/list/limit")
            .importing("lang/integer/add")
            .test("limit(10)(iterate(add(1))(97))", "abcdefghij")
            .build())
        .add(snippetSuite("each")
            .importing("lang/list/each")
            .importing("lang/integer/add")
            .test("each(add(1))('ace')", "bdf")
            .test("each(add(1))('')", "")
            .build())
        .add(snippetSuite("reduce")
            .importing("lang/list/reduce")
            .importing("lang/list/append")
            .importing("lang/list/some")
            .importing("lang/list/none")
            .test("reduce('a')(append)(some('x')(some('y')(some('z')(none))))", "axyz")
            .test("reduce('')(append)(none)", "")
            .build())
        .add(snippetSuite("filter")
            .importing("lang/list/filter")
            .importing("lang/integer/moreThan")
            .test("filter(moreThan(100))('axbycz')", "xyz")
            .test("filter(moreThan(100))('xyz')", "xyz")
            .test("filter(moreThan(100))('')", "")
            .build())
        .add(snippetSuite("limit")
            .importing("lang/list/limit")
            .test("limit(5)('abcde')", "abcde")
            .test("limit(4)('abcde')", "abcd")
            .test("limit(6)('abcde')", "abcde")
            .test("limit(0)('abcde')", "")
            .test("limit(0)('')", "")
            .build())
        .add(snippetSuite("skip")
            .importing("lang/list/skip")
            .test("skip(4)('abcde')", "e")
            .test("skip(5)('abcde')", "")
            .test("skip(6)('abcde')", "")
            .test("skip(0)('abcde')", "abcde")
            .test("skip(-1)('abcde')", "abcde")
            .build())
        .add(snippetSuite("while")
            .importing("lang/list/while")
            .importing("lang/integer/lessThan")
            .test("while(lessThan(110))('abcde')", "abcde")
            .test("while(lessThan(110))('xabcde')", "")
            .test("while(lessThan(110))('abcxde')", "abc")
            .test("while(lessThan(110))('abcdex')", "abcde")
            .test("while(lessThan(110))('x')", "")
            .test("while(lessThan(110))('')", "")
            .build())
        .add(suite("since")
            .add(cuts("since", "abcd", ""))
            .add(cuts("since", "!abcd", "!abcd"))
            .add(cuts("since", "ab!de", "!de"))
            .add(cuts("since", "abcd!", "!"))
            .add(cuts("since", "!", "!"))
            .add(cuts("since", "", "")))
        .add(suite("until")
            .add(cuts("until", "abcd", "abcd"))
            .add(cuts("until", "!abcd", "!"))
            .add(cuts("until", "ab!de", "ab!"))
            .add(cuts("until", "abcd!", "abcd!"))
            .add(cuts("until", "!", "!"))
            .add(cuts("until", "", "")))
        .add(suite("before")
            .add(cuts("before", "abcd", "abcd"))
            .add(cuts("before", "!abcd", ""))
            .add(cuts("before", "ab!de", "ab"))
            .add(cuts("before", "abcd!", "abcd"))
            .add(cuts("before", "!", ""))
            .add(cuts("before", "", "")))
        .add(suite("after")
            .add(cuts("after", "abcd", ""))
            .add(cuts("after", "!abcd", "abcd"))
            .add(cuts("after", "ab!de", "de"))
            .add(cuts("after", "abcd!", ""))
            .add(cuts("after", "!", ""))
            .add(cuts("after", "", "")))
        .add(snippetSuite("append")
            .importing("lang/list/append")
            .test("append('x')('abc')", "abcx")
            .test("append('x')('')", "x")
            .build())
        .add(snippetSuite("prepend")
            .importing("lang/list/prepend")
            .test("prepend('x')('abc')", "xabc")
            .test("prepend('x')('')", "x")
            .build())
        .add(suite("splitAround")
            .add(split("Around", "abcd", "abcd."))
            .add(split("Around", "ab!cd", "ab.cd."))
            .add(split("Around", "a!b!c!d", "a.b.c.d."))
            .add(split("Around", "!ab!cd!", ".ab.cd.."))
            .add(split("Around", "!!", "..."))
            .add(split("Around", "!", ".."))
            .add(split("Around", "", ".")))
        .add(snippetSuite("flatten")
            .importing("lang/list/flatten")
            .importing("lang/list/some")
            .importing("lang/list/none")
            .test("flatten(some('abc')(some('de')(some('f')(none))))", "abcdef")
            .test("flatten(some('abc')(none))", "abc")
            .test("flatten(some('')(some('')(some('')(none))))", "")
            .test("flatten(some('')(none))", "")
            .test("flatten(some('abc')(some('')(some('def')(none))))", "abcdef")
            .test("flatten(none)", "")
            .build())
        .add(snippetSuite("transpose")
            .importing("lang/list/transpose")
            .importing("lang/list/flatten")
            .importing("lang/list/some")
            .importing("lang/list/none")
            .test("some('abc')(some('def')(some('ghi')(none))).transpose.flatten", "adgbehcfi")
            .test("some('abc')(some('de')(some('f')(none))).transpose.flatten", "adfbec")
            .test("some('abc')(none).transpose.flatten", "abc")
            .test("some('a')(none).transpose.flatten", "a")
            .test("some('')(some('')(some('')(none))).transpose.flatten", "")
            .test("some('')(none).transpose.flatten", "")
            .test("some('abc')(some('')(some('def')(none))).transpose.flatten", "adbecf")
            .test("none.transpose.flatten", "")
            .build())
        .add(snippetSuite("prefixes")
            .importing("lang/list/prefixes")
            .importing("lang/list/flatten")
            .importing("lang/list/length")
            .test("prefixes('abc').flatten", "aababc")
            .test("prefixes('abc').length", 4)
            .test("prefixes('a').flatten", "a")
            .test("prefixes('a').length", 2)
            .test("prefixes('').flatten", "")
            .test("prefixes('').length", 1)
            .build())
        .add(snippetSuite("suffixes")
            .importing("lang/list/suffixes")
            .importing("lang/list/flatten")
            .importing("lang/list/length")
            .test("suffixes('abc').flatten", "abcbcc")
            .test("suffixes('abc').length", 4)
            .test("suffixes('a').flatten", "a")
            .test("suffixes('a').length", 2)
            .test("suffixes('').flatten", "")
            .test("suffixes('').length", 1)
            .build())
        .add(suite("hasPrefix")
            .add(has("Prefix", "abc", "abc", true))
            .add(has("Prefix", "ab", "abc", true))
            .add(has("Prefix", "a", "abc", true))
            .add(has("Prefix", "", "abc", true))
            .add(has("Prefix", "", "", true))
            .add(has("Prefix", "x", "abc", false))
            .add(has("Prefix", "abcx", "abc", false))
            .add(has("Prefix", "xx", "x", false))
            .add(has("Prefix", "x", "", false)))
        .add(suite("hasInfix")
            .add(has("Infix", "abc", "abc", true))
            .add(has("Infix", "ab", "abc", true))
            .add(has("Infix", "bc", "abc", true))
            .add(has("Infix", "a", "abc", true))
            .add(has("Infix", "b", "abc", true))
            .add(has("Infix", "c", "abc", true))
            .add(has("Infix", "", "abc", true))
            .add(has("Infix", "", "", true))
            .add(has("Infix", "x", "abc", false))
            .add(has("Infix", "xx", "x", false))
            .add(has("Infix", "x", "", false)))
        .add(suite("hasSuffix")
            .add(has("Suffix", "abc", "abc", true))
            .add(has("Suffix", "bc", "abc", true))
            .add(has("Suffix", "c", "abc", true))
            .add(has("Suffix", "", "abc", true))
            .add(has("Suffix", "", "", true))
            .add(has("Suffix", "x", "abc", false))
            .add(has("Suffix", "xabc", "abc", false))
            .add(has("Suffix", "xx", "x", false))
            .add(has("Suffix", "x", "", false)))
        .add(suite("matches")
            .add(matches("abc", "abc", true))
            .add(matches("bc", "abc", false))
            .add(matches("c", "abc", false))
            .add(matches("", "abc", false))
            .add(matches("", "", true))
            .add(matches("x", "abc", false))
            .add(matches("xabc", "abc", false))
            .add(matches("xx", "x", false))
            .add(matches("x", "", false)))
        .add(snippetSuite("length")
            .importing("lang/list/length")
            .test("length('')", 0)
            .test("length('xyz')", 3)
            .build())
        .add(snippetSuite("reverse")
            .importing("lang/list/reverse")
            .test("reverse('abcde')", "edcba")
            .test("reverse('')", "")
            .build())
        .add(snippetSuite("sort")
            .importing("lang/list/sort")
            .importing("lang/integer/moreThan")
            .test("sort(moreThan)('1928374650')", "0123456789")
            .test("sort(moreThan)('0123456789')", "0123456789")
            .test("sort(moreThan)('0101010101')", "0000011111")
            .test("sort(moreThan)('10')", "01")
            .test("sort(moreThan)('01')", "01")
            .test("sort(moreThan)('0')", "0")
            .test("sort(moreThan)('')", "")
            .build());
  }

  private static Test cuts(
      String function,
      String list,
      String expected) {
    return programTest(list + " " + expected)
        .imports("""
            lang/list/%s
            lang/integer/equal
            """
            .formatted(function))
        .source("""
            main(stdin) { %s(equal(33))("%s") }
            """
            .formatted(function, list))
        .stdout(expected);
  }

  private static Test split(
      String suffix,
      String list,
      String expected) {
    var function = "split" + suffix;
    return programTest(function + " " + list + " " + expected)
        .imports("""
            lang/list/%s
            lang/integer/equal
            lang/list/each
            lang/list/append
            lang/list/flatten
            """
            .formatted(function))
        .source("""
            main(stdin) {
              %s(equal(33))("%s")
                .each(append("."))
                .flatten
            }
            """
            .formatted(function, list))
        .stdout(expected);
  }

  private static Test has(
      String what,
      String xfix,
      String list,
      boolean expected) {
    return testMatch("has" + what, xfix, list, expected);
  }

  private static Test matches(
      String xfix,
      String list,
      boolean expected) {
    return testMatch("matches", xfix, list, expected);
  }

  private static Test testMatch(
      String function,
      String xfix,
      String list,
      boolean expected) {
    return programTest("%s(%s)(%s)=%s"
        .formatted(function, xfix, list, expected))
            .imports("""
                lang/list/%s
                lang/integer/equal
                lang/list/each
                lang/boolean/format
                """
                .formatted(function))
            .source("""
                main(stdin) {
                    %s("%s".each(equal))("%s").format
                }
                """
                .formatted(function, xfix, list))
            .stdout(Boolean.toString(expected));
  }
}
