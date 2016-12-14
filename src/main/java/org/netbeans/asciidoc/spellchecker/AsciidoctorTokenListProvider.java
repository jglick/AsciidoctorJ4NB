package org.netbeans.asciidoc.spellchecker;

import javax.swing.text.Document;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.asciidoc.structure.AsciidoctorLanguageConfig;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.spellchecker.spi.language.TokenList;
import org.netbeans.modules.spellchecker.spi.language.TokenListProvider;

/**
 * Copied (adjusted) from the Markdown plugin
 *
 * @author Holger Stenger
 */
@MimeRegistration(
        mimeType = AsciidoctorLanguageConfig.MIME_TYPE,
        service = TokenListProvider.class,
        position = 1010)
public class AsciidoctorTokenListProvider implements TokenListProvider {

    public AsciidoctorTokenListProvider() {
    }

    @Override
    public TokenList findTokenList(Document doc) {
        if (doc instanceof BaseDocument) {
            BaseDocument baseDoc = (BaseDocument)doc;
            final Object mimeType = baseDoc.getProperty("mimeType");
            if (AsciidoctorLanguageConfig.MIME_TYPE.equals(mimeType)) {
                return new AsciidoctorTokenList(doc);
            }
        }
        return null;
    }
}
