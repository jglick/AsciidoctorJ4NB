package org.netbeans.asciidoc.editor;

import javax.swing.text.BadLocationException;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.asciidoc.structure.AsciidoctorLanguageConfig;
import org.netbeans.asciidoc.util.DocumentUtils;
import org.netbeans.asciidoc.util.RomanNumerals;
import org.netbeans.spi.editor.typinghooks.TypedBreakInterceptor;

public final class AsciidoctorTypedBreakInterceptor implements TypedBreakInterceptor {
    private static final int MAX_ROMAN_LENGTH = 20;

    private static final LineInserter[] LINE_INSERTERS = new LineInserter[]{
        AsciidoctorTypedBreakInterceptor::tryInsertArabicListLine,
        AsciidoctorTypedBreakInterceptor::tryInsertLetterListLine,
        AsciidoctorTypedBreakInterceptor::tryInsertRomanListLine
    };

    @Override
    public boolean beforeInsert(Context context) throws BadLocationException {
        return false;
    }

    @Override
    public void insert(MutableContext context) throws BadLocationException {
        int caretOffset = context.getCaretOffset();

        String line = DocumentUtils.getLineUntilPos(context.getDocument(), caretOffset);
        for (LineInserter inserter: LINE_INSERTERS) {
            String newLine = inserter.tryGetLineToAdd(line);
            if (newLine != null) {
                context.setText(newLine, 0, newLine.length());
                return;
            }
        }
    }

    @Override
    public void afterInsert(Context context) throws BadLocationException {
    }

    @Override
    public void cancelled(Context context) {
    }

    private static String tryInsertArabicListLine(String prevLine) {
        return tryInsertListLine(prevLine, '.', AsciidoctorTypedBreakInterceptor::tryGetNextArabicIndex);
    }

    private static String tryInsertLetterListLine(String prevLine) {
        return tryInsertListLine(prevLine, '.', AsciidoctorTypedBreakInterceptor::tryGetNextLetterIndex);
    }

    private static String tryInsertRomanListLine(String prevLine) {
        return tryInsertListLine(prevLine, ')', AsciidoctorTypedBreakInterceptor::tryGetNextRomanIndex);
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
            if (str.charAt(i) > ' ') {
                return i;
            }
        }
        return -1;
    }

    private interface NextIndexGetter {
        public String tryGetNextIndex(String indexStr, int startOffset, int endOffset);
    }

    private interface LineInserter {
        public String tryGetLineToAdd(String prevLine);
    }

    @MimeRegistration(
            mimeType = AsciidoctorLanguageConfig.MIME_TYPE,
            service = TypedBreakInterceptor.Factory.class)
    public static class AsciidoctorFactory implements TypedBreakInterceptor.Factory {
        @Override
        public TypedBreakInterceptor createTypedBreakInterceptor(MimePath mimePath) {
            return new AsciidoctorTypedBreakInterceptor();
        }
    }
}
