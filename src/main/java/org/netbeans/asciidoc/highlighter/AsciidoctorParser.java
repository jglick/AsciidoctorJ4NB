package org.netbeans.asciidoc.highlighter;

import java.util.Collections;
import java.util.List;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.api.Task;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.parsing.spi.SourceModificationEvent;

public final class AsciidoctorParser extends Parser {
    private final AsciidocTokenizer tokenizer;
    private Snapshot snapshot;
    private List<AsciidoctorToken> tokens;

    public AsciidoctorParser() {
        this.tokenizer = new AsciidocTokenizer();
        this.tokens = Collections.emptyList();
    }

    @Override
    public void parse(Snapshot snapshot, Task task, SourceModificationEvent event) throws ParseException {
        this.snapshot = snapshot;
        this.tokens = tokenizer.readTokens(snapshot.getText());
    }

    @Override
    public Result getResult(Task task) throws ParseException {
        return new AsciidoctorParserResult(snapshot, tokens);
    }

    @Override
    public void addChangeListener(ChangeListener changeListener) {
    }

    @Override
    public void removeChangeListener(ChangeListener changeListener) {
    }
}
