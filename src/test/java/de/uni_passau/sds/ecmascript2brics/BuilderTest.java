package de.uni_passau.sds.ecmascript2brics;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BuilderTest {

    @Test
    public void test_legalPattern_starOfOr() {
        assertDoesNotThrow(() -> assertNotNull(Builder.buildParseTree("(^a$|b)*")));
    }

    @Test
    public void test_illegalPattern_toManyBrackets() {
        assertThrows(RuntimeException.class, () -> assertNotNull(Builder.buildParseTree("(^a$|b))*")));
    }

}
