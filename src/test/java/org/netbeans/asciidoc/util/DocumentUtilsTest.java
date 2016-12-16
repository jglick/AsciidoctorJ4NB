package org.netbeans.asciidoc.util;

import java.util.Objects;
import java.util.function.Predicate;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
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
        testGetLineUntilPos(content, pos, expectedResult, null);
    }

    private void testGetLineUntilPos(
            String content,
            int pos,
            String expectedResult,
            Predicate<? super String> lineAcceptor) throws BadLocationException {
        Document document = TestDocumentUtils.createDocument(content);
        String actualResult = lineAcceptor != null
                ? DocumentUtils.getLineUntilPos(document, pos, lineAcceptor)
                : DocumentUtils.getLineUntilPos(document, pos);
        if (!Objects.equals(actualResult, expectedResult)) {
            throw new AssertionError("Expected result: \"" + expectedResult
                    + "\" but received: \"" + actualResult
                    + "\" at position " + pos + " for document \"" + content + "\"");
        }
    }

    @Test
    public void testGetLineUntilPosMiddleResult() throws Exception {
        Predicate<? super String> lineAcceptor = (candidate) -> candidate.startsWith("b_");
        testGetLineUntilPosWithFilter(lineAcceptor, "b_line2", "a_line", "b_line1", "b_line2", "c_line", "d_line");
    }

    @Test
    public void testGetLineUntilPosEmptyLine() throws Exception {
        Predicate<? super String> lineAcceptor = String::isEmpty;
        testGetLineUntilPosWithFilter(lineAcceptor, "", "a_line", "", "b_line", "c_line");
    }

    private void testGetLineUntilPosWithFilter(
            Predicate<? super String> lineAcceptor,
            String expectedResult,
            String... lines) throws Exception {
        for (String lineSeparator: new String[]{"\n", "\r", "\r\n"}) {
            try {
                testGetLineUntilPosWithFilterAndLineEnding(lineAcceptor, expectedResult, lineSeparator, lines);
            } catch (Throwable ex) {
                throw new AssertionError("Error for line separator: " + translateLineSeparator(lineSeparator), ex);
            }
        }
    }

    private static String translateLineSeparator(String lineSeparator) {
        switch (lineSeparator) {
            case "\r":
                return "CR";
            case "\n":
                return "LF";
            case "\r\n":
                return "CRLF";
            default:
                return "UNKNOWN";
        }
    }

    private void testGetLineUntilPosWithFilterAndLineEnding(
            Predicate<? super String> lineAcceptor,
            String expectedResult,
            String lineSeparator,
            String... lines) throws Exception {

        StringBuilder combinedLines = new StringBuilder();
        combinedLines.append(lines[0]);
        for (int i = 1; i < lines.length; i++) {
            combinedLines.append(lineSeparator);
            combinedLines.append(lines[i]);
        }

        testGetLineUntilPosFromEnd(combinedLines.toString(), expectedResult, lineAcceptor);
    }

    private void testGetLineUntilPosFromEnd(
            String content,
            String expectedResult,
            Predicate<? super String> lineAcceptor) throws BadLocationException {
        testGetLineUntilPos(content, content.length(), expectedResult, lineAcceptor);
    }
}
