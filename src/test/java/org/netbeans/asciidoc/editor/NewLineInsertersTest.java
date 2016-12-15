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
