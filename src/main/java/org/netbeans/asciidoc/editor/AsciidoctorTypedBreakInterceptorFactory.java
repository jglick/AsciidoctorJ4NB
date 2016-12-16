package org.netbeans.asciidoc.editor;

import javax.swing.text.BadLocationException;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.asciidoc.structure.AsciidoctorLanguageConfig;
import org.netbeans.spi.editor.typinghooks.TypedBreakInterceptor;

@MimeRegistration(
        mimeType = AsciidoctorLanguageConfig.MIME_TYPE,
        service = TypedBreakInterceptor.Factory.class)
public final class AsciidoctorTypedBreakInterceptorFactory implements TypedBreakInterceptor.Factory {
    @Override
    public TypedBreakInterceptor createTypedBreakInterceptor(MimePath mimePath) {
        return new AsciidoctorTypedBreakInterceptor();
    }

    private static final class AsciidoctorTypedBreakInterceptor implements TypedBreakInterceptor {
        private static final DocumentNewLineInserter NEW_LINE_INSERTER = new DefaultDocumentNewLineInserter();

        @Override
        public boolean beforeInsert(Context context) throws BadLocationException {
            return false;
        }

        @Override
        public void insert(MutableContext context) throws BadLocationException {
            String newLine = NEW_LINE_INSERTER.tryGetLineToAdd(context.getDocument(), context.getCaretOffset());
            if (newLine != null) {
                context.setText(newLine, 0, newLine.length());
            }
        }

        @Override
        public void afterInsert(Context context) throws BadLocationException {
        }

        @Override
        public void cancelled(Context context) {
        }
    }
}
