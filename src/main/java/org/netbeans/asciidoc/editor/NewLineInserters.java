package org.netbeans.asciidoc.editor;

import org.netbeans.asciidoc.util.RomanNumerals;

public final class NewLineInserters {
    private static final int MAX_ROMAN_LENGTH = 20;

    public static String tryInsertArabicListLine(String prevLine) {
        return tryInsertListLine(prevLine, '.', NewLineInserters::tryGetNextArabicIndex);
    }

    public static String tryInsertLetterListLine(String prevLine) {
        return tryInsertListLine(prevLine, '.', NewLineInserters::tryGetNextLetterIndex);
    }

    public static String tryInsertRomanListLine(String prevLine) {
        return tryInsertListLine(prevLine, ')', NewLineInserters::tryGetNextRomanIndex);
    }

    public static NewLineInserter unorderedListElementInserter() {
        return (prevLine) -> tryInsertUnorderedListElement(prevLine, '-');
    }

    private static String tryInsertUnorderedListElement(String prevLine, char prefixChar) {
        int nonSpaceIndex = findFirstNonSpace(prevLine);
        if (nonSpaceIndex < 0 || (nonSpaceIndex + 1 >= prevLine.length())) {
            return null;
        }

        if (prevLine.charAt(nonSpaceIndex) == prefixChar && isSpace(prevLine.charAt(nonSpaceIndex + 1))) {
            StringBuilder result = new StringBuilder(nonSpaceIndex + 3);
            result.append('\n');
            result.append(prevLine, 0, nonSpaceIndex + 2);
            return result.toString();
        }
        else {
            return null;
        }
    }

    private static String tryInsertListLine(String prevLine, char indexSepChar, NextIndexGetter nextIndexGetter) {
        int nonSpaceIndex = findFirstNonSpace(prevLine);
        if (nonSpaceIndex < 0) {
            return null;
        }

        int indexSepIndex = prevLine.indexOf(indexSepChar, nonSpaceIndex);
        if (indexSepIndex < 0) {
            return null;
        }

        String nextIndex = nextIndexGetter.tryGetNextIndex(prevLine, nonSpaceIndex, indexSepIndex);
        if (nextIndex == null) {
            return null;
        }

        StringBuilder result = new StringBuilder(nonSpaceIndex + nextIndex.length() + 3);
        result.append('\n');
        result.append(prevLine, 0, nonSpaceIndex);
        result.append(nextIndex);
        result.append(indexSepChar);
        result.append(' ');
        return result.toString();
    }

    private static String tryGetNextArabicIndex(String indexStr, int startOffset, int endOffset) {
        for (int i = startOffset; i < endOffset; i++) {
            char ch = indexStr.charAt(i);
            if (ch < '0' || ch > '9') {
                return null;
            }
        }

        if (endOffset - startOffset > 20) {
            // This would be too much for an long anyway, don't bother
            return null;
        }

        try {
            long currentValue = Long.parseLong(indexStr.substring(startOffset, endOffset));
            return Long.toString(currentValue + 1);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static String tryGetNextRomanIndex(String indexStr, int startOffset, int endOffset) {
        if (endOffset - startOffset > MAX_ROMAN_LENGTH) {
            return null;
        }

        try {
            int index = RomanNumerals.parseRoman(indexStr, startOffset, endOffset, -1);
            if (index == -1) {
                return null;
            }

            return RomanNumerals.tryConvertToRoman(index + 1, hasUpper(indexStr, startOffset, endOffset));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static boolean hasUpper(String str, int startOffset, int endOffset) {
        for (int i = startOffset; i < endOffset; i++) {
            char ch = str.charAt(i);
            if (ch >= 'A' && ch <= 'Z') {
                return true;
            }
        }
        return false;
    }

    private static String tryGetNextLetterIndex(String indexStr, int startOffset, int endOffset) {
        if (endOffset - startOffset != 1) {
            return null;
        }

        char ch = indexStr.charAt(startOffset);
        if ((ch >= 'a' && ch < 'z') || (ch >= 'A' && ch < 'Z')) {
            return Character.toString((char)(ch + 1));
        }
        return null;
    }

    private static int findFirstNonSpace(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!isSpace(str.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    private static boolean isSpace(char ch) {
        return ch <= ' ';
    }

    private interface NextIndexGetter {
        public String tryGetNextIndex(String indexStr, int startOffset, int endOffset);
    }

    private NewLineInserters() {
        throw new AssertionError();
    }
}
