package org.netbeans.asciidoc;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.awt.UndoRedo;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;

@MultiViewElement.Registration(
        displayName = "#LBL_Adoc_VISUAL",
        iconBase = "org/netbeans/asciidoc/resources/icon.png",
        mimeType = "text/x-adoc",
        persistenceType = TopComponent.PERSISTENCE_NEVER,
        preferredID = "AdocVisual",
        position = 2000
)
@Messages("LBL_Adoc_VISUAL=Visual")
public final class AdocViewElement implements MultiViewElement {
    private final AdocDataObject obj;

    private final LazyValue<JToolBar> toolBarRef;
    private final LazyValue<AdocVisualPanel> panelRef;

    private final AtomicReference<FileChangeListener> listenerRef;

    public AdocViewElement(Lookup lookup) {
        this.obj = lookup.lookup(AdocDataObject.class);

        this.listenerRef = new AtomicReference<>(null);

        this.toolBarRef = new LazyValue<>(() -> {
            JToolBar toolbar = new JToolBar();
            toolbar.setFloatable(false);
            return toolbar;
        });

        this.panelRef = new LazyValue<>(() -> {
            AdocVisualPanel panel = new AdocVisualPanel();
            return panel;
        });
    }

    @Override
    public JComponent getVisualRepresentation() {
        return panelRef.get();
    }

    @Override
    public JComponent getToolbarRepresentation() {
        return toolBarRef.get();
    }

    @Override
    public Action[] getActions() {
        return new Action[0];
    }

    @Override
    public Lookup getLookup() {
        return obj.getLookup();
    }

    private void updateWithAsciidoc() {
        panelRef.get().updateWithAsciidoc(() -> {
            try {
                return obj.getPrimaryFile().asText();
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        });
    }

    private FileChangeListener detachChangeListener() {
        FileChangeListener listener = listenerRef.getAndSet(null);
        if (listener != null) {
            obj.getPrimaryFile().removeFileChangeListener(listener);
        }
        return listener;
    }

    @Override
    public void componentOpened() {
        if (obj == null) {
            return;
        }

        FileChangeListener newListener = new FileChangeAdapter() {
            @Override
            public void fileChanged(FileEvent fe) {
                updateWithAsciidoc();
            }
        };

        FileObject primaryFile = obj.getPrimaryFile();
        while (true) {
            FileChangeListener prevListener = detachChangeListener();
            primaryFile.addFileChangeListener(newListener);
            if (listenerRef.compareAndSet(prevListener, newListener)) {
                break;
            }
            else {
                primaryFile.removeFileChangeListener(newListener);
            }
        }

        updateWithAsciidoc();
    }

    @Override
    public void componentClosed() {
        detachChangeListener();
    }

    @Override
    public void componentShowing() {
    }

    @Override
    public void componentHidden() {
    }

    @Override
    public void componentActivated() {
    }

    @Override
    public void componentDeactivated() {
    }

    @Override
    public UndoRedo getUndoRedo() {
        return UndoRedo.NONE;
    }

    @Override
    public void setMultiViewCallback(MultiViewElementCallback callback) {
    }

    @Override
    public CloseOperationState canCloseElement() {
        return CloseOperationState.STATE_OK;
    }
}
