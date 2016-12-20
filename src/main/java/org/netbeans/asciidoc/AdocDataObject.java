package org.netbeans.asciidoc;

import java.io.IOException;
import org.netbeans.asciidoc.structure.AsciidoctorLanguageConfig;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.text.MultiViewEditorElement;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.MIMEResolver;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;

@Messages({
    "LBL_ASCIIDOC_LOADER=Files of AsciiDoc"
})
@MIMEResolver.ExtensionRegistration(
        displayName = "#LBL_ASCIIDOC_LOADER",
        mimeType = AsciidoctorLanguageConfig.MIME_TYPE,
        extension = {"ad", "adoc", "asc", "asciidoc"}
)
@DataObject.Registration(
        mimeType = AsciidoctorLanguageConfig.MIME_TYPE,
        iconBase = "org/netbeans/asciidoc/resources/icon.png",
        displayName = "#LBL_ASCIIDOC_LOADER",
        position = 300
)
@ActionReferences({
    @ActionReference(
            path = AdocDataObject.ASCIIDOC_LOADERS_PATH + "/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.OpenAction"),
            position = 100,
            separatorAfter = 200
    ),
    @ActionReference(
            path = AdocDataObject.ASCIIDOC_LOADERS_PATH + "/Actions",
            id = @ActionID(category = "Edit", id = "org.openide.actions.CutAction"),
            position = 300
    ),
    @ActionReference(
            path = AdocDataObject.ASCIIDOC_LOADERS_PATH + "/Actions",
            id = @ActionID(category = "Edit", id = "org.openide.actions.CopyAction"),
            position = 400,
            separatorAfter = 500
    ),
    @ActionReference(
            path = AdocDataObject.ASCIIDOC_LOADERS_PATH + "/Actions",
            id = @ActionID(category = "Edit", id = "org.openide.actions.DeleteAction"),
            position = 600
    ),
    @ActionReference(
            path = AdocDataObject.ASCIIDOC_LOADERS_PATH + "/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.RenameAction"),
            position = 700,
            separatorAfter = 800
    ),
    @ActionReference(
            path = AdocDataObject.ASCIIDOC_LOADERS_PATH + "/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.SaveAsTemplateAction"),
            position = 900,
            separatorAfter = 1000
    ),
    @ActionReference(
            path = AdocDataObject.ASCIIDOC_LOADERS_PATH + "/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.FileSystemAction"),
            position = 1100,
            separatorAfter = 1200
    ),
    @ActionReference(
            path = AdocDataObject.ASCIIDOC_LOADERS_PATH + "/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.ToolsAction"),
            position = 1300
    ),
    @ActionReference(
            path = AdocDataObject.ASCIIDOC_LOADERS_PATH + "/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.PropertiesAction"),
            position = 1400
    )
})
public class AdocDataObject extends MultiDataObject {
    private static final long serialVersionUID = 1L;

    static final String ASCIIDOC_LOADERS_PATH = "Loaders/text/" + AsciidoctorLanguageConfig.MIME_TYPE;

    public AdocDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        registerEditor(AsciidoctorLanguageConfig.MIME_TYPE, true);
    }

    @Override
    protected int associateLookup() {
        return 1;
    }

    @MultiViewElement.Registration(
            displayName = "#LBL_ASCIIDOC_EDITOR",
            iconBase = "org/netbeans/asciidoc/resources/icon.png",
            mimeType = "text/x-asciidoc",
            persistenceType = TopComponent.PERSISTENCE_ONLY_OPENED,
            preferredID = "AsciiDoc",
            position = 1000
    )
    @Messages("LBL_ASCIIDOC_EDITOR=Source")
    public static MultiViewEditorElement createEditor(Lookup lkp) {
        return new MultiViewEditorElement(lkp);
    }

}
