package org.netbeans.asciidoc.highlighter;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Locale;
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
    HEADER1("header1"),
    HEADER2("header2"),
    HEADER3("header3"),
    HEADER4("header4"),
    HEADER5("header5"),
    HEADER6("header6"),
    CODE_BLOCK("code_block"),
    OTHER("other");

    private static final Map<String, AsciidoctorTokenId> BY_CODE_NAMES;

    static {
        AsciidoctorTokenId[] ids = values();
        BY_CODE_NAMES = CollectionsEx.newHashMap(ids.length);

        for (AsciidoctorTokenId id: ids) {
            BY_CODE_NAMES.put(id.getCodeName(), id);
        }
    }

    private final String codeName;

    private AsciidoctorTokenId(String codeName) {
        this.codeName = codeName;
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
}
