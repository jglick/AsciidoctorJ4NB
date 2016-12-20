package org.netbeans.asciidoc.structure;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Callable;
import javax.swing.text.Document;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.parsing.implspi.EnvironmentFactory;
import org.netbeans.modules.parsing.implspi.SchedulerControl;
import org.netbeans.modules.parsing.implspi.SourceControl;
import org.netbeans.modules.parsing.implspi.SourceEnvironment;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.parsing.spi.Scheduler;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

public final class TestEnvironmentFactory implements EnvironmentFactory {
    public TestEnvironmentFactory() {
    }

    @Override
    public Lookup getContextLookup() {
        return Lookup.EMPTY;
    }

    private RuntimeException unexpectedCall() {
        throw new AssertionError("Unexpected EnvironmentFactory access");
    }

    @Override
    public Class<? extends Scheduler> findStandardScheduler(String schedulerName) {
        throw unexpectedCall();
    }

    @Override
    public Parser findMimeParser(Lookup context, String mimeType) {
        throw unexpectedCall();
    }

    @Override
    public Collection<? extends Scheduler> getSchedulers(Lookup context) {
        return Collections.emptyList();
    }

    @Override
    public SourceEnvironment createEnvironment(Source src, SourceControl control) {
        return new SourceEnvironment(control) {
            @Override
            public Document readDocument(FileObject f, boolean forceOpen) throws IOException {
                throw unexpectedCall();
            }

            @Override
            public void attachScheduler(SchedulerControl s, boolean attach) {
                throw unexpectedCall();
            }

            @Override
            public void activate() {
            }

            @Override
            public boolean isReparseBlocked() {
                return false;
            }
        };
    }

    @Override
    public <T> T runPriorityIO(Callable<T> r) throws Exception {
        throw unexpectedCall();
    }
}
