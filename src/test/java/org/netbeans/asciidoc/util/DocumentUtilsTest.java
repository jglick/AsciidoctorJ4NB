package org.netbeans.asciidoc.util;

import java.util.Objects;
import javax.swing.text.BadLocationException;
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
        String content = TestDocumentUtils.generateTestString(2000);
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
                TestDocumentUtils.generateTestString(2100, 'a', 'm'),
                TestDocumentUtils.generateTestString(1900, 'n', 'z'));
    }

    private void testGetLineUntilPosWithMultiLine(String lineSeparator, String firstLine, String secondLine) throws Exception {
        String content = firstLine + lineSeparator + secondLine;
        testGetLineUntilPos(content, content.length() - 4, secondLine.substring(0, secondLine.length() - 4));
    }

    private void testGetLineUntilPos(String content, int pos, String expectedResult) throws BadLocationException {
        String actualResult = DocumentUtils.getLineUntilPos(TestDocumentUtils.createDocument(content), pos);
        if (!Objects.equals(actualResult, expectedResult)) {
            throw new AssertionError("Expected result: \"" + expectedResult
                    + "\" but received: \"" + actualResult
                    + "\" at position " + pos + " for document \"" + content + "\"");
        }
    }
}
