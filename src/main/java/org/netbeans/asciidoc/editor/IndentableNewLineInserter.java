package org.netbeans.asciidoc.editor;

public interface IndentableNewLineInserter {
    public String tryGetLineToAdd(String prevLine, int nonSpaceIndex);
}
