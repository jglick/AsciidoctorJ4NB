package org.netbeans.asciidoc.structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import javax.swing.text.StringContent;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.netbeans.junit.MockServices;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.api.StructureItem;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.api.Source;

import static org.junit.Assert.*;

public class AsciidoctorStructureScannerTest {
    @BeforeClass
    public static void setupTests() {
        MockServices.setServices(TestEnvironmentFactory.class);
    }

    @AfterClass
    public static void destroyTests() {
        MockServices.setServices();
    }

    @Test
    public void testEmptyStructure() throws Exception {
        testStructure((tokens, expectations) -> {
        });
    }

    @Test
    public void testTextOnly() throws Exception {
        testStructure((tokens, expectations) -> {
            tokens.addToken(AsciidoctorTokenId.PLAIN, "something bla bla");
        });
    }

    @Test
    public void testSingleTitle() throws Exception {
        testStructure((tokens, expectations) -> {
            AsciidoctorToken header = tokens.addToken(AsciidoctorTokenId.HEADER2, "== My Header");

            expectations.expectHeaderNode(header, "My Header", tokens.getInputSize() - 1);
        });
    }

    @Test
    public void testSimpleStructure() throws Exception {
        testStructure((tokens, expectations) -> {
            tokens.addToken(AsciidoctorTokenId.PLAIN, "something\n\n");
            AsciidoctorToken header11 = tokens.addToken(AsciidoctorTokenId.HEADER2, "== First header 2");
            tokens.addToken(AsciidoctorTokenId.PLAIN, "\n\nsection body line 1\nsection body line 2\n\n");
            AsciidoctorToken header12 = tokens.addToken(AsciidoctorTokenId.HEADER2, "== Second header 2");
            tokens.addToken(AsciidoctorTokenId.PLAIN, "\n\nsection body line 3\n\n");
            AsciidoctorToken header21 = tokens.addToken(AsciidoctorTokenId.HEADER3, "=== Third header 3");
            tokens.addToken(AsciidoctorTokenId.PLAIN, "\n\n");
            tokens.addToken(AsciidoctorTokenId.CODE_BLOCK, "----\nMy Test Code Block\n----");
            tokens.addToken(AsciidoctorTokenId.PLAIN, "\n\nfinal part\n");

            long endPos = tokens.getInputSize() - 1;

            expectations.expectHeaderNode(header11, "First header 2", header12.getStartIndex() - 1);
            expectations.expectHeaderNode(header12, "Second header 2", endPos, (level2, level2Exp) -> {
                level2Exp.expectHeaderNode(header21, "Third header 3", endPos);
            });
        });
    }

    @Test
    public void testSkipHeaderLevels() throws Exception {
        testStructure((tokens, expectations) -> {
            AsciidoctorToken header = tokens.addToken(AsciidoctorTokenId.HEADER1, "= Main Title");
            tokens.addToken(AsciidoctorTokenId.PLAIN, "\n\n");
            AsciidoctorToken header1 = tokens.addToken(AsciidoctorTokenId.HEADER2, "== 1. header 2");
            tokens.addToken(AsciidoctorTokenId.PLAIN, "\n\n");
            AsciidoctorToken header11 = tokens.addToken(AsciidoctorTokenId.HEADER3, "=== 1.1. header 3");
            tokens.addToken(AsciidoctorTokenId.PLAIN, "\n\n");
            AsciidoctorToken header111 = tokens.addToken(AsciidoctorTokenId.HEADER4, "==== 1.1.1. header 4");
            tokens.addToken(AsciidoctorTokenId.PLAIN, "\n\n");
            AsciidoctorToken header2 = tokens.addToken(AsciidoctorTokenId.HEADER2, "== 2. header 2");
            tokens.addToken(AsciidoctorTokenId.PLAIN, "\n\n");
            AsciidoctorToken header2111 = tokens.addToken(AsciidoctorTokenId.HEADER4, "==== 2.1.1. header 4");
            tokens.addToken(AsciidoctorTokenId.PLAIN, "\n\n");
            AsciidoctorToken header3 = tokens.addToken(AsciidoctorTokenId.HEADER2, "== 3. header2");
            tokens.addToken(AsciidoctorTokenId.PLAIN, "\n");

            long endPos = tokens.getInputSize() - 1;

            expectations.expectHeaderNode(header, "Main Title", endPos, (level1, level1Exp) -> {
                int header1End = header2.getStartIndex() - 1;

                level1Exp.expectHeaderNode(header1, "1. header 2", header1End, (level2, level2Exp) -> {
                    level2Exp.expectHeaderNode(header11, "1.1. header 3", header1End, (level3, level3Exp) -> {
                        level3Exp.expectHeaderNode(header111, "1.1.1. header 4", header1End);
                    });
                });

                int header2End = header3.getStartIndex() - 1;

                level1Exp.expectHeaderNode(header2, "2. header 2", header2End, (level2, level2Exp) -> {
                    level2Exp.expectHeaderNode(header2111, "2.1.1. header 4", header2End);
                });

                level1Exp.expectHeaderNode(header3, "3. header2", endPos);
            });
        });
    }

    @Test
    public void testSimpleFolds() throws Exception {
        testFolds((tokens, expectations) -> {
            tokens.addToken(AsciidoctorTokenId.PLAIN, "something\n\n");
            AsciidoctorToken header11 = tokens.addToken(AsciidoctorTokenId.HEADER2, "== First header 2");
            tokens.addToken(AsciidoctorTokenId.PLAIN, "\n\nsection body line 1\nsection body line 2\n\n");
            AsciidoctorToken header12 = tokens.addToken(AsciidoctorTokenId.HEADER2, "== Second header 2");
            tokens.addToken(AsciidoctorTokenId.PLAIN, "\n\nsection body line 3\n\n");
            AsciidoctorToken header21 = tokens.addToken(AsciidoctorTokenId.HEADER3, "=== Third header 3");
            tokens.addToken(AsciidoctorTokenId.PLAIN, "\n\n");
            AsciidoctorToken codeBlock = tokens.addToken(AsciidoctorTokenId.CODE_BLOCK, "----\nMy Test Code Block\n----");
            tokens.addToken(AsciidoctorTokenId.PLAIN, "\n\nfinal part\n");

            int endPos = tokens.getInputSize();

            expectations.addRange(AsciidoctorTokenId.HEADER2.tryGetFoldGroup(),
                    header11.getEndIndex(),
                    header12.getStartIndex());
            expectations.addRange(AsciidoctorTokenId.HEADER2.tryGetFoldGroup(),
                    header12.getEndIndex(),
                    endPos);
            expectations.addRange(AsciidoctorTokenId.HEADER3.tryGetFoldGroup(),
                    header21.getEndIndex(),
                    endPos);
            expectations.addRange(AsciidoctorTokenId.CODE_BLOCK.tryGetFoldGroup(),
                    codeBlock.getStartIndex(),
                    codeBlock.getEndIndex());
        });
    }

    @Test
    public void testFoldsWithSkipHeaderLevels() throws Exception {
        testFolds((tokens, expectations) -> {
            AsciidoctorToken header = tokens.addToken(AsciidoctorTokenId.HEADER1, "= Main Title");
            tokens.addToken(AsciidoctorTokenId.PLAIN, "\n\n");
            AsciidoctorToken header1 = tokens.addToken(AsciidoctorTokenId.HEADER2, "== 1. header 2");
            tokens.addToken(AsciidoctorTokenId.PLAIN, "\n\n");
            AsciidoctorToken header11 = tokens.addToken(AsciidoctorTokenId.HEADER3, "=== 1.1. header 3");
            tokens.addToken(AsciidoctorTokenId.PLAIN, "\n\n");
            AsciidoctorToken header111 = tokens.addToken(AsciidoctorTokenId.HEADER4, "==== 1.1.1. header 4");
            tokens.addToken(AsciidoctorTokenId.PLAIN, "\n\n");
            AsciidoctorToken header2 = tokens.addToken(AsciidoctorTokenId.HEADER2, "== 2. header 2");
            tokens.addToken(AsciidoctorTokenId.PLAIN, "\n\n");
            AsciidoctorToken header2111 = tokens.addToken(AsciidoctorTokenId.HEADER4, "==== 2.1.1. header 4");
            tokens.addToken(AsciidoctorTokenId.PLAIN, "\n\n");
            AsciidoctorToken header3 = tokens.addToken(AsciidoctorTokenId.HEADER2, "== 3. header2");
            tokens.addToken(AsciidoctorTokenId.PLAIN, "\n");

            int endPos = tokens.getInputSize();

            expectations.addRange(AsciidoctorTokenId.HEADER1.tryGetFoldGroup(),
                    header.getEndIndex(),
                    endPos);
            expectations.addRange(AsciidoctorTokenId.HEADER2.tryGetFoldGroup(),
                    header1.getEndIndex(),
                    header2.getStartIndex());
            expectations.addRange(AsciidoctorTokenId.HEADER3.tryGetFoldGroup(),
                    header11.getEndIndex(),
                    header2.getStartIndex());
            expectations.addRange(AsciidoctorTokenId.HEADER4.tryGetFoldGroup(),
                    header111.getEndIndex(),
                    header2.getStartIndex());
            expectations.addRange(AsciidoctorTokenId.HEADER2.tryGetFoldGroup(),
                    header2.getEndIndex(),
                    header3.getStartIndex());
            expectations.addRange(AsciidoctorTokenId.HEADER4.tryGetFoldGroup(),
                    header2111.getEndIndex(),
                    header3.getStartIndex());
            expectations.addRange(AsciidoctorTokenId.HEADER2.tryGetFoldGroup(),
                    header3.getEndIndex(),
                    endPos);
        });
    }

    private void testStructure(ScanTestSetup setup) throws Exception {
        TokenListBuilder tokensBuilder = new TokenListBuilder();
        StructureExpectations expectations = new StructureExpectations();

        setup.setupTest(tokensBuilder, expectations);

        AsciidoctorStructureScanner scanner = new AsciidoctorStructureScanner();

        List<? extends StructureItem> items = scanner.scan(tokensBuilder.getParserResult());
        expectations.verifyNodes(items);
    }

    private void testFolds(FoldsTestSetup setup) throws Exception {
        TokenListBuilder tokensBuilder = new TokenListBuilder();
        FoldExpectations expectations = new FoldExpectations();

        setup.setupTest(tokensBuilder, expectations);

        AsciidoctorStructureScanner scanner = new AsciidoctorStructureScanner();

        Map<String, List<OffsetRange>> folds = scanner.folds(tokensBuilder.getParserResult());
        expectations.verifyEquivalent(folds);
    }

    private static final class TokenListBuilder {
        private final List<AsciidoctorToken> tokens;
        private final StringBuilder currentInput;
        private int offset;

        public TokenListBuilder() {
            this.offset = 0;
            this.tokens = new ArrayList<>();
            this.currentInput = new StringBuilder(128);
        }

        public AsciidoctorToken addToken(AsciidoctorTokenId id, String content) {
            AsciidoctorToken token = new AsciidoctorToken(id, offset, offset + content.length());
            currentInput.append(content);
            tokens.add(token);
            offset += token.getLength();

            return token;
        }

        public int getInputSize() {
            return currentInput.length();
        }

        public String getAllInput() {
            return currentInput.toString();
        }

        public Snapshot getInputSnapshot() throws Exception {
            return createSnapshot(getAllInput());
        }

        private Snapshot createSnapshot(String content) throws Exception {
            StringContent docContent = new StringContent();
            docContent.insertString(0, content);
            Document document = new PlainDocument(docContent);
            document.putProperty("mimeType", AsciidoctorLanguageConfig.MIME_TYPE);

            Source source = Source.create(document);
            return source.createSnapshot();
        }

        public AsciidoctorParserResult getParserResult() throws Exception {
            return new AsciidoctorParserResult(getInputSnapshot(), getCurrentTokens());
        }

        public List<AsciidoctorToken> getCurrentTokens() {
            return Collections.unmodifiableList(new ArrayList<>(tokens));
        }
    }

    private interface FoldsTestSetup {
        public void setupTest(TokenListBuilder tokens, FoldExpectations expectations) throws Exception;
    }

    private static final class FoldExpectations {
        private final Map<String, Set<OffsetRange>> folds;

        public FoldExpectations() {
            this.folds = new LinkedHashMap<>();
        }

        public void addRange(String foldCode, int startOffset, int endOffset) {
            Set<OffsetRange> foldsOfCode = folds.computeIfAbsent(foldCode, (key) -> new LinkedHashSet<>());
            foldsOfCode.add(new OffsetRange(startOffset, endOffset));
        }

        public void verifyEquivalent(Map<String, List<OffsetRange>> received) {
            folds.forEach((foldCode, foldsOfCode) -> {
                List<OffsetRange> receivedFoldsOfCode = received.getOrDefault(foldCode, Collections.emptyList());
                assertEquals("folds of " + foldCode, foldsOfCode, new HashSet<>(receivedFoldsOfCode));
            });

            if (folds.size() != received.size()) {
                throw new AssertionError("Expected fold count: " + folds.size() + " but received " + received.size());
            }
        }
    }

    private static final class StructureExpectations {
        private final List<NodeExpectation> nodeVerifiers;

        public StructureExpectations() {
            this.nodeVerifiers = new ArrayList<>();
        }

        public void expectNode(NodeVerifier verifier) {
            nodeVerifiers.add(new NodeExpectation(verifier));
        }

        public void expectHeaderNode(AsciidoctorToken token, String content, long endPos) {
            expectHeaderNode(token, content, endPos, NodeVerifier.NO_OP);
        }

        public void expectHeaderNode(AsciidoctorToken token, String content, long endPos, NodeVerifier verifier) {
            expectNode((StructureItem node, StructureExpectations childExpectations) -> {
                assertEquals("node.name", content, node.getName());

                assertEquals("startOffset", token.getStartIndex(), node.getPosition());
                assertEquals("endOffset", endPos, node.getEndPosition());

                verifier.expectNode(node, childExpectations);
            });
        }

        public void verifyNodes(List<? extends StructureItem> nodes) throws Exception {
            int verifyCount = 0;

            for (StructureItem node: nodes) {
                try {
                    if (verifyCount >= nodeVerifiers.size()) {
                        throw new AssertionError("Expected " + nodeVerifiers.size() + " but received " + nodes.size());
                    }

                    NodeExpectation expectation = nodeVerifiers.get(verifyCount);
                    expectation.verifyNode(node);
                } catch (Throwable ex) {
                    throw new AssertionError("Failure for node: " + node.getName(), ex);
                }

                verifyCount++;
            }

            assertEquals("node count", nodeVerifiers.size(), verifyCount);
        }
    }

    private static final class NodeExpectation {
        private final NodeVerifier verifier;

        public NodeExpectation(NodeVerifier verifier) {
            this.verifier = verifier;
        }

        public void verifyNode(StructureItem node) throws Exception {
            StructureExpectations childExpectations = new StructureExpectations();
            verifier.expectNode(node, childExpectations);

            childExpectations.verifyNodes(node.getNestedItems());
        }
    }

    private interface NodeVerifier {
        public static final NodeVerifier NO_OP = (node, childExpectations) -> { };

        public void expectNode(StructureItem node, StructureExpectations childExpectations) throws Exception;
    }

    private interface ScanTestSetup {
        public void setupTest(TokenListBuilder tokens, StructureExpectations expectations) throws Exception;
    }
}
