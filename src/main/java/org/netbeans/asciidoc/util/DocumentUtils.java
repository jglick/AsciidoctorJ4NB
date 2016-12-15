package org.netbeans.asciidoc.util;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Segment;
import org.jtrim.utils.ExceptionHelper;

public final class DocumentUtils {
    private static final int READ_BLOCK_SIZE = 128;

    public static String getLineUntilPos(Document doc, int pos) {
        try {
            return getLineUntilPos0(doc, pos);
        } catch (BadLocationException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static String getLineUntilPos0(Document doc, int pos) throws BadLocationException {
        final int startPos = doc.getStartPosition().getOffset();
        ExceptionHelper.checkArgumentInRange(pos, startPos, Integer.MAX_VALUE, "pos");

        StringBuilder result = new StringBuilder(READ_BLOCK_SIZE);

        Segment buffer = new Segment();
        buffer.setPartialReturn(false);

        int endOffset = pos;
        while (endOffset > startPos) {
            int blockStartPos = Math.max(startPos, endOffset - READ_BLOCK_SIZE);
            doc.getText(blockStartPos, endOffset - blockStartPos, buffer);
            if (copyReversedUntilEol(buffer.array, buffer.offset, buffer.count, result)) {
                break;
            }

            endOffset -= buffer.count;
        }

        result.reverse();
        return result.toString();
    }

    private static boolean copyReversedUntilEol(char[] chars, int offset, int length, StringBuilder result) {
        for (int i = offset + length - 1; i >= offset; i--) {
            char ch = chars[i];
            if (ch == '\n' || ch == '\r') {
                return true;
            }
            result.append(ch);
        }
        return false;
    }

    private DocumentUtils() {
        throw new AssertionError();
    }
}
