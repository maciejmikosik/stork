package com.mikosik.stork.test.cases;

import static com.mikosik.stork.test.SnippetSuite.snippetSuite;
import static org.quackery.Suite.suite;

import org.quackery.Test;

public class TestStream {
  public static Test testStream() {
    return suite("stream")
        .add(snippetSuite("constructor")
            .importing("lang.stream.some")
            .importing("lang.stream.none")
            .test("some(120)(some(121)(some(122)(none)))", "xyz")
            .test("none", ""))
        .add(snippetSuite("hasSome")
            .importing("lang.stream.hasSome")
            .importing("lang.stream.some")
            .importing("lang.stream.none")
            .test("hasSome(none)", false)
            .test("hasSome(some(1)(none))", true)
            .test("hasSome(some(1)(some(2)(some(3)(none))))", true))
        .add(snippetSuite("hasNone")
            .importing("lang.stream.hasNone")
            .importing("lang.stream.some")
            .importing("lang.stream.none")
            .test("hasNone(none)", true)
            .test("hasNone(some(1)(none))", false)
            .test("hasNone(some(1)(some(2)(some(3)(none))))", false))
        .add(snippetSuite("maybeHead")
            .importing("lang.stream.maybeHead")
            .importing("lang.stream.some")
            .importing("lang.stream.none")
            .test("maybeHead(some('head')(none)) ((element){element})('nothing')", "head")
            .test("maybeHead(none) ((element){element})('nothing')", "nothing"))
        .add(snippetSuite("maybeTail")
            .importing("lang.stream.maybeTail")
            .importing("lang.stream.some")
            .importing("lang.stream.none")
            .test("maybeTail(some('head')('tail')) ((element){element})('nothing')", "tail")
            .test("maybeTail(none) ((element){element})('nothing')", "nothing"))
        .add(snippetSuite("single")
            .importing("lang.stream.single")
            .test("single(120)", "x"))
        .add(snippetSuite("repeat")
            .importing("lang.stream.repeat")
            .importing("lang.stream.limit")
            .test("limit(5)(repeat(120))", "xxxxx"))
        .add(snippetSuite("cycle")
            .importing("lang.stream.cycle")
            .importing("lang.stream.limit")
            .test("limit(10)(cycle('abc'))", "abcabcabca")
            .test("limit(10)(cycle('a'))", "aaaaaaaaaa")
            .test("cycle('')", ""))
        .add(snippetSuite("iterate")
            .importing("lang.stream.iterate")
            .importing("lang.stream.limit")
            .importing("lang.integer.add")
            .test("limit(10)(iterate(add(1))(97))", "abcdefghij"))
        .add(snippetSuite("each")
            .importing("lang.stream.each")
            .importing("lang.integer.add")
            .test("each(add(1))('ace')", "bdf")
            .test("each(add(1))('')", ""))
        .add(snippetSuite("eachMaybe")
            .importing("lang.stream.eachMaybe")
            .importing("lang.integer.equal")
            .importing("lang.maybe.something")
            .importing("lang.maybe.nothing")
            .test("""
                eachMaybe((char) {
                  equal(33)(char)
                    (nothing)
                    (something(char))
                })
                ('abc!de')
                  """,
                "abcde")
            .test("""
                eachMaybe((char) {
                  equal(33)(char)
                    (nothing)
                    (something(char))
                })
                ('')
                  """,
                ""))
        .add(snippetSuite("reduce")
            .importing("lang.stream.reduce")
            .importing("lang.stream.append")
            .importing("lang.stream.some")
            .importing("lang.stream.none")
            .test("reduce('a')(append)(some('x')(some('y')(some('z')(none))))", "axyz")
            .test("reduce('')(append)(none)", ""))
        .add(snippetSuite("filter")
            .importing("lang.stream.filter")
            .importing("lang.integer.moreThan")
            .test("filter(moreThan(100))('axbycz')", "xyz")
            .test("filter(moreThan(100))('xyz')", "xyz")
            .test("filter(moreThan(100))('')", ""))
        .add(snippetSuite("limit")
            .importing("lang.stream.limit")
            .test("limit(5)('abcde')", "abcde")
            .test("limit(4)('abcde')", "abcd")
            .test("limit(6)('abcde')", "abcde")
            .test("limit(0)('abcde')", "")
            .test("limit(0)('')", ""))
        .add(snippetSuite("skip")
            .importing("lang.stream.skip")
            .test("skip(4)('abcde')", "e")
            .test("skip(5)('abcde')", "")
            .test("skip(6)('abcde')", "")
            .test("skip(0)('abcde')", "abcde")
            .test("skip(-1)('abcde')", "abcde"))
        .add(snippetSuite("while")
            .importing("lang.stream.while")
            .importing("lang.integer.lessThan")
            .test("while(lessThan(110))('abcde')", "abcde")
            .test("while(lessThan(110))('xabcde')", "")
            .test("while(lessThan(110))('abcxde')", "abc")
            .test("while(lessThan(110))('abcdex')", "abcde")
            .test("while(lessThan(110))('x')", "")
            .test("while(lessThan(110))('')", ""))
        .add(snippetSuite("until")
            .importing("lang.stream.until")
            .importing("lang.integer.equal")
            .test("until(equal(33))('abcde')", "abcde")
            .test("until(equal(33))('!abcde')", "!")
            .test("until(equal(33))('abc!de')", "abc!")
            .test("until(equal(33))('abcde!')", "abcde!")
            .test("until(equal(33))('!')", "!")
            .test("until(equal(33))('')", ""))
        .add(snippetSuite("append")
            .importing("lang.stream.append")
            .test("append('x')('abc')", "abcx")
            .test("append('x')('')", "x"))
        .add(snippetSuite("prepend")
            .importing("lang.stream.prepend")
            .test("prepend('x')('abc')", "xabc")
            .test("prepend('x')('')", "x"))
        .add(snippetSuite("flatten")
            .importing("lang.stream.flatten")
            .importing("lang.stream.some")
            .importing("lang.stream.none")
            .test("flatten(some('abc')(some('de')(some('f')(none))))", "abcdef")
            .test("flatten(some('abc')(none))", "abc")
            .test("flatten(some('')(some('')(some('')(none))))", "")
            .test("flatten(some('')(none))", "")
            .test("flatten(some('abc')(some('')(some('def')(none))))", "abcdef")
            .test("flatten(none)", ""))
        .add(snippetSuite("transpose")
            .importing("lang.stream.transpose")
            .importing("lang.stream.flatten")
            .importing("lang.stream.some")
            .importing("lang.stream.none")
            .test("flatten(transpose(some('abc')(some('de')(some('f')(none)))))", "adfbec")
            .test("flatten(transpose(some('abc')(none)))", "abc")
            .test("flatten(transpose(some('')(some('')(some('')(none)))))", "")
            .test("flatten(transpose(some('')(none)))", "")
            .test("flatten(transpose(some('abc')(some('')(some('def')(none)))))", "adbecf")
            .test("flatten(none)", ""))
        .add(snippetSuite("equal")
            .importing("lang.stream.some")
            .importing("lang.stream.none")
            .importing("lang.stream.equal")
            .importing("lang.integer.equal eq")
            .test("equal(eq)('')('')", true)
            .test("equal(eq)('a')('a')", true)
            .test("equal(eq)('ab')('ab')", true)
            .test("equal(eq)('abc')('abc')", true)
            .test("equal(eq)('a')('')", false)
            .test("equal(eq)('')('a')", false)
            .test("equal(eq)('a')('b')", false)
            .test("equal(eq)('ab')('a')", false)
            .test("equal(eq)('a')('ab')", false)
            .test("equal(eq)('ab')('ac')", false)
            .test("equal(eq)('abc')('ab')", false)
            .test("equal(eq)('ab')('abc')", false)
            .test("equal(eq)('abc')('abd')", false))
        .add(snippetSuite("startsWith")
            .importing("lang.integer.equal")
            .importing("lang.stream.startsWith")
            .test("startsWith(equal)('a')('abc')", true)
            .test("startsWith(equal)('a')('a')", true)
            .test("startsWith(equal)('a')('xyz')", false)
            .test("startsWith(equal)('abc')('a')", false)
            .test("startsWith(equal)('')('abc')", true))
        .add(snippetSuite("length")
            .importing("lang.stream.length")
            .test("length('')", 0)
            .test("length('xyz')", 3))
        .add(snippetSuite("contains")
            .importing("lang.integer.equal")
            .importing("lang.stream.contains")
            .test("contains(equal(0))('xyz')", false)
            .test("contains(equal(120))('xyz')", true))
        .add(snippetSuite("reverse")
            .importing("lang.stream.reverse")
            .test("reverse('abcde')", "edcba")
            .test("reverse('')", ""))
        .add(snippetSuite("sort")
            .importing("lang.stream.sort")
            .importing("lang.integer.moreThan")
            .test("sort(moreThan)('1928374650')", "0123456789")
            .test("sort(moreThan)('0123456789')", "0123456789")
            .test("sort(moreThan)('0101010101')", "0000011111")
            .test("sort(moreThan)('10')", "01")
            .test("sort(moreThan)('01')", "01")
            .test("sort(moreThan)('0')", "0")
            .test("sort(moreThan)('')", ""))
        .add(snippetSuite("maybeAt")
            .importing("lang.stream.maybeAt")
            .test("maybeAt(-1)('abc')((x){x})(-100)", -100)
            .test("maybeAt(0)('abc')((x){x})(-100)", 97)
            .test("maybeAt(1)('abc')((x){x})(-100)", 98)
            .test("maybeAt(2)('abc')((x){x})(-100)", 99)
            .test("maybeAt(3)('abc')((x){x})(-100)", -100)
            .test("maybeAt(0)('')((x){x})(-100)", -100));
  }
}
