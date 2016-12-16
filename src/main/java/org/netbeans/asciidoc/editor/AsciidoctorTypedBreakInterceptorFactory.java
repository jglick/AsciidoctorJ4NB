package org.netbeans.asciidoc.editor;

import javax.swing.text.BadLocationException;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.asciidoc.structure.AsciidoctorLanguageConfig;
import org.netbeans.asciidoc.util.DocumentUtils;
import org.netbeans.spi.editor.typinghooks.TypedBreakInterceptor;

@MimeRegistration(
        mimeType = AsciidoctorLanguageConfig.MIME_TYPE,
        service = TypedBreakInterceptor.Factory.class)
public final class AsciidoctorTypedBreakInterceptorFactory implements TypedBreakInterceptor.Factory {
    private static final NewLineInserter LINE_INSERTERS = NewLineInserters.indentableLineInserters(
            NewLineInserters::tryInsertArabicListLine,
            NewLineInserters::tryInsertLetterListLine,
            NewLineInserters::tryInsertRomanListLine,
            NewLineInserters.unorderedListElementInserter(),
            NewLineInserters.nestableListElementInserter1(),
            NewLineInserters.nestableListElementInserter2()
    );

    @Override
    public TypedBreakInterceptor createTypedBreakInterceptor(MimePath mimePath) {
        return new AsciidoctorTypedBreakInterceptor();
    }

    private static final class AsciidoctorTypedBreakInterceptor implements TypedBreakInterceptor {
        @Override
        public boolean beforeInsert(Context context) throws BadLocationException {
            return false;
        }

        @Override
        public void insert(MutableContext context) throws BadLocationException {
            int caretOffset = context.getCaretOffset();

            String line = DocumentUtils.getLineUntilPos(context.getDocument(), caretOffset);
            String newLine = LINE_INSERTERS.tryGetLineToAdd(line);
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
