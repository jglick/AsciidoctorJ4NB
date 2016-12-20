package org.netbeans.asciidoc.editor;

import org.junit.Test;

import static org.junit.Assert.*;

public class NewLineInsertersTest {
    @Test
    public void testNestableListElementInserter1Nothing() {
        testIndentableLineInserter(NewLineInserters.nestableListElementInserter1(), "", null);
    }

    @Test
    public void testNestableListElementInserter1EmptyLine() {
        testIndentableLineInserter(NewLineInserters.nestableListElementInserter1(), ". ", ". ");
    }

    @Test
    public void testNestableListElementInserter1SimpleLine() {
        testIndentableLineInserter(NewLineInserters.nestableListElementInserter1(), ". Line1", ". ");
    }

    @Test
    public void testNestableListElementInserter1NestedLine() {
        testIndentableLineInserter(NewLineInserters.nestableListElementInserter1(), ".. Line1", ".. ");
    }

    @Test
    public void testNestableListElementInserter1WrongLine() {
        testIndentableLineInserter(NewLineInserters.nestableListElementInserter1(), "+ Line1", null);
    }

    @Test
    public void testNestableListElementInserter2Nothing() {
        testIndentableLineInserter(NewLineInserters.nestableListElementInserter2(), "", null);
    }

    @Test
    public void testNestableListElementInserter2EmptyLine() {
        testIndentableLineInserter(NewLineInserters.nestableListElementInserter2(), "* ", "* ");
    }

    @Test
    public void testNestableListElementInserter2SimpleLine() {
        testIndentableLineInserter(NewLineInserters.nestableListElementInserter2(), "* Line1", "* ");
    }

    @Test
    public void testNestableListElementInserter2NestedLine() {
        testIndentableLineInserter(NewLineInserters.nestableListElementInserter2(), "** Line1", "** ");
    }

    @Test
    public void testNestableListElementInserter2WrongLine() {
        testIndentableLineInserter(NewLineInserters.nestableListElementInserter2(), "+ Line1", null);
    }

    @Test
    public void testUnorderedListElementInserterNothing() {
        testIndentableLineInserter(NewLineInserters.unorderedListElementInserter(), "", null);
    }

    @Test
    public void testUnorderedListElementInserterEmptyLine() {
        testIndentableLineInserter(NewLineInserters.unorderedListElementInserter(), "- ", "- ");
    }

    @Test
    public void testUnorderedListElementInserterSimpleLine() {
        testIndentableLineInserter(NewLineInserters.unorderedListElementInserter(), "- Line1", "- ");
    }

    @Test
    public void testUnorderedListElementInserterWrongLine() {
        testIndentableLineInserter(NewLineInserters.unorderedListElementInserter(), "+ Line1", null);
    }

    @Test
    public void testTryInsertArabicListLineNothing() {
        testIndentableLineInserter(NewLineInserters::tryInsertArabicListLine, "", null);
    }

    @Test
    public void testTryInsertArabicListLineAfterFirst() {
        testIndentableLineInserter(NewLineInserters::tryInsertArabicListLine, "1. Line1", "2. ");
    }

    @Test
    public void testTryInsertArabicListLineAfterLater() {
        testIndentableLineInserter(NewLineInserters::tryInsertArabicListLine, "5. Line1", "6. ");
    }

    @Test
    public void testTryInsertArabicListLineAfterMultiDigit() {
        testIndentableLineInserter(NewLineInserters::tryInsertArabicListLine, "99. Line1", "100. ");
    }

    @Test
    public void testTryInsertArabicListLineWrongLine() {
        testIndentableLineInserter(NewLineInserters::tryInsertArabicListLine, "- Line1", null);
    }

    @Test
    public void testTryInsertLetterListLineNothing() {
        testIndentableLineInserter(NewLineInserters::tryInsertLetterListLine, "", null);
    }

    @Test
    public void testTryInsertLetterListLineAfterFirst() {
        testIndentableLineInserter(NewLineInserters::tryInsertLetterListLine, "a. Line1", "b. ");
    }

    @Test
    public void testTryInsertLetterListLineAfterLater() {
        testIndentableLineInserter(NewLineInserters::tryInsertLetterListLine, "y. Line1", "z. ");
    }

    @Test
    public void testTryInsertLetterListLineLastLetter() {
        testIndentableLineInserter(NewLineInserters::tryInsertLetterListLine, "z. Line1", null);
    }

    @Test
    public void testTryInsertLetterListLineAfterFirstCapital() {
        testIndentableLineInserter(NewLineInserters::tryInsertLetterListLine, "A. Line1", "B. ");
    }

    @Test
    public void testTryInsertLetterListLineAfterLaterCapital() {
        testIndentableLineInserter(NewLineInserters::tryInsertLetterListLine, "Y. Line1", "Z. ");
    }

    @Test
    public void testTryInsertLetterListLineLastLetterCapital() {
        testIndentableLineInserter(NewLineInserters::tryInsertLetterListLine, "Z. Line1", null);
    }

    @Test
    public void testTryInsertLetterListLineWrongLine() {
        testIndentableLineInserter(NewLineInserters::tryInsertLetterListLine, "- Line1", null);
    }

    @Test
    public void testTryInsertRomanListLineNothing() {
        testIndentableLineInserter(NewLineInserters::tryInsertRomanListLine, "", null);
    }

    @Test
    public void testTryInsertRomanListLineAfterFirst() {
        testIndentableLineInserter(NewLineInserters::tryInsertRomanListLine, "i) Line1", "ii) ");
    }

    @Test
    public void testTryInsertRomanListLineAfterLater() {
        testIndentableLineInserter(NewLineInserters::tryInsertRomanListLine, "ix) Line1", "x) ");
    }

    @Test
    public void testTryInsertRomanListLineLast() {
        testIndentableLineInserter(NewLineInserters::tryInsertRomanListLine, "mmmcmxcix) Line1", null);
    }

    @Test
    public void testTryInsertRomanListLineAfterFirstCapital() {
        testIndentableLineInserter(NewLineInserters::tryInsertRomanListLine, "I) Line1", "II) ");
    }

    @Test
    public void testTryInsertRomanListLineAfterLaterCapital() {
        testIndentableLineInserter(NewLineInserters::tryInsertRomanListLine, "IX) Line1", "X) ");
    }

    @Test
    public void testTryInsertRomanListLineLastCapital() {
        testIndentableLineInserter(NewLineInserters::tryInsertRomanListLine, "MMMCMXCIX) Line1", null);
    }

    @Test
    public void testTryInsertRomanListLineWrongLine() {
        testIndentableLineInserter(NewLineInserters::tryInsertRomanListLine, "- Line1", null);
    }

    @Test
    public void testIndentableLineInsertersEmptyPrevLine() {
        testIndentableLineInsertersEmptyPrevLine("");
        testIndentableLineInsertersEmptyPrevLine(" ");
        testIndentableLineInsertersEmptyPrevLine("  ");
        testIndentableLineInsertersEmptyPrevLine("\t");
        testIndentableLineInsertersEmptyPrevLine(" \t");
    }

    private void testIndentableLineInsertersEmptyPrevLine(String prefix) {
        String input = prefix;
        testIndentableLineInserters(input, null,
                mockUnexpectedIndentableInserter());
    }

    @Test
    public void testIndentableLineInsertersSimplePrevLine() {
        testIndentableLineInsertersSimplePrevLine("");
        testIndentableLineInsertersSimplePrevLine(" ");
        testIndentableLineInsertersSimplePrevLine("  ");
        testIndentableLineInsertersSimplePrevLine("\t");
        testIndentableLineInsertersSimplePrevLine(" \t");
    }

    private void testIndentableLineInsertersSimplePrevLine(String prefix) {
        String input = prefix + "Line1";
        testIndentableLineInserters(input, "TestResult",
                mockIndentableInserter(input, prefix.length(), null),
                mockIndentableInserter(input, prefix.length(), "TestResult"));
    }

    @Test
    public void testIndentableLineInsertersStopCallForFirstResult() {
        String input = "Line1";
        testIndentableLineInserters(input, "TestResult",
                mockIndentableInserter(input, 0, null),
                mockIndentableInserter(input, 0, "TestResult"),
                mockUnexpectedIndentableInserter());
    }

    @Test
    public void testIndentableLineInsertersCorrectOrder() {
        String input = "Line1";
        testIndentableLineInserters(input, "TestResult1",
                mockIndentableInserter(input, 0, "TestResult1"),
                mockIndentableInserter(input, 0, "TestResult2"));
    }

    @Test
    public void testIndentableLineInsertersZeroInserter() {
        testIndentableLineInserters("Line1", null);
    }

    @Test
    public void testIndentableLineInsertersSingleInserter() {
        String input = "Line1";
        testIndentableLineInserters(input, "TestResult1",
                mockIndentableInserter(input, 0, "TestResult1"));
    }

    @Test
    public void testIndentableLineInsertersNullResult() {
        String input = "Line1";
        testIndentableLineInserters(input, null,
                mockIndentableInserter(input, 0, null),
                mockIndentableInserter(input, 0, null));
    }

    private static IndentableNewLineInserter mockIndentableInserter(
            String expectedPrevLine,
            int expectedNonSpaceIndex,
            String result) {
        return (String prevLine, int nonSpaceIndex) -> {
            assertEquals("prevLine", expectedPrevLine, prevLine);
            assertEquals("nonSpaceIndex", expectedNonSpaceIndex, nonSpaceIndex);
            return result;
        };
    }

    private static IndentableNewLineInserter mockUnexpectedIndentableInserter() {
        return (String prevLine, int nonSpaceIndex) -> {
            throw new AssertionError("Unexpected call to IndentableNewLineInserter");
        };
    }

    private void testIndentableLineInserters(
            String prevLine,
            String expectedResult,
            IndentableNewLineInserter... inserters) {

        NewLineInserter inserter = NewLineInserters.indentableLineInserters(inserters);
        String result = inserter.tryGetLineToAdd(prevLine);
        assertEquals("tryGetLineToAdd", expectedResult, result);
    }

    private static void testIndentableLineInserter(
            IndentableNewLineInserter inserter,
            String prevLine,
            String expectedResult) {
        testIndentableLineInserterWithPrefix("", inserter, prevLine, expectedResult);
        testIndentableLineInserterWithPrefix(" ", inserter, prevLine, expectedResult);
        testIndentableLineInserterWithPrefix("  ", inserter, prevLine, expectedResult);
        testIndentableLineInserterWithPrefix("\t", inserter, prevLine, expectedResult);
    }

    private static void testIndentableLineInserterWithPrefix(
            String prefix,
            IndentableNewLineInserter inserter,
            String prevLine,
            String expectedResult) {

        String result = inserter.tryGetLineToAdd(prefix + prevLine, prefix.length());
        if (expectedResult == null) {
            assertNull("tryGetLineToAdd", result);
        }
        else {
            assertEquals("tryGetLineToAdd", "\n" + prefix + expectedResult, result);
        }
    }
}
