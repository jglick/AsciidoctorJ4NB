package org.netbeans.asciidoc.util;

public final class RomanNumerals {
    private static final char[][] ROMAN_DIGITS_UPPER = {
        {'I', 'V', 'X'},
        {'X', 'L', 'C'},
        {'C', 'D', 'M'}
    };

    private static final char[][] ROMAN_DIGITS_LOWER = {
        {'i', 'v', 'x'},
        {'x', 'l', 'c'},
        {'c', 'd', 'm'}
    };

    public static int parseRoman(String str, int defaultValue) {
        return parseRoman(str, 0, str.length(), defaultValue);
    }

    public static int parseRoman(String str, int startOffset, int endOffset, int defaultValue) {
        int length = endOffset - startOffset;
        if (length <= 0) {
            return defaultValue;
        }

        int result = 0;

        int index = startOffset;
        while (index < endOffset) {
            char firstCh = Character.toUpperCase(str.charAt(index));
            int firstChCount = countUpperFirst(str, index + 1, endOffset, firstCh) + 1;
            index += firstChCount;

            int firstValue = getUpperDigitValue(firstCh, firstChCount);
            if (firstValue == 0) {
                return defaultValue;
            }

            int digit = firstValue;

            if (index < endOffset) {
                char nextCh = Character.toUpperCase(str.charAt(index));

                if (isAllowedToSubtractFrom(firstCh, nextCh)) {
                    index++;
                    digit = getUpperDigitValue(nextCh) - digit;
                }
            }

            result += digit;
        }

        return result;
    }

    private static int getUpperDigitValue(char ch, int count) {
        return getUpperDigitValue(ch) * count;
    }

    private static int getUpperDigitValue(char ch) {
        switch (ch) {
            case 'I':
                return 1;
            case 'V':
                return 5;
            case 'X':
                return 10;
            case 'L':
                return 50;
            case 'C':
                return 100;
            case 'D':
                return 500;
            case 'M':
                return 1000;
            default:
                return 0;
        }
    }

    private static boolean isAllowedToSubtractFrom(char baseCh, char nextCh) {
        switch (baseCh) {
            case 'I':
                return nextCh == 'V' || nextCh == 'X';
            case 'X':
                return nextCh == 'L' || nextCh == 'C';
            case 'C':
                return nextCh == 'D' || nextCh == 'M';
            default:
                return false;
        }
    }

    private static int countUpperFirst(String str, int startOffset, int endOffset, char ch) {
        int result = 0;
        for (int i = startOffset; i < endOffset; i++) {
            if (ch != Character.toUpperCase(str.charAt(i))) {
                break;
            }
            result++;
        }
        return result;
    }

    public static String tryConvertToRoman(int value, boolean upperCase) {
        if (value <= 0) {
            return null;
        }

        return toRomanPositive(value, upperCase ? ROMAN_DIGITS_UPPER : ROMAN_DIGITS_LOWER);
    }

    @SuppressWarnings("fallthrough")
    private static void appendReversedRomanChar(int digit, char[] digits, StringBuilder result) {
        switch (digit) {
            case 3:
                result.append(digits[0]);
                /* fall through */
            case 2:
                result.append(digits[0]);
                /* fall through */
            case 1:
                result.append(digits[0]);
                /* fall through */
            case 0:
                // Do nothing.
                break;
            case 8:
                result.append(digits[0]);
                /* fall through */
            case 7:
                result.append(digits[0]);
                /* fall through */
            case 6:
                result.append(digits[0]);
                /* fall through */
            case 5:
                result.append(digits[1]);
                break;
            case 4:
                result.append(digits[1]);
                result.append(digits[0]);
                break;
            case 9:
                result.append(digits[2]);
                result.append(digits[0]);
                break;
            default:
                throw new IllegalArgumentException(digit + " is not within [0, 9]");
        }
    }

    private static String toRomanPositive(int value, char[][] digits) {
        StringBuilder reversedResult = new StringBuilder(32);

        int digitIndex = 0;
        int rem = value;
        while (rem > 0) {
            int digit = rem % 10;
            rem = rem / 10;

            if (digitIndex >= 3) {
                if (digit > 3) {
                    return null;
                }
                for (int i = 0; i < digit; i++) {
                    reversedResult.append(digits[2][2]);
                }
                break;
            }

            char[] digitChars = digits[digitIndex];
            appendReversedRomanChar(digit, digitChars, reversedResult);

            digitIndex++;
        }

        reversedResult.reverse();
        return reversedResult.toString();
    }

    private RomanNumerals() {
        throw new AssertionError();
    }
}
