package org.netbeans.asciidoc.highlighter;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import org.jtrim.utils.ExceptionHelper;
import org.netbeans.asciidoc.util.PeekableIterator;
import org.netbeans.asciidoc.util.SimpleCharacterStream;
import org.netbeans.asciidoc.util.StringSplitters;

public final class AsciidoctorTokenizer {
    private static final BlockParser[] BLOCK_PARSERS = new BlockParser[]{
        new GenericBlockParser(AsciidoctorTokenId.CODE_BLOCK, '-', 4, false),
        new GenericBlockParser(AsciidoctorTokenId.TEXT_BLOCK, '/', 4, false),
        new GenericBlockParser(AsciidoctorTokenId.TEXT_BLOCK, '+', 4, false),
        new GenericBlockParser(AsciidoctorTokenId.TEXT_BLOCK, '.', 4, false),
        new GenericBlockParser(AsciidoctorTokenId.TEXT_BLOCK, '_', 4, false),
        new GenericBlockParser(AsciidoctorTokenId.TEXT_BLOCK, '=', 4, false),
        new GenericBlockParser(AsciidoctorTokenId.TEXT_BLOCK, '-', 2, false)
    };

    private static final UnderlinedHeaderDef[] UNDERLINED_HEADER_DEFS = new UnderlinedHeaderDef[]{
        new UnderlinedHeaderDef(AsciidoctorTokenId.HEADER1, '='),
        new UnderlinedHeaderDef(AsciidoctorTokenId.HEADER2, '-'),
        new UnderlinedHeaderDef(AsciidoctorTokenId.HEADER3, '~'),
        new UnderlinedHeaderDef(AsciidoctorTokenId.HEADER4, '^'),
        new UnderlinedHeaderDef(AsciidoctorTokenId.HEADER5, '+')
    };

    private static InProgressToken tryStartBlock(String line, int offset) {
        for (BlockParser parser: BLOCK_PARSERS) {
            InProgressToken newBlock = parser.tryStartBlockToken(line, offset);
            if (newBlock != null) {
                return newBlock;
            }
        }
        return null;
    }

    public List<AsciidoctorToken> readTokens(SimpleCharacterStream input) {
        List<AsciidoctorToken> result = new ArrayList<>(128);

        Deque<InProgressToken> tokenQueue = new ArrayDeque<>();
        InProgressToken root = new InProgressToken(AsciidoctorTokenId.OTHER, 0, Integer.MAX_VALUE);
        tokenQueue.push(root);

        int nextOffset = 0;
        PeekableIterator<String> lines = StringSplitters.splitByChar('\n', input);
        while (lines.hasNext()) {
            int offset = nextOffset;
            String line = lines.next();
            nextOffset = offset + line.length() + 1;

            InProgressToken parent = tokenQueue.peekFirst();
            if (parent.isClosingLine(line)) {
                tokenQueue.pop();
                parent.setEndIndex(offset + line.length());

                addToken(parent.tryGetRemainingToken(), tokenQueue, result);
                continue;
            }

            if (parent.isAllowNestedBlocks()) {
                AsciidoctorTokenId headerTokenId = tryGetUnderlinedHeaderId(line, lines.tryPeekNext());
                if (headerTokenId != null) {
                    String nextLine = lines.next();
                    nextOffset += nextLine.length() + 1;
                    addToken(new AsciidoctorToken(headerTokenId, offset, nextOffset - 1), tokenQueue, result);
                    continue;
                }

                InProgressToken newBlock = tryStartBlock(line, offset);
                if (newBlock != null) {
                    if (!parent.isAllowNestedBlocks()) {
                        newBlock.setAllowNestedBlocks(false);
                    }

                    tokenQueue.push(newBlock);
                    continue;
                }
            }

            AsciidoctorTokenId headerTokenId = tryGetHeaderTokenId(line, '=');
            if (headerTokenId != null) {
                addToken(new AsciidoctorToken(headerTokenId, offset, offset + line.length()), tokenQueue, result);
            }
        }

        if (nextOffset == 0) {
            return Collections.emptyList();
        }

        int length = nextOffset - 1;

        while (!tokenQueue.isEmpty()) {
            InProgressToken token = tokenQueue.pop();
            token.setEndIndex(length);

            addToken(token.tryGetRemainingToken(), tokenQueue, result);
        }

        return result;
    }

    private static AsciidoctorTokenId tryGetUnderlinedHeaderId(String line, String nextLine) {
        for (UnderlinedHeaderDef def: UNDERLINED_HEADER_DEFS) {
            AsciidoctorTokenId result = def.tryGetHeaderId(line, nextLine);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    private static AsciidoctorTokenId tryGetHeaderTokenId(String line, char headerPrefix) {
        int headerIndex = countPrefixChars(line, headerPrefix);
        if (headerIndex <= 0) {
            return null;
        }

        if (headerIndex == line.trim().length()) {
            return null;
        }

        switch (headerIndex) {
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
        if (token == null || token.getLength() <= 0) {
            return;
        }

        InProgressToken parent = tokenQueue.peekFirst();
        if (parent != null) {
            AsciidoctorToken parentPartToken = parent.consume(token.getStartIndex(), token.getEndIndex());
            if (parentPartToken != null && parentPartToken.getLength() > 0) {
                result.add(parentPartToken);
            }
        }
        result.add(token);
    }

    private static final class UnderlinedHeaderDef {
        private static final int MAX_DIST = 2;

        private final AsciidoctorTokenId id;
        private final char underlineChar;

        public UnderlinedHeaderDef(AsciidoctorTokenId id, char underlineChar) {
            this.id = id;
            this.underlineChar = underlineChar;
        }

        public AsciidoctorTokenId tryGetHeaderId(String line, String nextLine) {
            if (nextLine == null) {
                return null;
            }

            int underlineLength = countPrefixChars(nextLine, underlineChar);
            if (underlineLength != nextLine.trim().length()) {
                return null;
            }

            int diff = Math.abs(underlineLength - line.length());
            if (diff <= MAX_DIST) {
                return id;
            }

            diff = Math.abs(underlineLength - trimRight(line).length());
            return diff <= MAX_DIST ? id : null;
        }

        private static String trimRight(String str) {
            for (int i = str.length() - 1; i >= 0; i--) {
                if (str.charAt(i) > ' ') {
                    return str.substring(0, i + 1);
                }
            }
            return "";
        }
    }

    private static final class GenericBlockParser implements BlockParser {
        private final AsciidoctorTokenId id;
        private final char blockGuardChar;
        private final int minGuardChars;
        private final boolean allowNestedBlocks;

        public GenericBlockParser(
                AsciidoctorTokenId id,
                char blockGuardChar,
                int minGuardChars,
                boolean allowNestedBlocks) {
            this.id = id;
            this.blockGuardChar = blockGuardChar;
            this.minGuardChars = minGuardChars;
            this.allowNestedBlocks = allowNestedBlocks;
        }

        @Override
        public InProgressToken tryStartBlockToken(String line, int startIndex) {
            if (!isGuardLine(line, blockGuardChar, minGuardChars)) {
                return null;
            }

            return new InProgressToken(id, startIndex, Integer.MAX_VALUE, allowNestedBlocks, (String closeLine) -> {
                return isGuardLine(closeLine, blockGuardChar, minGuardChars);
            });
        }

        private static boolean isGuardLine(String line, char blockGuardChar, int minGuardChars) {
            if (line.isEmpty()) {
                return false;
            }

            char firstCh = line.charAt(0);
            if (firstCh != blockGuardChar) {
                return false;
            }

            int guardCharCount = countPrefixChars(line, firstCh);
            if (guardCharCount < minGuardChars) {
                return false;
            }

            return line.trim().length() == guardCharCount;
        }
    }

    private interface BlockParser {
        public InProgressToken tryStartBlockToken(String line, int startIndex);
    }

    private interface BlockTokenCloser {
        public boolean isClosingLine(String line);
    }

    private static final class InProgressToken {
        private final AsciidoctorTokenId id;
        private int startIndex;
        private int endIndex;
        private boolean allowNestedBlocks;

        private final BlockTokenCloser blockCloser;

        public InProgressToken(AsciidoctorTokenId id, int startIndex, int endIndex) {
            this(id, startIndex, endIndex, true, null);
        }

        public InProgressToken(
                AsciidoctorTokenId id,
                int startIndex,
                int endIndex,
                boolean allowNestedBlocks,
                BlockTokenCloser blockCloser) {
            ExceptionHelper.checkArgumentInRange(endIndex, startIndex, Integer.MAX_VALUE, "endIndex");

            this.id = Objects.requireNonNull(id, "id");
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.allowNestedBlocks = allowNestedBlocks;
            this.blockCloser = blockCloser;
        }

        public boolean isClosingLine(String line) {
            return blockCloser != null
                    ? blockCloser.isClosingLine(line)
                    : false;
        }

        public boolean isAllowNestedBlocks() {
            return allowNestedBlocks;
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

        public void setAllowNestedBlocks(boolean allowNestedBlocks) {
            this.allowNestedBlocks = allowNestedBlocks;
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
