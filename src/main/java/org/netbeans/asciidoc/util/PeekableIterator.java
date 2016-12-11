package org.netbeans.asciidoc.util;

import java.util.Iterator;

public interface PeekableIterator<T> extends Iterator<T> {
    public T tryPeekNext();
}
