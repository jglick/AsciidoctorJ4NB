package org.netbeans.asciidoc.util;

import java.util.function.Predicate;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Segment;
import org.jtrim.utils.ExceptionHelper;

public final class DocumentUtils {
    private static final int READ_BLOCK_SIZE = 128;

    public static String getLineUntilPos(Document doc, int pos) {
        return getLineUntilPos(doc, pos, (line) -> true);
    }

    public static String getLineUntilPos(Document doc, int pos, Predicate<? super String> lineAcceptor) {
        try {
            return getLineUntilPos0(doc, pos, lineAcceptor);
        } catch (BadLocationException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static String getLineUntilPos0(
            Document doc,
            int pos,
            Predicate<? super String> lineAcceptor) throws BadLocationException {
        final int startPos = doc.getStartPosition().getOffset();
        ExceptionHelper.checkArgumentInRange(pos, startPos, Integer.MAX_VALUE, "pos");

        StringBuilder result = new StringBuilder(READ_BLOCK_SIZE);

        Segment buffer = new Segment();
        buffer.setPartialReturn(false);

        boolean skipCr = false;
        int nextEndOffset = pos;
        while (nextEndOffset > startPos) {
            int endOffset = nextEndOffset;
            int blockStartPos = Math.max(startPos, endOffset - READ_BLOCK_SIZE);
            doc.getText(blockStartPos, endOffset - blockStartPos, buffer);
            nextEndOffset = endOffset - buffer.count;

            while (buffer.count > 0) {
                if (skipCr) {
                    skipCr = false;

                    if (lastChar(buffer) == '\r') {
                        removeSuffix(buffer, 1);
                        if (buffer.count <= 0) {
                            break;
                        }
                    }
                }

                int addCount = copyReversedUntilEol(buffer.array, buffer.offset, buffer.count, result);
                if (addCount == buffer.count) {
                    buffer.count = 0;
                }
                else {
                    assert addCount < buffer.count;

                    result.reverse();
                    String candidate = result.toString();
                    if (lineAcceptor.test(candidate)) {
                        return candidate;
                    }

                    result.setLength(0);

                    removeSuffix(buffer, addCount);

                    if (lastChar(buffer) == '\r') {
                        removeSuffix(buffer, 1);
                        skipCr = false;
                    }
                    else {
                        assert lastChar(buffer) == '\n';
                        removeSuffix(buffer, 1);

                        if (buffer.count > 0) {
                            if (lastChar(buffer) == '\r') {
                                removeSuffix(buffer, 1);
                            }
                        }
                        else {
                            skipCr = true;
                        }
                    }
                }
            }
        }

        result.reverse();
        return result.toString();
    }

    private static char lastChar(Segment buffer) {
        assert buffer.count > 0;
        return buffer.array[buffer.offset + buffer.count - 1];
    }

    private static void removeSuffix(Segment buffer, int suffixLength) {
        assert buffer.count >= suffixLength;
        buffer.count -= suffixLength;
    }

    private static int copyReversedUntilEol(char[] chars, int offset, int length, StringBuilder result) {
        int addCount = 0;
        for (int i = offset + length - 1; i >= offset; i--) {
            char ch = chars[i];
            if (ch == '\n' || ch == '\r') {
                return addCount;
            }
            addCount++;
            result.append(ch);
        }
        return addCount;
    }

    private DocumentUtils() {
        throw new AssertionError();
    }
}
