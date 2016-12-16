package org.netbeans.asciidoc.structure;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.junit.Test;
import org.netbeans.asciidoc.util.ConstSimpleCharacterStream;

import static org.junit.Assert.*;

public class AsciidoctorTokenizerTest {
    @Test
    public void testEmptyInput() throws Exception {
        doTestWithInput("", (verifier) -> {
        });
    }

    @Test
    public void testSingleLineWithHeader() throws Exception {
        doTestWithInput("=== Test Header 3", (verifier) -> {
            verifier.verifyToken(AsciidoctorTokenId.HEADER3, "=== Test Header 3");
        });
    }

    @Test
    public void testSingleLineWithTextOnly() throws Exception {
        doTestWithInput("Custom Test Line", (verifier) -> {
            verifier.verifyToken(AsciidoctorTokenId.PLAIN, "Custom Test Line");
        });
    }

    @Test
    public void testSingleLineHeaders() throws Exception {
        doTest("test_one_line_headers.adoc", (verifier) -> {
            verifier.verifyToken(AsciidoctorTokenId.PLAIN, "something\n\n");
            verifier.verifyToken(AsciidoctorTokenId.HEADER2, "== First header 2");
            verifier.verifyToken(AsciidoctorTokenId.PLAIN, "\n\nsection body line 1\nsection body line 2\n\n");
            verifier.verifyToken(AsciidoctorTokenId.HEADER2, "== Second header 2");
            verifier.verifyToken(AsciidoctorTokenId.PLAIN, "\n\nsection body line 3\n\n");
            verifier.verifyToken(AsciidoctorTokenId.HEADER3, "=== Third header 3");
            verifier.verifyToken(AsciidoctorTokenId.PLAIN, "\n\n");
            verifier.verifyToken(AsciidoctorTokenId.CODE_BLOCK, "----\nMy Test Code Block\n----");
            verifier.verifyToken(AsciidoctorTokenId.PLAIN, "\n\nfinal part\n");
        });
    }

    @Test
    public void testWithMultipleLineBreaks() throws Exception {
        doTestWithInput("= Test Header 1\n== Test Header 2/1\nText\n\n\n\n== Test Header 2/2", (verifier) -> {
            verifier.verifyToken(AsciidoctorTokenId.HEADER1, "= Test Header 1");
            verifier.verifyToken(AsciidoctorTokenId.PLAIN, "\n");
            verifier.verifyToken(AsciidoctorTokenId.HEADER2, "== Test Header 2/1");
            verifier.verifyToken(AsciidoctorTokenId.PLAIN, "\nText\n\n\n\n");
            verifier.verifyToken(AsciidoctorTokenId.HEADER2, "== Test Header 2/2");
        });
    }

    @Test
    public void testTwoLinesHeaders() throws Exception {
        doTest("test_two_lines_headers.adoc", (verifier) -> {
            verifier.verifyToken(AsciidoctorTokenId.PLAIN, "something\n\n");
            verifier.verifyToken(AsciidoctorTokenId.HEADER2, "First header 2\n--------------");
            verifier.verifyToken(AsciidoctorTokenId.PLAIN, "\n\nsection body line 1\nsection body line 2\n\n");
            verifier.verifyToken(AsciidoctorTokenId.HEADER2, "Second header 2\n---------------");
            verifier.verifyToken(AsciidoctorTokenId.PLAIN, "\n\nsection body line 3\n\n");
            verifier.verifyToken(AsciidoctorTokenId.HEADER3, "Third header 3\n~~~~~~~~~~~~~~");
            verifier.verifyToken(AsciidoctorTokenId.PLAIN, "\n\n");
            verifier.verifyToken(AsciidoctorTokenId.CODE_BLOCK, "----\nMy Test Code Block\n----");
            verifier.verifyToken(AsciidoctorTokenId.PLAIN, "\n\nfinal part\n");
        });
    }

    @Test
    public void testSkipHeaderLevels() throws Exception {
        doTest("test_skip_header_levels.adoc", (verifier) -> {
            verifier.verifyToken(AsciidoctorTokenId.HEADER1, "= Main Title");
            verifier.verifyToken(AsciidoctorTokenId.PLAIN, "\n\n");
            verifier.verifyToken(AsciidoctorTokenId.HEADER2, "== 1. header 2");
            verifier.verifyToken(AsciidoctorTokenId.PLAIN, "\n\n");
            verifier.verifyToken(AsciidoctorTokenId.HEADER3, "=== 1.1. header 3");
            verifier.verifyToken(AsciidoctorTokenId.PLAIN, "\n\n");
            verifier.verifyToken(AsciidoctorTokenId.HEADER4, "==== 1.1.1. header 4");
            verifier.verifyToken(AsciidoctorTokenId.PLAIN, "\n\n");
            verifier.verifyToken(AsciidoctorTokenId.HEADER2, "== 2. header 2");
            verifier.verifyToken(AsciidoctorTokenId.PLAIN, "\n\n");
            verifier.verifyToken(AsciidoctorTokenId.HEADER4, "==== 2.1.1. header 4");
            verifier.verifyToken(AsciidoctorTokenId.PLAIN, "\n\n");
            verifier.verifyToken(AsciidoctorTokenId.HEADER2, "== 3. header2");
            verifier.verifyToken(AsciidoctorTokenId.PLAIN, "\n");
        });
    }

    private void doTest(String asciidocPath, TokenizerResultVerifier resultVerifier) throws Exception {
        String input = ResourceUtils.readResource(getClass(), asciidocPath);
        doTestWithInput(input, resultVerifier);
    }

    private void doTestWithInput(String input, TokenizerResultVerifier resultVerifier) throws Exception {
        AsciidoctorTokenizer tokenizer = new AsciidoctorTokenizer();

        List<AsciidoctorToken> tokens = tokenizer.readTokens(new ConstSimpleCharacterStream(input));
        TokenVerifier verifier = new TokenVerifier(input, tokens);

        resultVerifier.verify(verifier);

        verifier.verifyNoMoreTokens();
    }

    private static final class TokenVerifier {
        private final String input;
        private final List<AsciidoctorToken> originalTokens;
        private final List<AsciidoctorToken> tokens;

        public TokenVerifier(String input, List<AsciidoctorToken> tokens) {
            this.input = input;
            this.originalTokens = new ArrayList<>(tokens);
            this.tokens = new LinkedList<>(originalTokens);
        }

        public void verifyToken(AsciidoctorTokenId expectedId, String expectedContent) {
            if (tokens.isEmpty()) {
                throw new AssertionError("Expected " + expectedId + " but there was no more token.");
            }

            AsciidoctorToken token = tokens.remove(0);

            assertEquals(expectedId, token.getId());

            String tokenStr = input.substring(token.getStartIndex(), token.getEndIndex());
            assertEquals("tokenContent", expectedContent, tokenStr);
        }

        public void verifyNoMoreTokens() {
            assertTrue("No more tokens", tokens.isEmpty());

            verifyTokenContinuity(originalTokens, input.length());
        }

        private static void verifyTokenContinuity(List<AsciidoctorToken> tokens, int inputLength) {
            int expectedStart = 0;
            for (AsciidoctorToken token: tokens) {
                if (token.getStartIndex() != expectedStart) {
                    throw new AssertionError("Token " + token.getId()
                            + " was expected to start at " + expectedStart
                            + " but it starts at " + token.getStartIndex());
                }
                if (token.getLength() <= 0) {
                    throw new AssertionError("Token " + token.getId()
                            + " must have a greater than zero length: " + token.getLength());
                }
                expectedStart = token.getEndIndex();
            }

            if (expectedStart != inputLength) {
                throw new AssertionError("Tokens do not cover the whole stream."
                        + " Stream length: " + inputLength + ". Coverage: " + expectedStart);
            }
        }
    }

    private interface TokenizerResultVerifier {
        public void verify(TokenVerifier verifier) throws Exception;
    }
}
