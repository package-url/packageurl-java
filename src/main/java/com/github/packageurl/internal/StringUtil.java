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

import com.github.packageurl.ValidationException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.stream.IntStream;

/**
 * String utility for validation and encoding.
 *
 * @since 2.0.0
 */
public final class StringUtil {

    private static final byte PERCENT_CHAR = '%';

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
    public static String toLowerCase(String s) {
        if (s == null) {
            return null;
        }

        int pos = indexOfFirstUpperCaseChar(s);

        if (pos == -1) {
            return s;
        }

        char[] chars = s.toCharArray();
        int length = chars.length;

        for (int i = pos; i < length; i++) {
            chars[i] = (char) toLowerCase(chars[i]);
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
    public static String percentDecode(final String source) {
        if (source == null || source.isEmpty()) {
            return source;
        }

        byte[] bytes = source.getBytes(StandardCharsets.UTF_8);
        int i = indexOfFirstPercentChar(bytes);

        if (i == -1) {
            return source;
        }

        int length = bytes.length;
        int writePos = i;
        while (i < length) {
            byte b = bytes[i];
            if (b == PERCENT_CHAR) {
                bytes[writePos++] = percentDecode(bytes, i++);
                i += 2;
            } else {
                bytes[writePos++] = bytes[i++];
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
    public static String percentEncode(final String source) {
        if (source == null || source.isEmpty()) {
            return source;
        }
        byte[] bytes = source.getBytes(StandardCharsets.UTF_8);
        int start = indexOfFirstNonAsciiChar(bytes);
        if (start == -1) {
            return source;
        }
        int length = bytes.length;
        ByteBuffer buffer = ByteBuffer.allocate(start + ((length - start) * 3));
        if (start != 0) {
            buffer.put(bytes, 0, start);
        }

        for (int i = start; i < length; i++) {
            byte b = bytes[i];
            if (shouldEncode(b)) {
                byte b1 = (byte) Character.toUpperCase(Character.forDigit((b >> 4) & 0xF, 16));
                byte b2 = (byte) Character.toUpperCase(Character.forDigit(b & 0xF, 16));
                buffer.put(PERCENT_CHAR);
                buffer.put(b1);
                buffer.put(b2);
            } else {
                buffer.put(b);
            }
        }

        return new String(buffer.array(), 0, buffer.position(), StandardCharsets.UTF_8);
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

    private static boolean isUnreserved(int c) {
        return (isValidCharForKey(c) || c == '~');
    }

    private static boolean shouldEncode(int c) {
        return !isUnreserved(c);
    }

    private static boolean isAlpha(int c) {
        return (isLowerCase(c) || isUpperCase(c));
    }

    private static boolean isAlphaNumeric(int c) {
        return (isDigit(c) || isAlpha(c));
    }

    private static boolean isUpperCase(int c) {
        return (c >= 'A' && c <= 'Z');
    }

    private static boolean isLowerCase(int c) {
        return (c >= 'a' && c <= 'z');
    }

    private static int toLowerCase(int c) {
        return isUpperCase(c) ? (c ^ 0x20) : c;
    }

    private static int indexOfFirstUpperCaseChar(String s) {
        int length = s.length();

        for (int i = 0; i < length; i++) {
            if (isUpperCase(s.charAt(i))) {
                return i;
            }
        }

        return -1;
    }

    private static int indexOfFirstNonAsciiChar(byte[] bytes) {
        int length = bytes.length;
        int start = -1;
        for (int i = 0; i < length; i++) {
            if (shouldEncode(bytes[i])) {
                start = i;
                break;
            }
        }
        return start;
    }

    private static int indexOfFirstPercentChar(final byte[] bytes) {
        return IntStream.range(0, bytes.length)
                .filter(i -> bytes[i] == PERCENT_CHAR)
                .findFirst()
                .orElse(-1);
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
