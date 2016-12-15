package org.netbeans.asciidoc.util;

import java.util.Objects;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import javax.swing.text.StringContent;
import org.junit.Test;

public class DocumentUtilsTest {
    @Test
    public void testGetLineUntilPosWithEmpty() throws Exception {
        testGetLineUntilPos("", 0, "");
    }

    @Test
    public void testGetLineUntilPosWithSingleLine() throws Exception {
        String content = "My Single Line";
        for (int i = 0; i <= content.length(); i++) {
            testGetLineUntilPos(content, i, content.substring(0, i));
        }
    }

    @Test
    public void testGetLineUntilPosWithLongSingleLine() throws Exception {
        String content = generateTestString(2000);
        int pos = 1900;
        testGetLineUntilPos(content, pos, content.substring(0, pos));
    }

    @Test
    public void testGetLineUntilPosWithMultiLine() throws Exception {
        testGetLineUntilPosWithMultiLine("\n");
        testGetLineUntilPosWithMultiLine("\r\n");
        testGetLineUntilPosWithMultiLine("\r");
    }

    private void testGetLineUntilPosWithMultiLine(String lineSeparator) throws Exception {
        testGetLineUntilPosWithMultiLine(lineSeparator, "First Line", "Second Line");
    }

    @Test
    public void testGetLineUntilPosWithLongMultiLine() throws Exception {
        testGetLineUntilPosWithLongMultiLine("\n");
        testGetLineUntilPosWithLongMultiLine("\r\n");
        testGetLineUntilPosWithLongMultiLine("\r");
    }

    private void testGetLineUntilPosWithLongMultiLine(String lineSeparator) throws Exception {
        testGetLineUntilPosWithMultiLine(lineSeparator,
                generateTestString(2100, 'a', 'm'),
                generateTestString(1900, 'n', 'z'));
    }

    private void testGetLineUntilPosWithMultiLine(String lineSeparator, String firstLine, String secondLine) throws Exception {
        String content = firstLine + lineSeparator + secondLine;
        testGetLineUntilPos(content, content.length() - 4, secondLine.substring(0, secondLine.length() - 4));
    }

    private String generateTestString(int length) {
        return generateTestString(length, 'a', 'z');
    }

    private String generateTestString(int length, char startChar, char endChar) {
        StringBuilder result = new StringBuilder();
        char ch = 'a';
        for (int i = 0; i < length; i++) {
            result.append(ch);
            ch = (char)(((ch + 1 - startChar) % (endChar - startChar)) + startChar);
        }
        return result.toString();
    }

    private void testGetLineUntilPos(String content, int pos, String expectedResult) throws BadLocationException {
        String actualResult = DocumentUtils.getLineUntilPos(createDocument(content), pos);
        if (!Objects.equals(actualResult, expectedResult)) {
            throw new AssertionError("Expected result: \"" + expectedResult
                    + "\" but received: \"" + actualResult
                    + "\" at position " + pos + " for document \"" + content + "\"");
        }
    }

    private static Document createDocument(String content) throws BadLocationException {
        StringContent docContent = new StringContent();
        docContent.insertString(0, content);
        return new PlainDocument(docContent);
    }
}
