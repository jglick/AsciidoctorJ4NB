package org.netbeans.asciidoc.editor;

import org.junit.Test;
import org.netbeans.asciidoc.util.TestDocumentUtils;

import static org.junit.Assert.*;

public class DefaultDocumentNewLineInserterTest {
    @Test
    public void testTryGetLineToAddNoResult() {
        testTryGetLineToAdd("|", null);
        testTryGetLineToAdd("a|bcd", null);
        testTryGetLineToAdd("abcd|", null);
        testTryGetLineToAdd(generateTestStringWithCaret(3000, 1501), null);
    }

    @Test
    public void testInsertArabic() {
        testTryGetLineToAdd("5. Line1|", "6. ");
    }

    @Test
    public void testInsertLetter() {
        testTryGetLineToAdd("f. Line1|", "g. ");
    }

    @Test
    public void testInsertLetterCapital() {
        testTryGetLineToAdd("F. Line1|", "G. ");
    }

    @Test
    public void testInsertRoman() {
        testTryGetLineToAdd("viii) Line1|", "ix) ");
    }

    @Test
    public void testInsertRomanCapital() {
        testTryGetLineToAdd("VIII) Line1|", "IX) ");
    }

    @Test
    public void testHyphenList() {
        testTryGetLineToAdd("- Line1|", "- ");
    }

    @Test
    public void testDotList() {
        testTryGetLineToAdd(".. Line1|", ".. ");
    }

    @Test
    public void testStarList() {
        testTryGetLineToAdd("*** Line1|", "*** ");
    }

    private void testTryGetLineToAdd(String content, String expectedResult) {
        int caretIndex = content.indexOf('|');
        if (caretIndex < 0) {
            throw new AssertionError("Content must contain a caret character: |");
        }

        String contentWithoutCaret = content.replace("|", "");
        testTryGetLineToAdd(contentWithoutCaret, caretIndex, expectedResult);
    }

    private void testTryGetLineToAdd(String content, int caretOffset, String expectedResult) {
        DefaultDocumentNewLineInserter inserter = new DefaultDocumentNewLineInserter();
        String result = inserter.tryGetLineToAdd(TestDocumentUtils.createDocument(content), caretOffset);

        if (expectedResult == null) {
            assertEquals("tryGetLineToAdd", null, result);
        }
        else {
            assertEquals("tryGetLineToAdd", "\n" + expectedResult, result);
        }
    }

    private static String generateTestStringWithCaret(int length, int caretIndex) {
        String caretlessDoc = TestDocumentUtils.generateTestString(length);
        StringBuilder result = new StringBuilder(length + 1);
        result.append(caretlessDoc, 0, caretIndex);
        result.append('|');
        result.append(caretlessDoc, caretIndex, caretlessDoc.length());
        return result.toString();
    }
}
