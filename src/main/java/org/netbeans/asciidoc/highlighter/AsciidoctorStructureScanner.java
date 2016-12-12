package org.netbeans.asciidoc.highlighter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import org.netbeans.modules.csl.api.ElementHandle;
import org.netbeans.modules.csl.api.ElementKind;
import org.netbeans.modules.csl.api.HtmlFormatter;
import org.netbeans.modules.csl.api.Modifier;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.api.StructureItem;
import org.netbeans.modules.csl.api.StructureScanner;
import org.netbeans.modules.csl.spi.ParserResult;

public final class AsciidoctorStructureScanner implements StructureScanner {
    private static final Logger LOGGER = Logger.getLogger(AsciidoctorStructureScanner.class.getName());

    public AsciidoctorStructureScanner() {
    }

    @Override
    public List<? extends StructureItem> scan(ParserResult info) {
        if (info instanceof AsciidoctorParserResult) {
            try {
                AsciidoctorParserResult result = (AsciidoctorParserResult)info;
                List<AsciidoctorToken> tokens = result.getTokens();
                return toStructureItems(info.getSnapshot().getText(), tokens);
            } catch (Exception ex) {
                LOGGER.log(Level.INFO, "Internal error: Failed to create structured tree from tokens.", ex);
                return Collections.emptyList();
            }
        }
        else {
            return Collections.emptyList();
        }
    }

    private static List<StructureItem> toStructureItems(CharSequence input, List<AsciidoctorToken> tokens) {
        if (tokens.isEmpty()) {
            return Collections.emptyList();
        }

        List<StructureItem> result = new ArrayList<>();

        // FIXME: This alg is quadratic.

        int startIndex = 0;
        int currentLevel = tokens.get(0).getId().getLevel();
        int tokenCount = tokens.size();
        for (int i = 1; i < tokenCount; i++) {
            AsciidoctorToken token = tokens.get(i);
            AsciidoctorTokenId id = token.getId();
            int level = id.getLevel();
            if (level <= currentLevel) {
                List<StructureItem> children = toStructureItems(input, tokens.subList(startIndex + 1, i));
                result.add(new HierarchicalStructureItem(input, tokens.get(startIndex), children, token.getStartIndex()));

                startIndex = i;
                currentLevel = token.getId().getLevel();
            }
        }

        List<StructureItem> children = toStructureItems(input, tokens.subList(startIndex + 1, tokenCount));
        result.add(new HierarchicalStructureItem(input, tokens.get(startIndex), children, input.length()));

        return result;
    }

    @Override
    public Map<String, List<OffsetRange>> folds(ParserResult info) {
        // TODO: Implement
        return Collections.emptyMap();
    }

    @Override
    public Configuration getConfiguration() {
        return new Configuration(false, false);
    }

    private static final class HierarchicalStructureItem implements StructureItem {
        private final String name;
        private final AsciidoctorToken token;
        private final List<StructureItem> children;

        private final long endPosition;

        public HierarchicalStructureItem(
                CharSequence input,
                AsciidoctorToken token,
                List<StructureItem> children,
                long siblingStartPosition) {

            this.name = token.getName(input);
            this.token = token;
            this.children = Collections.unmodifiableList(children);

            this.endPosition = Math.max(token.getEndIndex(), siblingStartPosition) - 1;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getSortText() {
            return "";
        }

        @Override
        public String getHtml(HtmlFormatter formatter) {
            formatter.appendText(getName());
            return formatter.getText();
        }

        @Override
        public ElementHandle getElementHandle() {
            return null;
        }

        @Override
        public ElementKind getKind() {
            return ElementKind.PACKAGE;
        }

        @Override
        public Set<Modifier> getModifiers() {
            return EnumSet.of(Modifier.PUBLIC);
        }

        @Override
        public boolean isLeaf() {
            return children.isEmpty();
        }

        @Override
        public List<? extends StructureItem> getNestedItems() {
            return children;
        }

        @Override
        public long getPosition() {
            return token.getStartIndex();
        }

        @Override
        public long getEndPosition() {
            return endPosition;
        }

        @Override
        public ImageIcon getCustomIcon() {
            return null;
        }
    }
}
