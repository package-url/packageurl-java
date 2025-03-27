/*
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.packageurl.internal;

import static java.lang.Byte.toUnsignedInt;

import com.github.packageurl.ValidationException;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;
import org.jspecify.annotations.NonNull;

/**
 * String utility for validation and encoding.
 *
 * @since 2.0.0
 */
public final class StringUtil {

    private static final byte PERCENT_CHAR = '%';

    private static final boolean[] UNRESERVED_CHARS = new boolean[128];

    static {
        for (char c = '0'; c <= '9'; c++) {
            UNRESERVED_CHARS[c] = true;
        }
        for (char c = 'A'; c <= 'Z'; c++) {
            UNRESERVED_CHARS[c] = true;
        }
        for (char c = 'a'; c <= 'z'; c++) {
            UNRESERVED_CHARS[c] = true;
        }
        UNRESERVED_CHARS['-'] = true;
        UNRESERVED_CHARS['.'] = true;
        UNRESERVED_CHARS['_'] = true;
        UNRESERVED_CHARS['~'] = true;
    }

    private static final int NBITS = 128;

    private static final BitSet WHITESPACECHAR = new BitSet(NBITS);

    static {
        WHITESPACECHAR.set(0x09);
        WHITESPACECHAR.set(0x0A);
        WHITESPACECHAR.set(0x0B);
        WHITESPACECHAR.set(0x0C);
        WHITESPACECHAR.set(0x0D);
        WHITESPACECHAR.set(' ');
    }

    private StringUtil() {
        throw new AssertionError("Cannot instantiate StringUtil");
    }

    /**
     * Returns the lower case version of the string.
     *
     * @param s the string to convert to lower case
     * @return the lower case version of the string
     *
     * @since 2.0.0
     */
    public static @NonNull String toLowerCase(@NonNull String s) {
        int pos = indexOfFirstUpperCaseChar(s);

        if (pos == -1) {
            return s;
        }

        char[] chars = s.toCharArray();

        for (int length = chars.length; pos < length; pos++) {
            chars[pos] = (char) toLowerCase(chars[pos]);
        }

        return new String(chars);
    }

    /**
     * Percent decodes the given string.
     *
     * @param source the string to decode
     * @return the percent decoded string
     *
     * @since 2.0.0
     */
    public static @NonNull String percentDecode(@NonNull final String source) {
        if (source.indexOf(PERCENT_CHAR) == -1) {
            return source;
        }

        byte[] bytes = source.getBytes(StandardCharsets.UTF_8);

        int readPos = indexOfFirstPercentChar(bytes);
        int writePos = readPos;
        int length = bytes.length;
        while (readPos < length) {
            byte b = bytes[readPos];
            if (b == PERCENT_CHAR) {
                bytes[writePos++] = percentDecode(bytes, readPos++);
                readPos += 2;
            } else {
                bytes[writePos++] = bytes[readPos++];
            }
        }

        return new String(bytes, 0, writePos, StandardCharsets.UTF_8);
    }

    /**
     * Percent encodes the given string.
     *
     * @param source the string to encode
     * @return the percent encoded string
     *
     * @since 2.0.0
     */
    public static @NonNull String percentEncode(@NonNull final String source) {
        if (!shouldEncode(source)) {
            return source;
        }

        byte[] src = source.getBytes(StandardCharsets.UTF_8);
        byte[] dest = new byte[3 * src.length];

        int writePos = 0;
        for (byte b : src) {
            if (shouldEncode(toUnsignedInt(b))) {
                dest[writePos++] = PERCENT_CHAR;
                dest[writePos++] = toHexDigit(b >> 4);
                dest[writePos++] = toHexDigit(b);
            } else {
                dest[writePos++] = b;
            }
        }

        return new String(dest, 0, writePos, StandardCharsets.UTF_8);
    }

    /**
     * Determines if the character is a digit.
     *
     * @param c the character to check
     * @return true if the character is a digit; otherwise, false
     *
     * @since 2.0.0
     */
    public static boolean isDigit(int c) {
        return (c >= '0' && c <= '9');
    }

    /**
     * Determines if the character is valid for the package-url type.
     *
     * @param c the character to check
     * @return true if the character is valid for the package-url type; otherwise, false
     *
     * @since 2.0.0
     */
    public static boolean isValidCharForType(int c) {
        return (isAlphaNumeric(c) || c == '.' || c == '+' || c == '-');
    }

    /**
     * Determines if the character is valid for the package-url qualifier key.
     *
     * @param c the character to check
     * @return true if the character is valid for the package-url qualifier key; otherwise, false
     *
     * @since 2.0.0
     */
    public static boolean isValidCharForKey(int c) {
        return (isAlphaNumeric(c) || c == '.' || c == '_' || c == '-');
    }

    private static byte toHexDigit(int b) {
        return (byte) Character.toUpperCase(Character.forDigit(b & 0xF, 16));
    }

    /**
     * Returns {@code true} if the character is in the unreserved RFC 3986 set.
     * <p>
     *     <strong>Warning</strong>: Profiling shows that the performance of {@link #percentEncode} relies heavily on this method.
     *     Modify with care.
     * </p>
     * @param c non-negative integer.
     */
    private static boolean isUnreserved(int c) {
        return c < 128 && UNRESERVED_CHARS[c];
    }

    /**
     * @param c non-negative integer
     */
    private static boolean shouldEncode(int c) {
        return !isUnreserved(c);
    }

    private static boolean shouldEncode(String s) {
        for (int i = 0, length = s.length(); i < length; i++) {
            if (shouldEncode(s.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAlpha(int c) {
        return (isLowerCase(c) || isUpperCase(c));
    }

    private static boolean isAlphaNumeric(int c) {
        return (isDigit(c) || isAlpha(c));
    }

    private static boolean isUpperCase(int c) {
        return 'A' <= c && c <= 'Z';
    }

    private static boolean isLowerCase(int c) {
        return (c >= 'a' && c <= 'z');
    }

    public static boolean isWhitespace(int c) {
        if (c < 0 || c >= NBITS) {
            return false;
        }

        return WHITESPACECHAR.get(c);
    }

    private static int toLowerCase(int c) {
        return isUpperCase(c) ? (c ^ 0x20) : c;
    }

    private static int indexOfFirstUpperCaseChar(String s) {
        for (int i = 0, length = s.length(); i < length; i++) {
            if (isUpperCase(s.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    private static int indexOfFirstPercentChar(final byte[] bytes) {
        for (int i = 0, length = bytes.length; i < length; i++) {
            if (bytes[i] == PERCENT_CHAR) {
                return i;
            }
        }
        return -1;
    }

    private static byte percentDecode(final byte[] bytes, final int start) {
        if (start + 2 >= bytes.length) {
            throw new ValidationException("Incomplete percent encoding at offset " + start + " with value '"
                    + new String(bytes, start, bytes.length - start, StandardCharsets.UTF_8) + "'");
        }

        int pos1 = start + 1;
        byte b1 = bytes[pos1];
        int c1 = Character.digit(b1, 16);

        if (c1 == -1) {
            throw new ValidationException(
                    "Invalid percent encoding char 1 at offset " + pos1 + " with value '" + ((char) b1) + "'");
        }

        int pos2 = pos1 + 1;
        byte b2 = bytes[pos2];
        int c2 = Character.digit(bytes[pos2], 16);

        if (c2 == -1) {
            throw new ValidationException(
                    "Invalid percent encoding char 2 at offset " + pos2 + " with value '" + ((char) b2) + "'");
        }

        return ((byte) ((c1 << 4) + c2));
    }
}
