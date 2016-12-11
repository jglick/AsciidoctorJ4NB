package org.netbeans.asciidoc.highlighter;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import org.netbeans.api.lexer.Token;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

public final class AsciidoctorLexer implements Lexer<AsciidoctorTokenId> {
    private final LexerRestartInfo<AsciidoctorTokenId> info;
    private final AsciidocTokenizer tokenizer;
    private Iterator<AsciidoctorToken> tokensItr;

    private AsciidoctorLexer(LexerRestartInfo<AsciidoctorTokenId> info) {
        this.info = Objects.requireNonNull(info, "info");
        this.tokenizer = new AsciidocTokenizer();
        this.tokensItr = null;
    }

    public static Lexer<AsciidoctorTokenId> create(LexerRestartInfo<AsciidoctorTokenId> info) {
        return new AsciidoctorLexer(info);
    }

    @Override
    public Token<AsciidoctorTokenId> nextToken() {
        if (tokensItr == null) {
            tokensItr = readTokens().iterator();
        }

        if (!tokensItr.hasNext()) {
            return null;
        }

        AsciidoctorToken token = tokensItr.next();
        return info.tokenFactory().createToken(token.getId(), token.getLength());
    }

    @Override
    public Object state() {
        return null;
    }

    @Override
    public void release() {
    }

    private Collection<AsciidoctorToken> readTokens() {
        return tokenizer.readTokens(info.input()::read);
    }
}
