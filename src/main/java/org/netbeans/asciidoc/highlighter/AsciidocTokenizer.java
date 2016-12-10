package org.netbeans.asciidoc.highlighter;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import org.jtrim.utils.ExceptionHelper;

public final class AsciidocTokenizer {
    public Collection<AsciidoctorToken> readTokens(String input) {
        try {
            return readTokens0(input);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public Collection<AsciidoctorToken> readTokens0(String input) throws IOException {
        List<AsciidoctorToken> result = new ArrayList<>(128);

        Deque<InProgressToken> tokenQueue = new ArrayDeque<>();
        InProgressToken root = new InProgressToken(AsciidoctorTokenId.OTHER, 0, input.length());
        tokenQueue.push(root);

        int offset = 0;
        for (String line: input.split("\n")) {
            if (line.startsWith("=")) {
                AsciidoctorTokenId id = toHeaderTokenId(line);
                addToken(new AsciidoctorToken(id, offset, offset + line.length()), tokenQueue, result);
            }
            else if (line.startsWith("----")) {
                InProgressToken parent = tokenQueue.peekFirst();
                if (parent.id == AsciidoctorTokenId.CODE_BLOCK) {
                    tokenQueue.pop();
                    parent.setEndIndex(offset + line.length());

                    addToken(parent.tryGetRemainingToken(), tokenQueue, result);
                }
                else {
                    tokenQueue.push(new InProgressToken(AsciidoctorTokenId.CODE_BLOCK, offset, input.length()));
                }
            }

            offset += line.length() + 1;
        }

        while (!tokenQueue.isEmpty()) {
            InProgressToken token = tokenQueue.pop();
            addToken(token.tryGetRemainingToken(), tokenQueue, result);
        }

        return result;
    }

    private static AsciidoctorTokenId toHeaderTokenId(String line) {
        switch (countPrefixChars(line, '=')) {
            case 1:
                return AsciidoctorTokenId.HEADER1;
            case 2:
                return AsciidoctorTokenId.HEADER2;
            case 3:
                return AsciidoctorTokenId.HEADER3;
            case 4:
                return AsciidoctorTokenId.HEADER4;
            case 5:
                return AsciidoctorTokenId.HEADER5;
            default:
                return AsciidoctorTokenId.HEADER6;
        }
    }

    private static int countPrefixChars(String str, char ch) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) != ch) {
                break;
            }
            count++;
        }
        return count;
    }

    private static void addToken(
            AsciidoctorToken token,
            Deque<InProgressToken> tokenQueue,
            List<AsciidoctorToken> result) {
        if (token == null) {
            return;
        }

        InProgressToken parent = tokenQueue.peekFirst();
        if (parent != null) {
            AsciidoctorToken parentPartToken = parent.consume(token.getStartIndex(), token.getEndIndex());
            if (parentPartToken != null) {
                result.add(parentPartToken);
            }
        }
        result.add(token);
    }

    private static final class InProgressToken {
        private final AsciidoctorTokenId id;
        private int startIndex;
        private int endIndex;

        public InProgressToken(AsciidoctorTokenId id, int startIndex, int endIndex) {
            ExceptionHelper.checkArgumentInRange(endIndex, startIndex, Integer.MAX_VALUE, "endIndex");

            this.id = Objects.requireNonNull(id, "id");
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }

        public AsciidoctorToken tryGetRemainingToken() {
            if (startIndex >= endIndex) {
                return null;
            }

            return new AsciidoctorToken(id, startIndex, endIndex);
        }

        public void setEndIndex(int newEndIndex) {
            this.endIndex = newEndIndex;
        }

        public AsciidoctorToken consume(int from, int to) {
            if (to > startIndex) {
                AsciidoctorToken result = from >= startIndex
                        ? new AsciidoctorToken(id, startIndex, from)
                        : null;
                startIndex = to;
                return result;
            }
            else {
                return null;
            }
        }
    }
}
