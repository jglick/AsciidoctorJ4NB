package org.netbeans.asciidoc.util;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import javax.swing.text.StringContent;
import org.jtrim.utils.ExceptionHelper;
import org.netbeans.asciidoc.structure.AsciidoctorLanguageConfig;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.api.Source;

public final class TestDocumentUtils {
    public static Document createDocument(String content) {
        ExceptionHelper.checkNotNullArgument(content, "content");

        StringContent docContent = new StringContent();
        try {
            docContent.insertString(0, content);
        } catch (BadLocationException ex) {
            throw new RuntimeException(ex);
        }

        return new PlainDocument(docContent);
    }

    public static Source createAsciidoctorSource(String content) {
        Document document = createDocument(content);
        document.putProperty("mimeType", AsciidoctorLanguageConfig.MIME_TYPE);
        return Source.create(document);
    }

    public static Snapshot createAsciidoctorSnapshot(String content) {
        Source source = createAsciidoctorSource(content);
        return source.createSnapshot();
    }

    public static String generateTestString(int length) {
        return generateTestString(length, 'a', 'z');
    }

    public static String generateTestString(int length, char startChar, char endChar) {
        StringBuilder result = new StringBuilder();
        char ch = 'a';
        for (int i = 0; i < length; i++) {
            result.append(ch);
            ch = (char)(((ch + 1 - startChar) % (endChar - startChar)) + startChar);
        }
        return result.toString();
    }

    private TestDocumentUtils() {
        throw new AssertionError();
    }
}
