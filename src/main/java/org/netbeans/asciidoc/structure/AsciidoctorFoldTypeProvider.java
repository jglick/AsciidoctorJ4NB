package org.netbeans.asciidoc.structure;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.netbeans.api.editor.fold.FoldTemplate;
import org.netbeans.api.editor.fold.FoldType;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.spi.editor.fold.FoldTypeProvider;

@MimeRegistration(
        mimeType = AsciidoctorLanguageConfig.MIME_TYPE,
        service = FoldTypeProvider.class
)
public final class AsciidoctorFoldTypeProvider implements FoldTypeProvider {
    public static final FoldType TYPE_HEADER
            = FoldType.create("header", "Header", FoldTemplate.DEFAULT);
    public static final FoldType TYPE_TEXT_BLOCK
            = FoldType.create("text_block", "Text Block", FoldTemplate.DEFAULT_BLOCK);

    private final Collection<FoldType> folderTypes;

    public AsciidoctorFoldTypeProvider() {
        this.folderTypes = Collections.unmodifiableList(Arrays.asList(TYPE_HEADER, TYPE_TEXT_BLOCK));
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Collection<?> getValues(Class type) {
        return folderTypes;
    }

    @Override
    public boolean inheritable() {
        return false;
    }
}
