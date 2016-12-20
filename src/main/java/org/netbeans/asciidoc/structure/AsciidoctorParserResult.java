package org.netbeans.asciidoc.structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.netbeans.modules.csl.api.Error;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.api.Snapshot;

public final class AsciidoctorParserResult extends ParserResult {
    private final List<AsciidoctorToken> tokens;

    public AsciidoctorParserResult(Snapshot snapshot, List<AsciidoctorToken> tokens) {
        super(snapshot);

        this.tokens = Collections.unmodifiableList(filter(tokens));
    }

    private static List<AsciidoctorToken> filter(List<AsciidoctorToken> tokens) {
        List<AsciidoctorToken> result = new ArrayList<>();
        tokens.forEach((token) -> {
            if (token.getId() != AsciidoctorTokenId.PLAIN) {
                result.add(token);
            }
        });
        return result;
    }

    public List<AsciidoctorToken> getTokens() {
        return tokens;
    }

    @Override
    protected void invalidate() {
    }

    @Override
    public List<? extends Error> getDiagnostics() {
        return Collections.emptyList();
    }
}
