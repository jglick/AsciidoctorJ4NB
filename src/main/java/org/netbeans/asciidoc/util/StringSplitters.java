package org.netbeans.asciidoc.util;

import java.util.NoSuchElementException;
import java.util.Objects;

public final class StringSplitters {
    public static PeekableIterator<String> splitByChar(char separator, SimpleCharacterStream input) {
        return new SplitByCharPeekableIterator(separator, input);
    }

    private static final class SplitByCharPeekableIterator implements PeekableIterator<String> {
        private final char separator;
        private final SimpleCharacterStream input;

        private final StringBuilder lineBuffer;

        private boolean finished;
        private String next;

        public SplitByCharPeekableIterator(char separator, SimpleCharacterStream input) {
            this.separator = separator;
            this.input = Objects.requireNonNull(input, "input");
            this.lineBuffer = new StringBuilder();
            this.finished = false;

            this.next = readNext();
        }

        @Override
        public String tryPeekNext() {
            if (next != null) {
                return next;
            }

            String result = readNext();
            next = result;
            return result;
        }

        private String readNext() {
            if (finished) {
                return null;
            }

            SimpleCharacterStream currentInput = this.input;
            char currentSeparator = separator;

            StringBuilder resultBuilder = lineBuffer;
            resultBuilder.setLength(0);

            while (true) {
                int chInt = currentInput.readNext();
                if (chInt < 0) {
                    finished = true;
                    break;
                }

                char ch = (char)chInt;
                if (ch == currentSeparator) {
                    break;
                }

                resultBuilder.append(ch);
            }

            return resultBuilder.toString();
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public String next() {
            String result = next;
            if (result == null) {
                throw new NoSuchElementException();
            }

            next = readNext();
            return result;
        }
    }

    private StringSplitters() {
        throw new AssertionError();
    }
}
