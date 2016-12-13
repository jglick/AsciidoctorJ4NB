package org.netbeans.asciidoc.highlighter;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;
import org.jtrim.collections.CollectionsEx;
import org.netbeans.api.lexer.InputAttributes;
import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.LanguagePath;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.spi.lexer.LanguageEmbedding;
import org.netbeans.spi.lexer.LanguageHierarchy;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

public enum AsciidoctorTokenId implements TokenId {
    HEADER1(1, "header1", AsciidoctorTokenId::getHeaderName),
    HEADER2(2, "header2", AsciidoctorTokenId::getHeaderName),
    HEADER3(3, "header3", AsciidoctorTokenId::getHeaderName),
    HEADER4(4, "header4", AsciidoctorTokenId::getHeaderName),
    HEADER5(5, "header5", AsciidoctorTokenId::getHeaderName),
    HEADER6(6, "header6", AsciidoctorTokenId::getHeaderName),
    CODE_BLOCK(Integer.MAX_VALUE, "code_block", (a, b, c) -> "Code"),
    TEXT_BLOCK(Integer.MAX_VALUE, "text_block", (a, b, c) -> "Text Block"),
    OTHER(Integer.MAX_VALUE, "other", (a, b, c) -> "Text");

    private static final Map<String, AsciidoctorTokenId> BY_CODE_NAMES;

    static {
        AsciidoctorTokenId[] ids = values();
        BY_CODE_NAMES = CollectionsEx.newHashMap(ids.length);

        for (AsciidoctorTokenId id: ids) {
            BY_CODE_NAMES.put(id.getCodeName(), id);
        }
    }

    private final int level;
    private final String codeName;
    private final NameParser nameParser;

    private AsciidoctorTokenId(int level, String codeName, NameParser nameParser) {
        this.level = level;
        this.codeName = codeName;
        this.nameParser = nameParser;
    }

    private static String getHeaderName(CharSequence str, int startOffset, int endOffset) {
        int strLength = endOffset - startOffset;
        if (strLength <= 0) {
            return "";
        }

        int textStartIndex = findNonMatching(str, startOffset, endOffset, '=');
        int textEndIndex = findEolIndex(str, textStartIndex, endOffset);

        StringBuilder result = new StringBuilder(strLength);
        result.append(str, textStartIndex, textEndIndex);
        return result.toString().trim();
    }

    private static int findEolIndex(CharSequence str, int startOffset, int endOffset) {
        for (int i = startOffset; i < endOffset; i++) {
            char ch = str.charAt(i);
            if (ch == '\n' || ch == '\r') {
                return i;
            }
        }
        return endOffset;
    }

    private static int findNonMatching(CharSequence str, int startOffset, int endOffset, char ch) {
        for (int i = startOffset; i < endOffset; i++) {
            if (str.charAt(i) != ch) {
                return i;
            }
        }
        return endOffset;
    }

    public String getName(CharSequence str, int startOffset, int endOffset) {
        return nameParser.getName(str, startOffset, endOffset);
    }

    public boolean isTableOfContentToken() {
        return level != Integer.MAX_VALUE;
    }

    public int getLevel() {
        return level;
    }

    public static AsciidoctorTokenId tryGetByCode(String codeName) {
        return BY_CODE_NAMES.get(codeName);
    }

    private static final Language<AsciidoctorTokenId> LANGUAGE = new LanguageHierarchy<AsciidoctorTokenId>() {
        @Override
        protected Collection<AsciidoctorTokenId> createTokenIds() {
            return EnumSet.allOf(AsciidoctorTokenId.class);
        }

        @Override
        protected Lexer<AsciidoctorTokenId> createLexer(LexerRestartInfo<AsciidoctorTokenId> info) {
            return AsciidoctorLexer.create(info);
        }

        @Override
        protected String mimeType() {
            return AsciidoctorLanguageConfig.MIME_TYPE;
        }

        @Override
        protected LanguageEmbedding<?> embedding(
                Token<AsciidoctorTokenId> token,
                LanguagePath languagePath,
                InputAttributes inputAttributes) {
            return null;
        }
    }.language();

    public String getCodeName() {
        return codeName;
    }

    @Override
    public String primaryCategory() {
        return codeName;
    }

    public static Language<AsciidoctorTokenId> language() {
        return LANGUAGE;
    }

    private interface NameParser {
        public String getName(CharSequence str, int startOffset, int endOffset);
    }
}
