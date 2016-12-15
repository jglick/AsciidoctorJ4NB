package org.netbeans.asciidoc.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class RomanNumeralsTest {
    @Test
    public void testConvertAndParse() {
        for (int i = 1; i < 3999; i++) {
            try {
                testConvertAndParse(i);
            } catch (Throwable ex) {
                throw new AssertionError("Test failed for " + i, ex);
            }
        }
    }

    private void testConvertAndParse(int number) {
        testConvertAndParse(number, true);
        testConvertAndParse(number, false);
    }

    private void testConvertAndParse(int number, boolean upperCase) {
        String numberStr = RomanNumerals.tryConvertToRoman(number, upperCase);
        assertNotNull("converted", numberStr);

        int reparsedNumber = RomanNumerals.parseRoman(numberStr, -1);
        assertEquals("reparsedNumber", number, reparsedNumber);
    }
}
