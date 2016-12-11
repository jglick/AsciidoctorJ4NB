package org.netbeans.asciidoc.util;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.util.Objects;
import org.junit.Test;

import static org.junit.Assert.*;

public class StringSplittersTest {
    @Test
    public void testSplitByChar() {
        testSplitByChar("", 'x', "");
        testSplitByChar(":", ':', "", "");
        testSplitByChar("a:", ':', "a", "");
        testSplitByChar("a:b", ':', "a", "b");
        testSplitByChar("abcd:", ':', "abcd", "");
        testSplitByChar("abcd:::", ':', "abcd", "", "", "");
        testSplitByChar("abcd:efg", ':', "abcd", "efg");
        testSplitByChar("ab:cde:fg:hijk", ':', "ab", "cde", "fg", "hijk");
    }

    private void testSplitByChar(String input, char separator, String... parts) {
        testSplitByChar(false, input, separator, parts);
        testSplitByChar(true, input, separator, parts);
    }

    private void testSplitByChar(boolean peek, String input, char separator, String... parts) {
        PeekableIterator<String> itr = splitByCharItr(input, separator);

        int index = 0;
        while (itr.hasNext()) {
            String peekedPart = null;
            if (peek) {
                peekedPart = itr.tryPeekNext();
            }

            String part = itr.next();
            String expected = parts[index];

            if (!Objects.equals(expected, part)) {
                throw new AssertionError("Part mismatch at index " + index
                        + ". Expected: " + expected + ". Actual: " + part);
            }

            if (peek && !Objects.equals(peekedPart, part)) {
                throw new AssertionError("Peek mismatch at index " + index
                        + ". Expected: " + part + ". Actual: " + peekedPart);
            }

            index++;
        }

        assertEquals("parts.length", parts.length, index);

        String afterFinished = itr.tryPeekNext();
        assertNull("stream end", afterFinished);
    }

    private PeekableIterator<String> splitByCharItr(String input, char separator) {
        Reader reader = new StringReader(input);
        return StringSplitters.splitByChar(separator, () -> {
            try {
                return reader.read();
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        });
    }
}
