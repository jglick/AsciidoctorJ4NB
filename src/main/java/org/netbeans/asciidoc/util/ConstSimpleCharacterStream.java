package org.netbeans.asciidoc.util;

public final class ConstSimpleCharacterStream implements SimpleCharacterStream {
    private final CharSequence input;
    private int offset;

    public ConstSimpleCharacterStream(CharSequence input) {
        this.input = input;
        this.offset = 0;
    }

    @Override
    public int readNext() {
        int currentOffset = offset;
        CharSequence currentInput = input;

        if (currentInput.length() <= currentOffset) {
            return -1;
        }

        offset++;
        return currentInput.charAt(currentOffset);
    }
}
