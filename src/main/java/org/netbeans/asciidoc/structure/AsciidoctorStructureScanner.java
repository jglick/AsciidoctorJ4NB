package org.netbeans.asciidoc.structure;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
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
                LOGGER.log(Level.INFO, "Internal error: Failed to create structure tree from tokens.", ex);
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
        Deque<HierarchicalStructureItem> parents = new ArrayDeque<>();

        Stream<AsciidoctorToken> filteredTokens = tokens.stream()
                .filter((token) -> token.getId().isTableOfContentToken());

        filteredTokens.forEach((token) -> {
            HierarchicalStructureItem current = new HierarchicalStructureItem(input, token);

            int currentLevel = token.getId().getLevel();

            HierarchicalStructureItem parent = null;
            while (true) {
                parent = parents.peekFirst();
                if (parent == null) {
                    break;
                }

                int parentLevel = parent.getLevel();
                if (parentLevel < currentLevel) {
                    break;
                }

                parent.setEndPosition(current.getPosition() - 1);
                parents.pop();
            }

            if (parent == null) {
                result.add(current);
            }
            else {
                parent.addChild(current);
            }

            parents.push(current);
        });

        return result;
    }

    @Override
    public Map<String, List<OffsetRange>> folds(ParserResult info) {
        if (info instanceof AsciidoctorParserResult) {
            try {
                AsciidoctorParserResult result = (AsciidoctorParserResult)info;
                List<AsciidoctorToken> tokens = result.getTokens();
                return folds(result.getSnapshot().getText().length(), tokens);
            } catch (Exception ex) {
                LOGGER.log(Level.INFO, "Internal error: Failed to create folds from tokens.", ex);
                return Collections.emptyMap();
            }
        }
        else {
            return Collections.emptyMap();
        }
    }

    private static <K, V> List<V> getKeyList(Map<K, List<V>> map, K key) {
        return map.computeIfAbsent(key, (currentKey) -> new ArrayList<>());
    }

    private Map<String, List<OffsetRange>> folds(int inputSize, List<AsciidoctorToken> tokens) {
        Map<String, List<OffsetRange>> result = new HashMap<>();

        Deque<AsciidoctorToken> parents = new ArrayDeque<>();

        tokens.forEach((token) -> {
            AsciidoctorTokenId id = token.getId();
            String foldGroup = id.tryGetFoldGroup();
            if (foldGroup != null) {
                if (id.isTableOfContentToken()) {
                    while (true) {
                        AsciidoctorToken parent = parents.peekFirst();
                        if (parent == null || parent.getId().getLevel() < id.getLevel()) {
                            break;
                        }

                        parents.pop();
                        addFold(parent, parent.getEndIndex(), token.getStartIndex(), result);
                    }

                    parents.push(token);
                }
                else {
                    getKeyList(result, foldGroup).add(new OffsetRange(token.getStartIndex(), token.getEndIndex()));
                }
            }
        });

        while (!parents.isEmpty()) {
            AsciidoctorToken token = parents.pop();
            addFold(token, token.getEndIndex(), inputSize, result);
        }

        return result;
    }

    private static void addFold(
            AsciidoctorToken token,
            int startIndex,
            int endIndex,
            Map<String, List<OffsetRange>> result) {
        addFold(token.getId(), startIndex, endIndex, result);
    }

    private static void addFold(
            AsciidoctorTokenId id,
            int startIndex,
            int endIndex,
            Map<String, List<OffsetRange>> result) {

        if (startIndex >= endIndex) {
            return;
        }

        String foldGroup = id.tryGetFoldGroup();
        if (foldGroup == null) {
            return;
        }

        getKeyList(result, foldGroup).add(new OffsetRange(startIndex, endIndex));
    }

    @Override
    public Configuration getConfiguration() {
        return new Configuration(false, false);
    }

    private static final class HierarchicalStructureItem implements StructureItem {
        private final String name;
        private final AsciidoctorToken token;
        private final List<StructureItem> children;
        private final List<StructureItem> childrenView;

        private long endPosition;

        public HierarchicalStructureItem(CharSequence input, AsciidoctorToken token) {
            this.name = token.getName(input);
            this.token = token;
            this.children = new ArrayList<>();
            this.childrenView = Collections.unmodifiableList(this.children);

            this.endPosition = input.length() - 1;
        }

        private void setEndPosition(long endPosition) {
            this.endPosition = endPosition;
        }

        private void addChild(StructureItem child) {
            children.add(child);
        }

        public int getLevel() {
            return token.getId().getLevel();
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
            return childrenView;
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
