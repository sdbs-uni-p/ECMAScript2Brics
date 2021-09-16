package de.uni_passau.sds.ecmascript2brics;

import dk.brics.automaton.Automaton;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AutomatonFactoryTest {

    private final String WHITESPACE_CHARACTERS =
              "\u0009" // CHARACTER TABULATION
            + "\n" // LINE FEED
            + "\u000b" // LINE TABULATION
            + "\u000c" // FORM FEED
            + "\r" // CARRIAGE RETURN
            + "\u0020" // SPACE
            + "\u0085" // NEXT LINE
            + "\u00a0" // NO-BREAK SPACE
            + "\u1680" // OGHAM SPACE MARK
            + "\u180e" // MONGOLIAN VOWEL SEPARATOR
            + "\u2000" // EN QUAD
            + "\u2001" // EM QUAD
            + "\u2002" // EN SPACE
            + "\u2003" // EM SPACE
            + "\u2004" // THREE-PER-EM SPACE
            + "\u2005" // FOUR-PER-EM SPACE
            + "\u2006" // SIX-PER-EM SPACE
            + "\u2007" // FIGURE SPACE
            + "\u2008" // PUNCTUATION SPACE
            + "\u2009" // THIN SPACE
            + "\u200a" // HAIR SPACE
            + "\u200b" // ZERO WIDTH SPACE
            + "\u200c" // ZERO WIDTH NON-JOINER
            + "\u200d" // ZERO WIDTH JOINER
            + "\u2028" // LINE SEPARATOR
            + "\u2029" // PARAGRAPH SEPARATOR
            + "\u202f" // NARROW NO-BREAK SPACE
            + "\u205f" // MEDIUM MATHEMATICAL SPACE
            + "\u2060" // WORD JOINER
            + "\u3000" // IDEOGRAPHIC SPACE
            + "\ufeff" // ZERO WIDTH NON-BREAKING SPACE
            ;

    private final String CONTROL_CHARACTERS =
              "\u0000-\u001f" // C0 CONTROLS
            + "\u007f" // DELETE
            + "\u0080-\u009f" // C1 CONTROLS
            ;

    @Test
    public void test_doesNotMatchTrailingNewline() {
        Automaton automaton = AutomatonFactory.create("abc");
        assertFalse(automaton.run("abc\n"));
        assertTrue(automaton.run("abc"));
    }

    @Test
    public void test_horizontalTab() {
        Automaton automaton = AutomatonFactory.create("\t");
        assertFalse(automaton.run("\\t"));
        assertTrue(automaton.run("\u0009"));
    }

    @Test
    public void test_escapesControlCodeWithUpperLetter() {
        Automaton automaton = AutomatonFactory.create("[" + CONTROL_CHARACTERS + "]");
        assertFalse(automaton.run("\\cC"));
        assertTrue(automaton.run("\u0003"));
    }

    @Test
    public void test_escapesControlCodeWithLowerLetter() {
        Automaton automaton = AutomatonFactory.create("[" + CONTROL_CHARACTERS + "]");
        assertFalse(automaton.run("\\cc"));
        assertTrue(automaton.run("\u0003"));
    }

    @Test
    public void test_matchesAsciiDigitsOnly() {
        Automaton automaton = AutomatonFactory.create("[0-9]");
        assertTrue(automaton.run("0"));
        assertFalse(automaton.run("߀"));
        assertFalse(automaton.run("\u07c0"));
    }

    @Test
    public void test_matchesEverythingButAsciiDigits() {
        Automaton automaton = AutomatonFactory.create("[^0-9]");
        assertFalse(automaton.run("0"));
        assertTrue(automaton.run("߀"));
        assertTrue(automaton.run("\u07c0"));
    }

    @Test
    public void test_matchesAsciiLettersOnly() {
        Automaton automaton = AutomatonFactory.create("[A-Za-z]");
        assertTrue(automaton.run("a"));
        assertFalse(automaton.run("é"));
    }

    @Test
    public void test_matchesEverythingButAsciiLetters() {
        Automaton automaton = AutomatonFactory.create("[^A-Za-z]");
        assertFalse(automaton.run("a"));
        assertTrue(automaton.run("é"));
    }

    @Test
    public void test_matchesWhitespace() {
        Automaton automaton = AutomatonFactory.create("[" + WHITESPACE_CHARACTERS + "]");
        assertTrue(automaton.run(" "));
        assertTrue(automaton.run("\t"));
        assertTrue(automaton.run("\u000b"));
        assertTrue(automaton.run("\u000c"));
        assertTrue(automaton.run("\u00a0"));
        assertTrue(automaton.run("\ufeff"));
        assertTrue(automaton.run("\n"));
        assertTrue(automaton.run("\u2029"));
        assertTrue(automaton.run("\u2003"));
        assertFalse(automaton.run("\u0001"));
        assertFalse(automaton.run("\u2013"));
    }

    @Test
    public void test_matchesEverythingButWhitespace() {
        Automaton automaton = AutomatonFactory.create("[^" + WHITESPACE_CHARACTERS + "]");
        assertFalse(automaton.run(" "));
        assertFalse(automaton.run("\t"));
        assertFalse(automaton.run("\u000b"));
        assertFalse(automaton.run("\u000c"));
        assertFalse(automaton.run("\u00a0"));
        assertFalse(automaton.run("\ufeff"));
        assertFalse(automaton.run("\n"));
        assertFalse(automaton.run("\u2029"));
        assertFalse(automaton.run("\u2003"));
        assertTrue(automaton.run("\u0001"));
        assertTrue(automaton.run("\u2013"));
    }

    @Test
    public void test_complexExpression() {
        Automaton automaton = AutomatonFactory.create("(a*b+cc*)?c*|x");
        assertTrue(automaton.run(""));
        assertTrue(automaton.run("bccc"));
        assertTrue(automaton.run("x"));
        assertTrue(automaton.run("aabbbc"));
        assertTrue(automaton.run("aabc"));
        assertTrue(automaton.run("cccccc"));
        assertTrue(automaton.run("bc"));
        assertTrue(automaton.run("c"));
        assertFalse(automaton.run("xx"));
        assertFalse(automaton.run("v"));
        assertFalse(automaton.run("b"));
        assertFalse(automaton.run("aba"));
        assertFalse(automaton.run("a"));
        assertFalse(automaton.run("ab"));
        assertFalse(automaton.run("ac"));
        assertFalse(automaton.run("aac"));
    }

    @Test
    public void test_braces() {
        Automaton automaton = AutomatonFactory.create("(((((((a)))|b))))*");
        assertTrue(automaton.run("abbabbba"));
    }

    @Test
    public void test_anyCharacter() {
        Automaton automaton = AutomatonFactory.create(".");
        assertTrue(automaton.run("x"));
        assertFalse(automaton.run("ab"));
    }

    @Test
    public void test_characterClasses() {
        Automaton automaton1 = AutomatonFactory.create("[-]");
        Automaton automaton2 = AutomatonFactory.create("[--]");
        Automaton automaton3 = AutomatonFactory.create("[---]");
        Automaton automaton4 = AutomatonFactory.create("[----]");
        Automaton automaton5 = AutomatonFactory.create("[+-]");
        Automaton automaton6 = AutomatonFactory.create("[+--]");
        assertTrue(automaton1.run("-"));
        assertTrue(automaton2.run("-"));
        assertTrue(automaton3.run("-"));
        assertTrue(automaton4.run("-"));
        assertTrue(automaton5.run("-"));
        assertTrue(automaton5.run("+"));
        assertFalse(automaton5.run(","));
        assertTrue(automaton6.run("-"));
        assertTrue(automaton6.run(","));
        assertTrue(automaton6.run("+"));
    }

    @Test
    public void test_escapings_brackets() {
        assertTrue(AutomatonFactory.create("a{2,3}").run("aa"));
        assertTrue(AutomatonFactory.create("a\\{2,3\\}").run("a{2,3}"));
        assertTrue(AutomatonFactory.create("a\\{2,3}").run("a{2,3}"));
        assertTrue(AutomatonFactory.create("\\{").run("{"));
        assertTrue(AutomatonFactory.create("{").run("{"));
        assertTrue(AutomatonFactory.create("a\\{").run("a{"));
        assertTrue(AutomatonFactory.create("}").run("}"));
        assertTrue(AutomatonFactory.create("a}").run("a}"));
        assertTrue(AutomatonFactory.create("{abc").run("{abc"));
        assertTrue(AutomatonFactory.create("a\\{abc").run("a{abc"));
        assertTrue(AutomatonFactory.create("{abc}").run("{abc}"));
        assertTrue(AutomatonFactory.create("a\\{abc}").run("a{abc}"));
        assertTrue(AutomatonFactory.create("{2bc}").run("{2bc}"));
        assertTrue(AutomatonFactory.create("{2,3bc}").run("{2,3bc}"));
        assertTrue(AutomatonFactory.create("\\[").run("["));
        assertTrue(AutomatonFactory.create("]\\[").run("]["));
        assertTrue(AutomatonFactory.create("\\]\\[").run("]["));
    }

    @Test
    public void test_escapings_special_characters() {
        assertTrue(AutomatonFactory.create(",").run(","));
        assertTrue(AutomatonFactory.create("\\,").run(","));
        assertTrue(AutomatonFactory.create("&").run("&"));
        assertTrue(AutomatonFactory.create("\\&").run("&"));
        assertFalse(AutomatonFactory.create("#").run("#"));
        assertTrue(AutomatonFactory.create("\\#").run("#"));
    }

    @Test
    public void test_escapings_charclasses() {
        assertTrue(AutomatonFactory.create("[^^]").run("x"));
        assertFalse(AutomatonFactory.create("[^^]").run("^"));
        assertTrue(AutomatonFactory.create("[\\^]").run("^"));
        assertTrue(AutomatonFactory.create("[$]").run("$"));
        assertTrue(AutomatonFactory.create("[.]").run("."));
        assertFalse(AutomatonFactory.create("[.]").run("a"));
        assertTrue(AutomatonFactory.create("[()]").run("("));
        assertTrue(AutomatonFactory.create("[()]").run(")"));
        assertFalse(AutomatonFactory.create("[()]").run("["));
        assertTrue(AutomatonFactory.create("[\\]]").run("]"));
        assertTrue(AutomatonFactory.create("[a-e]&[e-h]").run("e"));
        assertTrue(AutomatonFactory.create("[a-e]\\&[e-h]").run("a&h"));
        assertTrue(AutomatonFactory.create("~[a-e]").run("f"));
        assertTrue(AutomatonFactory.create("~[a-e]").run("~a")); // Why? Better do not use '~' to convert ecma patterns.
        assertFalse(AutomatonFactory.create("\\~[a-e]").run("f"));
        assertTrue(AutomatonFactory.create("\\~[a-e]").run("~a"));
        assertTrue(AutomatonFactory.create("[a-e&f-h]").run("&"));
        assertTrue(AutomatonFactory.create("[a-e&f-h]").run("e"));
        assertTrue(AutomatonFactory.create("[a-e&f-h]").run("f"));
        assertTrue(AutomatonFactory.create("[~a-e]").run("~"));
        assertTrue(AutomatonFactory.create("[~a-e]").run("a"));
    }

    @Test
    public void test_escapings_numericalInterval() {
        assertTrue(AutomatonFactory.create("<3-5>").run("3"));
        assertTrue(AutomatonFactory.create("<3-5>").run("4"));
        assertTrue(AutomatonFactory.create("<3-5>").run("5"));
        assertFalse(AutomatonFactory.create("<3-5>").run("6"));
        assertTrue(AutomatonFactory.create("<30-47>").run("41"));
        assertFalse(AutomatonFactory.create("<30-47>").run("48"));
        assertFalse(AutomatonFactory.create("<3-5>").run("<3-5>"));
        assertTrue(AutomatonFactory.create("\\<3-5>").run("<3-5>"));
        assertThrows(IllegalArgumentException.class, () -> AutomatonFactory.create("<3-5\\>").run("<3-5>"));
        assertThrows(IllegalArgumentException.class, () -> AutomatonFactory.create("<3\\-5>").run("<3-5>"));
    }

    @Test
    public void test_backspace() {
        assertTrue(AutomatonFactory.create("a[\b]").run("a\b"));
    }

}
