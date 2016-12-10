package org.netbeans.asciidoc.highlighter;

import org.netbeans.api.lexer.Language;
import org.netbeans.modules.csl.api.StructureScanner;
import org.netbeans.modules.csl.spi.DefaultLanguageConfig;
import org.netbeans.modules.csl.spi.LanguageRegistration;
import org.netbeans.modules.parsing.spi.Parser;

@LanguageRegistration(mimeType = AsciidoctorLanguageConfig.MIME_TYPE)
public final class AsciidoctorLanguageConfig extends DefaultLanguageConfig {
    public static final String MIME_TYPE = "text/x-asciidoc";

    @Override
    public Language<AsciidoctorTokenId> getLexerLanguage() {
        return AsciidoctorTokenId.language();
    }

    @Override
    public String getDisplayName() {
        return "Asciidoctor";
    }

    @Override
    public Parser getParser() {
        return new AsciidoctorParser();
    }

    @Override
    public boolean hasStructureScanner() {
        return true;
    }

    @Override
    public StructureScanner getStructureScanner() {
        return new AsciidoctorStructureScanner();
    }
}
