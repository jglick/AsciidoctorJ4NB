package org.netbeans.asciidoc.editor;

import javax.swing.text.Document;
import org.netbeans.asciidoc.util.DocumentUtils;

public final class DefaultDocumentNewLineInserter implements DocumentNewLineInserter {
    private static final NewLineInserter LINE_INSERTERS = NewLineInserters.indentableLineInserters(
            NewLineInserters::tryInsertArabicListLine,
            NewLineInserters::tryInsertLetterListLine,
            NewLineInserters::tryInsertRomanListLine,
            NewLineInserters.unorderedListElementInserter(),
            NewLineInserters.nestableListElementInserter1(),
            NewLineInserters.nestableListElementInserter2()
    );

    @Override
    public String tryGetLineToAdd(Document document, int caretOffset) {
        String line = DocumentUtils.getLineUntilPos(document, caretOffset, (String candidate) -> {
            if (candidate.isEmpty() || candidate.charAt(0) > ' ') {
                return true;
            }

            return LINE_INSERTERS.tryGetLineToAdd(candidate) != null;
        });
        return LINE_INSERTERS.tryGetLineToAdd(line);
    }
}
