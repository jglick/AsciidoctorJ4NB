package org.netbeans.asciidoc.editor;

public interface NewLineInserter {
    public String tryGetLineToAdd(String prevLine);
}
