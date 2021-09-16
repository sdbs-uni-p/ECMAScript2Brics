package de.uni_passau.sds.ecmascript2brics;

import dk.brics.automaton.Automaton;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EcmaToBricsExpressionConverterTest {

    @Test
    public void test_noAnchor_concatenation() {
        assertEquals("@abc@", EcmaToBricsExpressionConverter.convert("abc"));
        assertEquals("@abc@", EcmaToBricsExpressionConverter.convert("a(b)c"));
        assertEquals("@abc@", EcmaToBricsExpressionConverter.convert("(abc)"));
        assertEquals("@abc@", EcmaToBricsExpressionConverter.convert("(((a)((bc))))"));
    }

    @Test
    public void test_noAnchor_disjunction() {
        assertEquals("@(a|b)@", EcmaToBricsExpressionConverter.convert("a|b"));
        assertEquals("@(a|b)@", EcmaToBricsExpressionConverter.convert("(a|b)"));
        assertEquals("@(a|b)@", EcmaToBricsExpressionConverter.convert("((a|b))"));
        assertEquals("@(a|b)@", EcmaToBricsExpressionConverter.convert("(a)|((b))"));
        assertEquals("@(a|b)@", EcmaToBricsExpressionConverter.convert("((((a))|(((b)))))"));
        assertEquals("@(a|b|c)@", EcmaToBricsExpressionConverter.convert("a|b|c"));
        assertEquals("@(a|b|c)@", EcmaToBricsExpressionConverter.convert("a|(b|c)"));
    }

    @Test
    public void test_noAnchor_disjunction_emptyWord() {
        assertEquals("@a?@", EcmaToBricsExpressionConverter.convert("a|"));
        assertEquals("@a?@", EcmaToBricsExpressionConverter.convert("|a"));
        assertEquals("@(a|b)?@", EcmaToBricsExpressionConverter.convert("a|b|"));
        assertEquals("@@", EcmaToBricsExpressionConverter.convert("|"));
        assertEquals("@@", EcmaToBricsExpressionConverter.convert("||"));
        assertEquals("@((a?|b)?)?@", EcmaToBricsExpressionConverter.convert("|a|b||"));
    }

    @Test
    public void test_noAnchor_star() {
        assertEquals("@a*@", EcmaToBricsExpressionConverter.convert("a*"));
        assertEquals("@a*@", EcmaToBricsExpressionConverter.convert("(a)*"));
        assertEquals("@a*@", EcmaToBricsExpressionConverter.convert("(a*)"));
        assertEquals("@a*@", EcmaToBricsExpressionConverter.convert("((a)*)"));
    }

    @Test
    public void test_noAnchor_concAndStar() {
        assertEquals("@a*b*cde@", EcmaToBricsExpressionConverter.convert("a*b*cde"));
        assertEquals("@a*b*cde@", EcmaToBricsExpressionConverter.convert("(a)*b*cde"));
        assertEquals("@a*b*cde@", EcmaToBricsExpressionConverter.convert("(a*)b*c(d(e))"));
        assertEquals("@a*b*cde@", EcmaToBricsExpressionConverter.convert("((a)*b*cde)"));
        assertEquals("@a*b*cde@", EcmaToBricsExpressionConverter.convert("(a*b*cde)"));
    }

    @Test void test_noAnchor_concDisjStar() {
        assertEquals("@(a(b|c)*d*e*f|g)@", EcmaToBricsExpressionConverter.convert("a(b|c)*(d*e*f)|g"));
        assertEquals("@ab(c|d*e(f|g))*@", EcmaToBricsExpressionConverter.convert("ab(c|d*e(f|g))*"));
    }

    @Test
    public void test_noAnchor_emptyWord() {
        assertEquals("@@", EcmaToBricsExpressionConverter.convert("()"));
        assertEquals("@@", EcmaToBricsExpressionConverter.convert("()*"));
        assertEquals("@@", EcmaToBricsExpressionConverter.convert("()*|()"));
        assertEquals("@(a*b|abc)@", EcmaToBricsExpressionConverter.convert("a*b()*|ab()c"));
    }

    @Test
    public void test_simpleAnchorRemoval_concatenation() {
        assertEquals("abc", EcmaToBricsExpressionConverter.convert("^abc$"));
        assertEquals("abc", EcmaToBricsExpressionConverter.convert("^a(bc$)"));
        assertEquals("abc", EcmaToBricsExpressionConverter.convert("(^)abc$"));
    }

    @Test
    public void test_simpleAnchorRemoval_disjunction() {
        assertEquals("a|b", EcmaToBricsExpressionConverter.convert("^(a|b)$"));
        assertEquals("a|b", EcmaToBricsExpressionConverter.convert("(^(a|b)$)"));
        assertEquals("a|b", EcmaToBricsExpressionConverter.convert("^((a|(b))($))"));
        assertEquals("a|b|c", EcmaToBricsExpressionConverter.convert("^(a|b|c)$"));
        assertEquals("a|b|c", EcmaToBricsExpressionConverter.convert("^(a|(b|c))$"));
    }

    @Test
    public void test_simpleAnchorRemoval_star() {
        assertEquals("a*", EcmaToBricsExpressionConverter.convert("^a*$"));
        assertEquals("a*", EcmaToBricsExpressionConverter.convert("^(a*)$"));
        assertEquals("a*", EcmaToBricsExpressionConverter.convert("^(a)*$"));
        assertEquals("a*", EcmaToBricsExpressionConverter.convert("(^a*$)"));
        assertEquals("a*", EcmaToBricsExpressionConverter.convert("((^))(a*($))"));
    }

    @Test
    public void test_simpleAnchorRemoval_concDisjStar() {
        assertEquals("a(b|c)*d*e*f|g", EcmaToBricsExpressionConverter.convert("^(a(b|c)*(d*e*f)|g)$"));
        assertEquals("ab(c|d*e(f|g))*", EcmaToBricsExpressionConverter.convert("^ab(c|d*e(f|g))*$"));
    }

    @Test
    public void test_simpleAnchorRemoval_emptyWord() {
        assertEquals("()", EcmaToBricsExpressionConverter.convert("^()$"));
        assertEquals("()", EcmaToBricsExpressionConverter.convert("^()*$"));
        assertEquals("()", EcmaToBricsExpressionConverter.convert("^(()*|())$"));
        assertEquals("a*b|abc", EcmaToBricsExpressionConverter.convert("^(a*b()*|ab()c)$"));
    }

    @Test
    public void test_star_anchorInside() {
        assertEquals("a|@@", EcmaToBricsExpressionConverter.convert("(^a$)*"));
    }

    @Test
    public void test_disjunctionInStar_anchorOnOneSide() {
        assertEquals("a|@b*@", EcmaToBricsExpressionConverter.convert("(^a$|b)*"));
    }

    @Test
    public void test_disjunction_anchorOnBothSides() {
        assertEquals("@b|a@", EcmaToBricsExpressionConverter.convert("^a|b$"));
    }

    @Test
    public void test_quantifier_multipleDigits() {
        assertEquals("a{10,}", EcmaToBricsExpressionConverter.convert("^a{10,}$"));
    }

    @Test
    public void test_quantifier_illegalRange() {
        assertThrows(ECMAScriptRangeException.class, () -> EcmaToBricsExpressionConverter.convert("^324{455,398}"));
        assertThrows(ECMAScriptRangeException.class, () -> EcmaToBricsExpressionConverter.convert("^3h4{1000,999}"));
    }

    @Test
    public void test_allOperations_simple() {
        assertEquals("a+(((b|c)*)+){3,6}c*|d+e?a(ab(c|d)){2}",
                EcmaToBricsExpressionConverter.convert("^(a+(((b|(c))*)+){3,6}c{0,}|d{1,}e{0,1}(a{1,1}(ab(c|d)){2,2}))$"));
    }

    @Test
    public void test_disjunctionInStar_anchorOnBothSides() {
        assertEquals("ab|@b|(a|@)@", EcmaToBricsExpressionConverter.convert("(^a|b$)*"));
    }

    @Test
    public void test_disjunctionInQuestionMark_anchorOnBothSides() {
        assertEquals("@b|(a|@)@", EcmaToBricsExpressionConverter.convert("(^a|b$)?"));
    }

    @Test
    public void test_disjunctionInStar_anchorOnBothSides_otherSymbols() {
        assertEquals("ß\t|@\t|(ß|@)@", EcmaToBricsExpressionConverter.convert("(^ß|\t$)*"));
    }

    @Test
    public void test_disjunctionInQuestionMark_anchorOnBothSides_otherSymbols() {
        assertEquals("@\"|(!|@)@", EcmaToBricsExpressionConverter.convert("(^!|\"$)?"));
    }

    @Test
    public void test_anyCharacter_simple() {
        assertEquals(".", EcmaToBricsExpressionConverter.convert("^.$"));
        assertEquals(".", EcmaToBricsExpressionConverter.convert("^(.)$"));
        assertEquals("@.@", EcmaToBricsExpressionConverter.convert("."));
        assertEquals("@.@", EcmaToBricsExpressionConverter.convert("(.)"));
    }

    @Test
    public void test_anyCharacter_disjunction() {
        assertEquals(".|ab", EcmaToBricsExpressionConverter.convert("^.$|^ab$"));
        assertEquals("@ab|.@", EcmaToBricsExpressionConverter.convert("^.|ab$"));
    }

    @Test
    public void test_characterClass_simple() {
        assertEquals("[abc]", EcmaToBricsExpressionConverter.convert("^[abc]$"));
        assertEquals("[^ab-c]", EcmaToBricsExpressionConverter.convert("^[^ab-c]$"));
        assertEquals("@[ab-c]@", EcmaToBricsExpressionConverter.convert("[ab-c]"));
        assertEquals("@[^abc]@", EcmaToBricsExpressionConverter.convert("[^abc]"));
    }

    @Test
    public void test_characterClass_quantifier() {
        assertEquals("[abc]*", EcmaToBricsExpressionConverter.convert("^[abc]*$"));
        assertEquals("[^a-bc]+", EcmaToBricsExpressionConverter.convert("^[^a-bc]+$"));
        assertEquals("@[a-bc]{2,3}@", EcmaToBricsExpressionConverter.convert("[a-bc]{2,3}"));
        assertEquals("@[^abc]+@", EcmaToBricsExpressionConverter.convert("[^abc]{1,}"));
    }

    @Test
    public void test_characterClass_withNumbers() {
        assertEquals("[0-9]*", EcmaToBricsExpressionConverter.convert("^[0-9]{0,}$"));
        assertEquals("[0-25-9]+", EcmaToBricsExpressionConverter.convert("^[0-25-9]{1,}$"));
        assertEquals("[^0-25]?", EcmaToBricsExpressionConverter.convert("^[^0-25]{0,1}$"));
        assertEquals("[0123]{3}", EcmaToBricsExpressionConverter.convert("^[0123]{3,3}$"));
        assertEquals("[^A-Zabc123]{3,6}", EcmaToBricsExpressionConverter.convert("^[^A-Zabc123]{3,6}$"));
    }

    @Test
    public void test_characterClass_illegalRange() {
        assertThrows(ECMAScriptRangeException.class, () -> EcmaToBricsExpressionConverter.convert("[6-23]"));
        assertThrows(ECMAScriptRangeException.class, () -> EcmaToBricsExpressionConverter.convert("[a-A]"));
    }

    @Test
    public void test_anchorInside() {
        assertEquals("a@", EcmaToBricsExpressionConverter.convert("^^a"));
        assertEquals("@a|@a", EcmaToBricsExpressionConverter.convert("a$$"));
        assertEquals("#", EcmaToBricsExpressionConverter.convert("a^"));
        assertEquals("#", EcmaToBricsExpressionConverter.convert("$a"));
    }

    @Test
    public void test_characterClass_escapings() {
        assertEquals("@[\\\\]@", EcmaToBricsExpressionConverter.convert("[\\\\]"));
        assertEquals("@[^\\\\]@", EcmaToBricsExpressionConverter.convert("[^\\\\]"));
        assertEquals("@[\\^\\\\]@", EcmaToBricsExpressionConverter.convert("[\\^\\\\]"));
        assertEquals("@[\\^\\\\]]@", EcmaToBricsExpressionConverter.convert("[\\^\\\\]]"));
        assertEquals("@[\\-]@", EcmaToBricsExpressionConverter.convert("[-]"));
        assertEquals("@[\\-\\-]@", EcmaToBricsExpressionConverter.convert("[--]"));
        assertEquals("@[\\--\\-]@", EcmaToBricsExpressionConverter.convert("[---]"));
        assertEquals("@[\\--\\-\\-]@", EcmaToBricsExpressionConverter.convert("[----]"));
        assertEquals("@[+\\-]@", EcmaToBricsExpressionConverter.convert("[+-]"));
        assertEquals("@[+-\\-]@", EcmaToBricsExpressionConverter.convert("[+--]"));
        assertEquals("@[0-9A-Za-z_]+[\\\\]([0-9A-Za-z_]+\\.)*[0-9A-Za-z_]+\\.[0-9A-Za-z_]+@", EcmaToBricsExpressionConverter.convert("\\w+[\\\\](\\w+\\.)*\\w+\\.\\w+"));
        assertEquals("@\\\\@", EcmaToBricsExpressionConverter.convert("\\\\"));
        assertEquals("@\\\\d@", EcmaToBricsExpressionConverter.convert("\\\\d"));
        assertEquals("@\\\\d*@", EcmaToBricsExpressionConverter.convert("\\\\d*"));
    }

    @Test
    public void test_characterClass_escapeSequence() {
        assertEquals("@[0-9]@", EcmaToBricsExpressionConverter.convert("\\d"));
        assertEquals("@[0-9]@", EcmaToBricsExpressionConverter.convert("[\\d]"));
        assertEquals("@[^0-9]@", EcmaToBricsExpressionConverter.convert("[\\D]"));
        assertEquals("@[A-Z0-9]@", EcmaToBricsExpressionConverter.convert("[A-Z\\d]"));
        assertEquals("@[0-9a-z]@", EcmaToBricsExpressionConverter.convert("[\\da-z]"));
        assertEquals("@[^A-Z0-9]@", EcmaToBricsExpressionConverter.convert("[^A-Z\\d]"));
        assertEquals("@[^0-9a-z]@", EcmaToBricsExpressionConverter.convert("[^\\da-z]"));
        assertEquals("@([A-Z]|[^0-9])@", EcmaToBricsExpressionConverter.convert("[A-Z\\D]"));
        assertEquals("@([a-z]|[^0-9])@", EcmaToBricsExpressionConverter.convert("[\\Da-z]"));
        assertEquals("@([abcde]|[^0-9A-Za-z_])@", EcmaToBricsExpressionConverter.convert("[abc\\Wde]"));
        assertEquals("@([a-z]|[^0-9A-Za-z_])@", EcmaToBricsExpressionConverter.convert("[\\Wa-z]"));
        assertEquals("@([a0-9bcde]|[^0-9A-Za-z_])@", EcmaToBricsExpressionConverter.convert("[a\\dbc\\Wde]"));
        assertEquals("@([a-z0-9]|[^0-9A-Za-z_])@", EcmaToBricsExpressionConverter.convert("[\\Wa-z\\d]"));
        assertEquals("@([abcde]|[^0-9]|[^0-9A-Za-z_])@", EcmaToBricsExpressionConverter.convert("[a\\Dbc\\Wde]"));
        assertEquals("@([a-z]|[^0-9A-Za-z_]|[^0-9])@", EcmaToBricsExpressionConverter.convert("[\\Wa-z\\D]"));
    }

    @Test
    public void test_unicode_character() {
        assertEquals("@Y@", EcmaToBricsExpressionConverter.convert("\\u0059"));
        assertEquals("@Y@", EcmaToBricsExpressionConverter.convert("\u0059"));
        assertEquals("@Y@", EcmaToBricsExpressionConverter.convert("\\Y"));
        assertEquals("@Ä@", EcmaToBricsExpressionConverter.convert("\\u00c4"));
        assertEquals("@Ä@", EcmaToBricsExpressionConverter.convert("\\u00C4"));
        assertEquals("@Ä@", EcmaToBricsExpressionConverter.convert("\\Ä"));
        assertEquals("@Ä@", EcmaToBricsExpressionConverter.convert("\u00c4"));
        assertEquals("@Ä@", EcmaToBricsExpressionConverter.convert("\u00C4"));
    }

    @Test
    public void test_bracketsInCharclass() {
        assertEquals("[a-e[f-h]]", EcmaToBricsExpressionConverter.convert("^[a-e[f-h]]$"));
    }

    @Test
    public void test_spaceInClass() {
        assertEquals("[0-9A-Za-z_ /\\-]+\\.[0-9a-zA-Z]+", EcmaToBricsExpressionConverter.convert("^[\\w /-]+\\.[0-9a-zA-Z]+$"));
    }

    @Test
    public void test_spaceInClass_automaton() {
        String bricsRegex = EcmaToBricsExpressionConverter.convert("^[\\w /-]+\\.[0-9a-zA-Z]+$");
        Automaton automaton = AutomatonFactory.create(bricsRegex);
        assertTrue(automaton.run(" .0"));
    }

    @Test
    public void test_null() {
        String bricsRegex = EcmaToBricsExpressionConverter.convert("^\\0$");
        Automaton automaton = AutomatonFactory.create(bricsRegex);
        assertTrue(automaton.run("\u0000"));
        assertFalse(automaton.run("0"));
    }

    @Test
    public void test_nullRange() {
        String bricsRegex = EcmaToBricsExpressionConverter.convert("^[^\\0]$");
        Automaton automaton = AutomatonFactory.create(bricsRegex);
        assertTrue(automaton.run("a"));
        assertTrue(automaton.run("0"));
    }

    @Test
    public void test_nonWhiteSpace_negated() {
        Assertions.assertThrows(ECMAScriptNotSupportedException.class, () -> {
            String bricsRegex = EcmaToBricsExpressionConverter.convert("^[^0-9\\S]$"); // it is wrong to split this into [^0-9]|[^\S]
            Automaton automaton = AutomatonFactory.create(bricsRegex);
            assertTrue(automaton.run(" "));
            assertTrue(automaton.run("\n"));
            assertFalse(automaton.run("1"));
            assertFalse(automaton.run("x"));
        });
    }

    @Test
    public void test_unescapedWhitespace() {
        String bricsRegex = EcmaToBricsExpressionConverter.convert("^\n$");
        Automaton automaton = AutomatonFactory.create(bricsRegex);
        assertTrue(automaton.run("\n"));
    }

    @Test
    public void test_parLineSeparator() {
        String bricsRegex = EcmaToBricsExpressionConverter.convert("^a\\u2028b\\u2029c$");
        Automaton automaton = AutomatonFactory.create(bricsRegex);
        assertTrue(automaton.run("a\u2028b\u2029c"));
    }

    @Test
    public void test_parLineSeparator_unescaped() {
        String bricsRegex = EcmaToBricsExpressionConverter.convert("^a\u2028b\u2029c$");
        Automaton automaton = AutomatonFactory.create(bricsRegex);
        assertTrue(automaton.run("a\u2028b\u2029c"));
    }

    @Test
    public void test_gnuRegexpWhitespace() {
        // Tests the whitespace symbols supported by gnu.regexp.
        String bricsRegex = EcmaToBricsExpressionConverter.convert("^[ \\n\\t\\r]+$");
        Automaton automaton = AutomatonFactory.create(bricsRegex);
        assertTrue(automaton.run("\n"));
        assertTrue(automaton.run("\t"));
        assertTrue(automaton.run("\r"));
    }

    @Test
    public void test_gnuRegexpWhitespace_unescaped() {
        // Tests the whitespace symbols supported by gnu.regexp.
        String bricsRegex = EcmaToBricsExpressionConverter.convert("^[ \n\t\r]+$");
        Automaton automaton = AutomatonFactory.create(bricsRegex);
        assertTrue(automaton.run("      "));
        assertTrue(automaton.run("\n"));
        assertTrue(automaton.run("\t"));
        assertTrue(automaton.run("\r"));
    }

    @Test
    public void test_parLineSeparator_inClass() {
        String bricsRegex = EcmaToBricsExpressionConverter.convert("^[\\u2028\\u2029]$");
        Automaton automaton = AutomatonFactory.create(bricsRegex);
        assertTrue(automaton.run("\u2028"));
        assertTrue(automaton.run("\u2029"));
        assertFalse(automaton.run("\n"));
        assertFalse(automaton.run("\\u2029"));
    }

    @Test
    public void test_parLineSeparator_inClass_unescaped() {
        String bricsRegex = EcmaToBricsExpressionConverter.convert("^[\u2028\u2029]$");
        Automaton automaton = AutomatonFactory.create(bricsRegex);
        assertTrue(automaton.run("\u2028"));
        assertTrue(automaton.run("\u2029"));
        assertFalse(automaton.run("\n"));
        assertFalse(automaton.run("\\u2029"));
    }

    @Test
    public void test_whitespace() {
        String bricsRegex = EcmaToBricsExpressionConverter.convert("^[ \\n\\t\\r\\f\\v]+$");
        Automaton automaton = AutomatonFactory.create(bricsRegex);
        assertTrue(automaton.run(" "));
        assertTrue(automaton.run("\n"));
        assertTrue(automaton.run("\t\t\t"));
        assertTrue(automaton.run("\f\t\n"));
        assertTrue(automaton.run("\u000b")); // '\v', not known to Java
        assertFalse(automaton.run("ntr"));
    }

    @Test
    public void test_formFeed_outsideRange() {
        // "\f" is not originally supported by gnu.regexp.
        // This had to be added, so we need to test this explicitly.
        String bricsRegex = EcmaToBricsExpressionConverter.convert("^\\f+$");
        Automaton automaton = AutomatonFactory.create(bricsRegex);
        assertTrue(automaton.run("\f"));
        assertFalse(automaton.run("\\f\\f\\f"));
    }

    @Test
    public void test_verticalTab_outsideRange() {
        // "\v" is not originally supported by gnu.regexp.
        // This had to be added, so we need to test this explicitly.
        String bricsRegex = EcmaToBricsExpressionConverter.convert("^\\v+$");
        Automaton automaton = AutomatonFactory.create(bricsRegex);
        assertTrue(automaton.run("\u000b"));
        assertFalse(automaton.run("\\v\\v"));
    }

    @Test
    public void test_noBlankOrNewline() {
        String bricsRegex = EcmaToBricsExpressionConverter.convert("^[^ \\n]+$");
        Automaton automaton = AutomatonFactory.create(bricsRegex);
        assertFalse(automaton.run(" "));
        assertFalse(automaton.run("\n"));
        assertTrue(automaton.run("\\n"));
    }

    @Test
    public void test_hexConstant() {
        String bricsRegex = EcmaToBricsExpressionConverter.convert("^\\x41*$");
        Automaton automaton = AutomatonFactory.create(bricsRegex);
        assertTrue(automaton.run("AAA"));
        assertFalse(automaton.run("x41111"));
    }

    @Test
    public void test_hexConstant_inRange() {
        String bricsRegex = EcmaToBricsExpressionConverter.convert("^[\\x41-\\x43]$");
        Automaton automaton = AutomatonFactory.create(bricsRegex);
        assertTrue(automaton.run("A"));
        assertTrue(automaton.run("B"));
        assertTrue(automaton.run("C"));
    }

    @Test
    public void test_wordBoundary() {
        Assertions.assertThrows(ECMAScriptNotSupportedException.class, () -> {
            String bricsRegex = EcmaToBricsExpressionConverter.convert("\\bis\\b");
            Automaton automaton = AutomatonFactory.create(bricsRegex);
            assertTrue(automaton.run("This island is beautiful"));
            assertFalse(automaton.run("This island isn't beautiful"));
        });
    }

    @Test
    public void test_nonWordBoundary() {
        Assertions.assertThrows(ECMAScriptNotSupportedException.class, () -> {
            String bricsRegex = EcmaToBricsExpressionConverter.convert("\\Bis");
            Automaton automaton = AutomatonFactory.create(bricsRegex);
            assertTrue(automaton.run("This island is beautiful"));
            assertFalse(automaton.run("Is the island beautiful?"));
        });
    }

    @Test
    public void test_backspace_inClass_escaped() {
        Assertions.assertThrows(ECMAScriptNotSupportedException.class, () -> {
            String bricsRegex = EcmaToBricsExpressionConverter.convert("\\babc[\\b]");
            Automaton automaton = AutomatonFactory.create(bricsRegex);
            assertTrue(automaton.run("abc\b"));
            assertTrue(automaton.run("x abc\b"));
            assertFalse(automaton.run("xabc\b"));
            assertFalse(automaton.run("abc"));
        });
    }

    @Test
    public void test_backspace_inClass_escaped_noBoundary() {
        String bricsRegex = EcmaToBricsExpressionConverter.convert("abc[\\b]");
        Automaton automaton = AutomatonFactory.create(bricsRegex);
        assertTrue(automaton.run("abc\b"));
        assertTrue(automaton.run("x abc\b"));
        assertTrue(automaton.run("xabc\b"));
        assertFalse(automaton.run("abc"));
    }

    @Test
    public void test_backspace_notInClass() {
        String bricsRegex = EcmaToBricsExpressionConverter.convert("\babc\b");
        Automaton automaton = AutomatonFactory.create(bricsRegex);
        assertTrue(automaton.run("\babc\b"));
        assertTrue(automaton.run("x \babc\b"));
        assertTrue(automaton.run("x\babc\b"));
        assertFalse(automaton.run("abc"));
    }

    @Test
    public void test_backspace_inClass_unescaped() {
        String bricsRegex = EcmaToBricsExpressionConverter.convert("\babc[\b]");
        Automaton automaton = AutomatonFactory.create(bricsRegex);
        assertTrue(automaton.run("\babc\b"));
        assertTrue(automaton.run("x \babc\b"));
        assertTrue(automaton.run("x\babc\b"));
        assertFalse(automaton.run("abc"));
    }

    @Test
    public void test_controlCharacter() {
        String bricsRegex = EcmaToBricsExpressionConverter.convert("^\\ca$"); // CTRL+a
        Automaton automaton = AutomatonFactory.create(bricsRegex);
        assertTrue(automaton.run("\u0001"));
        assertFalse(automaton.run("ca"));
    }

    @Test
    public void test_controlCharacter_inRange() {
        String bricsRegex = EcmaToBricsExpressionConverter.convert("^[\\ca-\\cC]$");
        Automaton automaton = AutomatonFactory.create(bricsRegex);
        assertTrue(automaton.run("\u0001"));
        assertTrue(automaton.run("\u0002"));
        assertTrue(automaton.run("\u0003"));
        assertFalse(automaton.run("\u0004"));
    }

    @Test
    public void test_octalConstant() {
        String bricsRegex = EcmaToBricsExpressionConverter.convert("^\\101[0-5]$"); // A0, A1, ...
        Automaton automaton = AutomatonFactory.create(bricsRegex);
        assertTrue(automaton.run("A0"));
    }

    @Test
    public void test_octalConstantRange() {
        String bricsRegex = EcmaToBricsExpressionConverter.convert("^[\\101-\\103]$");
        Automaton automaton = AutomatonFactory.create(bricsRegex);
        assertTrue(automaton.run("A"));
        assertTrue(automaton.run("B"));
    }

}
