package org.netbeans.asciidoc.highlighter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.junit.Test;
import org.netbeans.asciidoc.util.ConstSimpleCharacterStream;

import static org.junit.Assert.*;

public class AsciidocTokenizerTest {
    @Test
    public void testSingleLineHeaders() throws Exception {
        doTest("test_one_line_headers.adoc", (verifier) -> {
            verifier.verifyToken(AsciidoctorTokenId.OTHER, "something\n\n");
            verifier.verifyToken(AsciidoctorTokenId.HEADER2, "== First header 2");
            verifier.verifyToken(AsciidoctorTokenId.OTHER, "\n\nsection body line 1\nsection body line 2\n\n");
            verifier.verifyToken(AsciidoctorTokenId.HEADER2, "== Second header 2");
            verifier.verifyToken(AsciidoctorTokenId.OTHER, "\n\nsection body line 3\n\n");
            verifier.verifyToken(AsciidoctorTokenId.HEADER3, "=== Third header 3");
            verifier.verifyToken(AsciidoctorTokenId.OTHER, "\n\n");
            verifier.verifyToken(AsciidoctorTokenId.CODE_BLOCK, "----\nMy Test Code Block\n----");
            verifier.verifyToken(AsciidoctorTokenId.OTHER, "\n\nfinal part\n");
        });
    }

    @Test
    public void testTwoLinesHeaders() throws Exception {
        doTest("test_two_lines_headers.adoc", (verifier) -> {
            verifier.verifyToken(AsciidoctorTokenId.OTHER, "something\n\n");
            verifier.verifyToken(AsciidoctorTokenId.HEADER2, "First header 2\n--------------");
            verifier.verifyToken(AsciidoctorTokenId.OTHER, "\n\nsection body line 1\nsection body line 2\n\n");
            verifier.verifyToken(AsciidoctorTokenId.HEADER2, "Second header 2\n---------------");
            verifier.verifyToken(AsciidoctorTokenId.OTHER, "\n\nsection body line 3\n\n");
            verifier.verifyToken(AsciidoctorTokenId.HEADER3, "Third header 3\n~~~~~~~~~~~~~~");
            verifier.verifyToken(AsciidoctorTokenId.OTHER, "\n\n");
            verifier.verifyToken(AsciidoctorTokenId.CODE_BLOCK, "----\nMy Test Code Block\n----");
            verifier.verifyToken(AsciidoctorTokenId.OTHER, "\n\nfinal part\n");
        });
    }

    private void doTest(String asciidocPath, TokenizerResultVerifier resultVerifier) throws Exception {
        String input = ResourceUtils.readResource(getClass(), asciidocPath);
        AsciidocTokenizer tokenizer = new AsciidocTokenizer();

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
