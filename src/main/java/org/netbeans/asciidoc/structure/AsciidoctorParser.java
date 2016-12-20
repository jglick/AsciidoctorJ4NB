package org.netbeans.asciidoc.structure;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeListener;
import org.netbeans.asciidoc.util.ConstSimpleCharacterStream;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.api.Task;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.parsing.spi.SourceModificationEvent;

public final class AsciidoctorParser extends Parser {
    private static final Logger LOGGER = Logger.getLogger(AsciidoctorParser.class.getName());

    private final AsciidoctorTokenizer tokenizer;
    private AsciidoctorParserResult parserResult;

    public AsciidoctorParser() {
        this.tokenizer = new AsciidoctorTokenizer();
        this.parserResult = null;
    }

    @Override
    public void parse(Snapshot snapshot, Task task, SourceModificationEvent event) throws ParseException {
        this.parserResult = parseSnapshot(snapshot);
    }

    private List<AsciidoctorToken> getTokens(CharSequence input) {
        try {
            return tokenizer.readTokens(new ConstSimpleCharacterStream(input));
        } catch (Exception ex) {
            LOGGER.log(Level.INFO, "Internal error: Tokenizer failed to read tokens.", ex);
            return Collections.emptyList();
        }
    }

    private AsciidoctorParserResult parseSnapshot(Snapshot snapshot) {
        List<AsciidoctorToken> tokens = getTokens(snapshot.getText());
        return new AsciidoctorParserResult(snapshot, tokens);
    }

    @Override
    public AsciidoctorParserResult getResult(Task task) throws ParseException {
        return parserResult;
    }

    @Override
    public void addChangeListener(ChangeListener changeListener) {
    }

    @Override
    public void removeChangeListener(ChangeListener changeListener) {
    }
}
