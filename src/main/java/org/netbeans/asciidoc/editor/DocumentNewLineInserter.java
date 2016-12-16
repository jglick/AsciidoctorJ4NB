package org.netbeans.asciidoc.editor;

import javax.swing.text.Document;

public interface DocumentNewLineInserter {
    public String tryGetLineToAdd(Document document, int caretOffset);
}
