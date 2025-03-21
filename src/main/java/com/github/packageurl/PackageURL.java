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
package com.github.packageurl;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.jspecify.annotations.Nullable;

/**
 * <p>Package-URL (aka purl) is a "mostly universal" URL to describe a package. A purl is a URL composed of seven components:</p>
 * <pre>
 * scheme:type/namespace/name@version?qualifiers#subpath
 * </pre>
 * <p>
 * Components are separated by a specific character for unambiguous parsing.
 * A purl must NOT contain a URL Authority, i.e., there is no support for username,
 * password, host and port components. A namespace segment may sometimes look
 * like a host, but its interpretation is specific to a type.
 * </p>
 * <p>SPEC: <a href="https://github.com/package-url/purl-spec">https://github.com/package-url/purl-spec</a></p>
 *
 * @author Steve Springett
 * @since 1.0.0
 */
public final class PackageURL implements Serializable {
    private static final long serialVersionUID = 3243226021636427586L;

    private static final char PERCENT_CHAR = '%';

    private static final int NBITS = 128;

    private static final BitSet DIGIT = new BitSet(NBITS);

    static {
        IntStream.rangeClosed('0', '9').forEach(DIGIT::set);
    }

    private static final BitSet LOWER = new BitSet(NBITS);

    static {
        IntStream.rangeClosed('a', 'z').forEach(LOWER::set);
    }

    private static final BitSet UPPER = new BitSet(NBITS);

    static {
        IntStream.rangeClosed('A', 'Z').forEach(UPPER::set);
    }

    private static final BitSet ALPHA = new BitSet(NBITS);

    static {
        ALPHA.or(LOWER);
        ALPHA.or(UPPER);
    }

    private static final BitSet ALPHA_DIGIT = new BitSet(NBITS);

    static {
        ALPHA_DIGIT.or(ALPHA);
        ALPHA_DIGIT.or(DIGIT);
    }

    private static final BitSet UNRESERVED = new BitSet(NBITS);

    static {
        UNRESERVED.or(ALPHA_DIGIT);
        UNRESERVED.set('-');
        UNRESERVED.set('.');
        UNRESERVED.set('_');
        UNRESERVED.set('~');
    }

    private static final BitSet GEN_DELIMS = new BitSet(NBITS);

    static {
        GEN_DELIMS.set(':');
        GEN_DELIMS.set('/');
        GEN_DELIMS.set('?');
        GEN_DELIMS.set('#');
        GEN_DELIMS.set('[');
        GEN_DELIMS.set(']');
        GEN_DELIMS.set('@');
    }

    private static final BitSet SUB_DELIMS = new BitSet(NBITS);

    static {
        SUB_DELIMS.set('!');
        SUB_DELIMS.set('$');
        SUB_DELIMS.set('&');
        SUB_DELIMS.set('\'');
        SUB_DELIMS.set('(');
        SUB_DELIMS.set(')');
        SUB_DELIMS.set('*');
        SUB_DELIMS.set('+');
        SUB_DELIMS.set(',');
        SUB_DELIMS.set(';');
        SUB_DELIMS.set('=');
    }

    private static final BitSet PCHAR = new BitSet(NBITS);

    static {
        PCHAR.or(UNRESERVED);
        PCHAR.or(SUB_DELIMS);
        PCHAR.set(':');
        PCHAR.clear('&'); // XXX: Why?
    }

    private static final BitSet QUERY = new BitSet(NBITS);

    static {
        QUERY.or(GEN_DELIMS);
        QUERY.or(PCHAR);
        QUERY.set('/');
        QUERY.set('?');
        QUERY.clear('#');
        QUERY.clear('&');
        QUERY.clear('=');
    }

    private static final BitSet FRAGMENT = new BitSet(NBITS);

    static {
        FRAGMENT.or(GEN_DELIMS);
        FRAGMENT.or(PCHAR);
        FRAGMENT.set('/');
        FRAGMENT.set('?');
        FRAGMENT.set('&');
        FRAGMENT.clear('#');
    }

    /**
     * The PackageURL scheme constant
     */
    public static final String SCHEME = "pkg";

    /**
     * The PackageURL scheme ({@code "pkg"}) constant followed by a colon ({@code ':'}).
     */
    private static final String SCHEME_PART = SCHEME + ':';

    /**
     * The package "type" or package "protocol" such as maven, npm, nuget, gem, pypi, etc.
     * Required.
     */
    private String type;

    /**
     * The name prefix such as a Maven groupid, a Docker image owner, a GitHub user or organization.
     * Optional and type-specific.
     */
    private @Nullable String namespace;

    /**
     * The name of the package.
     * Required.
     */
    private String name;

    /**
     * The version of the package.
     * Optional.
     */
    private @Nullable String version;

    /**
     * Extra qualifying data for a package such as an OS, architecture, a distro, etc.
     * Optional and type-specific.
     */
    private @Nullable Map<String, String> qualifiers;

    /**
     * Extra subpath within a package, relative to the package root.
     * Optional.
     */
    private @Nullable String subpath;

    /**
     * Constructs a new PackageURL object by parsing the specified string.
     *
     * @param purl a valid package URL string to parse
     * @throws MalformedPackageURLException if parsing fails
     * @throws NullPointerException if {@code purl} is {@code null}
     */
    public PackageURL(final String purl) throws MalformedPackageURLException {
        parse(requireNonNull(purl, "purl"));
    }

    /**
     * Constructs a new PackageURL object by specifying only the required
     * parameters necessary to create a valid PackageURL.
     *
     * @param type the type of package (i.e. maven, npm, gem, etc)
     * @param name the name of the package
     * @throws MalformedPackageURLException if parsing fails
     */
    public PackageURL(final String type, final String name) throws MalformedPackageURLException {
        this(type, null, name, null, (Map<String, String>) null, null);
    }

    /**
     * Constructs a new PackageURL object.
     *
     * @param type the type of package (i.e., maven, npm, gem, etc.), not {@code null}
     * @param namespace  the name prefix (i.e., group, owner, organization)
     * @param name the name of the package, not {@code null}
     * @param version the version of the package
     * @param qualifiers an array of key/value pair qualifiers
     * @param subpath the subpath string
     * @throws MalformedPackageURLException if parsing fails
     * @throws NullPointerException if {@code type} or {@code name} are {@code null}
     * @deprecated use {@link #PackageURL(String, String, String, String, Map, String)} instead
     */
    @Deprecated
    public PackageURL(
            final String type,
            final @Nullable String namespace,
            final String name,
            final @Nullable String version,
            final @Nullable TreeMap<String, String> qualifiers,
            final @Nullable String subpath)
            throws MalformedPackageURLException {
        this(type, namespace, name, version, qualifiers != null ? (Map<String, String>) qualifiers : null, subpath);
    }

    /**
     * Constructs a new PackageURL object.
     *
     * @param type the type of package (i.e. maven, npm, gem, etc)
     * @param namespace the name prefix (i.e., group, owner, organization)
     * @param name the name of the package
     * @param version the version of the package
     * @param qualifiers an array of key/value pair qualifiers
     * @param subpath the subpath string
     * @throws MalformedPackageURLException if parsing fails
     * @throws NullPointerException if {@code type} or {@code name} are {@code null}
     * @since 1.6.0
     */
    public PackageURL(
            final String type,
            final @Nullable String namespace,
            final String name,
            final @Nullable String version,
            final @Nullable Map<String, String> qualifiers,
            final @Nullable String subpath)
            throws MalformedPackageURLException {
        this.type = toLowerCase(validateType(requireNonNull(type, "type")));
        this.namespace = validateNamespace(this.type, namespace);
        this.name = validateName(this.type, requireNonNull(name, "name"));
        this.version = validateVersion(this.type, version);
        this.qualifiers = parseQualifiers(qualifiers);
        this.subpath = validateSubpath(subpath);
        verifyTypeConstraints(this.type, this.namespace, this.name);
    }

    /**
     * Converts this {@link PackageURL} to a {@link PackageURLBuilder}.
     *
     * @return the builder
     * @since 1.5.0
     * @deprecated use {@link PackageURLBuilder#aPackageURL(PackageURL)} or {@link PackageURLBuilder#aPackageURL(String)}
     */
    @Deprecated
    public PackageURLBuilder toBuilder() {
        return PackageURLBuilder.aPackageURL(this);
    }

    /**
     * Returns the package url scheme.
     *
     * @return the scheme
     */
    public String getScheme() {
        return SCHEME;
    }

    /**
     * Returns the package "type" or package "protocol" such as maven, npm, nuget, gem, pypi, etc.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the name prefix such as a Maven groupId, a Docker image owner, a GitHub user or organization.
     *
     * @return the namespace
     */
    public @Nullable String getNamespace() {
        return namespace;
    }

    /**
     * Returns the name of the package.
     *
     * @return the name of the package
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the version of the package.
     *
     * @return the version of the package
     */
    public @Nullable String getVersion() {
        return version;
    }

    /**
     * Returns extra qualifying data for a package such as an OS, architecture, a distro, etc.
     * This method returns an UnmodifiableMap.
     *
     * @return all the qualifiers, or an empty map if none are set
     */
    public Map<String, String> getQualifiers() {
        return qualifiers != null ? Collections.unmodifiableMap(qualifiers) : Collections.emptyMap();
    }

    /**
     * Returns extra subpath within a package, relative to the package root.
     *
     * @return the subpath
     */
    public @Nullable String getSubpath() {
        return subpath;
    }

    private static void validateScheme(final String value) throws MalformedPackageURLException {
        if (!SCHEME.equals(value)) {
            throw new MalformedPackageURLException(
                    "The PackageURL scheme '" + value + "' is invalid. It should be '" + SCHEME + "'");
        }
    }

    private static String validateType(final String value) throws MalformedPackageURLException {
        if (value.isEmpty()) {
            throw new MalformedPackageURLException("The PackageURL type cannot be empty");
        }

        validateChars(value, PackageURL::isValidCharForType, "type");

        return value;
    }

    private static boolean isValidCharForType(int c) {
        return (isAlphaNumeric(c) || c == '.' || c == '+' || c == '-');
    }

    private static boolean isValidCharForKey(int c) {
        return (isAlphaNumeric(c) || c == '.' || c == '_' || c == '-');
    }

    private static void validateChars(String value, IntPredicate predicate, String component)
            throws MalformedPackageURLException {
        char firstChar = value.charAt(0);

        if (isDigit(firstChar)) {
            throw new MalformedPackageURLException(
                    "The PackageURL " + component + " cannot start with a number: " + firstChar);
        }

        String invalidChars = value.chars()
                .filter(predicate.negate())
                .mapToObj(c -> String.valueOf((char) c))
                .collect(Collectors.joining(", "));

        if (!invalidChars.isEmpty()) {
            throw new MalformedPackageURLException(
                    "The PackageURL " + component + " '" + value + "' contains invalid characters: " + invalidChars);
        }
    }

    private static @Nullable String validateNamespace(final String type, final @Nullable String value)
            throws MalformedPackageURLException {
        if (isEmpty(value)) {
            return null;
        }
        return validateNamespace(type, value.split("/"));
    }

    private static @Nullable String validateNamespace(final String type, final String[] values)
            throws MalformedPackageURLException {
        if (values.length == 0) {
            return null;
        }
        final String tempNamespace = validatePath(values, false);
        final String retVal;
        switch (type) {
            case StandardTypes.APK:
            case StandardTypes.BITBUCKET:
            case StandardTypes.COMPOSER:
            case StandardTypes.DEB:
            case StandardTypes.GITHUB:
            case StandardTypes.GOLANG:
            case StandardTypes.HEX:
            case StandardTypes.LUAROCKS:
            case StandardTypes.QPKG:
            case StandardTypes.RPM:
                retVal = tempNamespace != null ? toLowerCase(tempNamespace) : null;
                break;
            case StandardTypes.MLFLOW:
            case StandardTypes.OCI:
                if (tempNamespace != null) {
                    throw new MalformedPackageURLException(
                            "The PackageURL specified contains a namespace which is not allowed for type: " + type);
                }
                retVal = null;
                break;
            default:
                retVal = tempNamespace;
                break;
        }
        return retVal;
    }

    private static String validateName(final String type, final String value) throws MalformedPackageURLException {
        if (value.isEmpty()) {
            throw new MalformedPackageURLException("The PackageURL name specified is invalid");
        }
        String temp;
        switch (type) {
            case StandardTypes.APK:
            case StandardTypes.BITBUCKET:
            case StandardTypes.BITNAMI:
            case StandardTypes.COMPOSER:
            case StandardTypes.DEB:
            case StandardTypes.GITHUB:
            case StandardTypes.GOLANG:
            case StandardTypes.HEX:
            case StandardTypes.LUAROCKS:
            case StandardTypes.OCI:
                temp = toLowerCase(value);
                break;
            case StandardTypes.PUB:
                temp = toLowerCase(value).replaceAll("[^a-z0-9_]", "_");
                break;
            case StandardTypes.PYPI:
                temp = toLowerCase(value).replace('_', '-');
                break;
            default:
                temp = value;
                break;
        }
        return temp;
    }

    private static @Nullable String validateVersion(final String type, final @Nullable String value) {
        if (value == null) {
            return null;
        }

        switch (type) {
            case StandardTypes.HUGGINGFACE:
            case StandardTypes.LUAROCKS:
            case StandardTypes.OCI:
                return toLowerCase(value);
            default:
                return value;
        }
    }

    private static @Nullable Map<String, String> validateQualifiers(final @Nullable Map<String, String> values)
            throws MalformedPackageURLException {
        if (values == null || values.isEmpty()) {
            return null;
        }

        for (Map.Entry<String, String> entry : values.entrySet()) {
            String key = entry.getKey();
            validateKey(key);
            validateValue(key, entry.getValue());
        }

        return values;
    }

    private static void validateKey(final @Nullable String value) throws MalformedPackageURLException {
        if (isEmpty(value)) {
            throw new MalformedPackageURLException("Qualifier key is invalid: " + value);
        }

        validateChars(value, PackageURL::isValidCharForKey, "qualifier key");
    }

    private static void validateValue(final String key, final @Nullable String value)
            throws MalformedPackageURLException {
        if (isEmpty(value)) {
            throw new MalformedPackageURLException(
                    "The specified PackageURL contains an empty or null qualifier value for key " + key);
        }
    }

    private static @Nullable String validateSubpath(final @Nullable String value) throws MalformedPackageURLException {
        if (isEmpty(value)) {
            return null;
        }
        return validatePath(value.split("/"), true);
    }

    private static @Nullable String validatePath(final String[] segments, final boolean isSubPath)
            throws MalformedPackageURLException {
        if (segments.length == 0) {
            return null;
        }
        try {
            return Arrays.stream(segments)
                    .peek(segment -> {
                        if (isSubPath && ("..".equals(segment) || ".".equals(segment))) {
                            throw new ValidationException(
                                    "Segments in the subpath may not be a period ('.') or repeated period ('..')");
                        } else if (segment.contains("/")) {
                            throw new ValidationException(
                                    "Segments in the namespace and subpath may not contain a forward slash ('/')");
                        } else if (segment.isEmpty()) {
                            throw new ValidationException("Segments in the namespace and subpath may not be empty");
                        }
                    })
                    .collect(Collectors.joining("/"));
        } catch (ValidationException e) {
            throw new MalformedPackageURLException(e);
        }
    }

    /**
     * Returns the canonicalized representation of the purl.
     *
     * @return the canonicalized representation of the purl
     * @since 1.1.0
     */
    @Override
    public String toString() {
        return canonicalize();
    }

    /**
     * Returns the canonicalized representation of the purl.
     *
     * @return the canonicalized representation of the purl
     */
    public String canonicalize() {
        return canonicalize(false);
    }

    /**
     * Returns the canonicalized representation of the purl.
     *
     * @return the canonicalized representation of the purl
     * @since 1.3.2
     */
    private String canonicalize(boolean coordinatesOnly) {
        final StringBuilder purl = new StringBuilder();
        purl.append(SCHEME_PART).append(type).append('/');
        if (namespace != null) {
            purl.append(encodePath(namespace, PCHAR));
            purl.append('/');
        }
        purl.append(percentEncode(name, PCHAR));
        if (version != null) {
            purl.append('@').append(percentEncode(version, PCHAR));
        }

        if (!coordinatesOnly) {
            if (qualifiers != null) {
                purl.append('?');
                Set<Map.Entry<String, String>> entries = qualifiers.entrySet();
                boolean separator = false;
                for (Map.Entry<String, String> entry : entries) {
                    if (separator) {
                        purl.append('&');
                    }
                    purl.append(entry.getKey());
                    purl.append('=');
                    purl.append(percentEncode(entry.getValue(), QUERY));
                    separator = true;
                }
            }
            if (subpath != null) {
                purl.append('#').append(encodePath(subpath, FRAGMENT));
            }
        }
        return purl.toString();
    }

    private static boolean isUnreserved(int c, BitSet safe) {
        if (c < 0 || c >= NBITS) {
            return false;
        }

        return safe.get(c);
    }

    private static boolean shouldEncode(int c, BitSet safe) {
        return !isUnreserved(c, safe);
    }

    private static boolean isAlpha(int c) {
        return (isLowerCase(c) || isUpperCase(c));
    }

    private static boolean isDigit(int c) {
        return (c >= '0' && c <= '9');
    }

    private static boolean isAlphaNumeric(int c) {
        return (isDigit(c) || isAlpha(c));
    }

    private static boolean isUpperCase(int c) {
        return (c >= 'A' && c <= 'Z');
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

    private static boolean isLowerCase(int c) {
        return (c >= 'a' && c <= 'z');
    }

    private static int toLowerCase(int c) {
        return isUpperCase(c) ? (c ^ 0x20) : c;
    }

    private static String toLowerCase(String s) {
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

    private static int indexOfFirstPercentChar(final byte[] bytes) {
        return IntStream.range(0, bytes.length)
                .filter(i -> isPercent(bytes[i]))
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

    static String percentDecode(final String source) {
        if (source.isEmpty()) {
            return source;
        }

        byte[] bytes = source.getBytes(StandardCharsets.UTF_8);
        int i = indexOfFirstPercentChar(bytes);

        if (i == -1) {
            return source;
        }

        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.position(i);
        int length = buffer.capacity();

        while (i < length) {
            byte b = bytes[i];

            if (isPercent(b)) {
                buffer.put(percentDecode(bytes, i));
                i += 2;
            } else {
                buffer.put(b);
            }

            i++;
        }

        return new String(buffer.array(), 0, buffer.position(), StandardCharsets.UTF_8);
    }

    /**
     * URI decodes the given string.
     *
     * @param source the encoded string
     * @return the decoded string
     * @since 1.4.2
     * @deprecated this method was made public in error in version 1.4.2 and will be removed without a replacement
     */
    @Deprecated
    public @Nullable String uriDecode(final @Nullable String source) {
        return source != null ? percentDecode(source) : null;
    }

    private static boolean isPercent(int c) {
        return (c == PERCENT_CHAR);
    }

    static String percentEncode(final String source) {
        return percentEncode(source, UNRESERVED);
    }

    private static String percentEncode(final String source, final BitSet safe) {
        if (source.isEmpty()) {
            return source;
        }

        byte[] bytes = source.getBytes(StandardCharsets.UTF_8);
        int length = bytes.length;
        ByteBuffer buffer = ByteBuffer.allocate(length * 3);
        boolean changed = false;

        for (byte b : bytes) {
            if (shouldEncode(b, safe)) {
                changed = true;
                byte b1 = (byte) Character.toUpperCase(Character.forDigit((b >> 4) & 0xF, 16));
                byte b2 = (byte) Character.toUpperCase(Character.forDigit(b & 0xF, 16));
                buffer.put((byte) PERCENT_CHAR);
                buffer.put(b1);
                buffer.put(b2);
            } else {
                buffer.put(b);
            }
        }

        return changed ? new String(buffer.array(), 0, buffer.position(), StandardCharsets.UTF_8) : source;
    }

    /**
     * Given a specified PackageURL, this method will parse the purl and populate this classes
     * instance fields so that the corresponding getters may be called to retrieve the individual
     * pieces of the purl.
     *
     * @param purl the purl string to parse
     * @throws MalformedPackageURLException if an exception occurs when parsing
     */
    private void parse(final String purl) throws MalformedPackageURLException {
        if (purl.isEmpty()) {
            throw new MalformedPackageURLException("Invalid purl: Is empty or null");
        }

        try {
            if (!purl.startsWith(SCHEME_PART)) {
                throw new MalformedPackageURLException(
                        "Invalid purl: " + purl + ". It does not start with '" + SCHEME_PART + "'");
            }

            final int length = purl.length();
            int start = SCHEME_PART.length();

            while (start < length && '/' == purl.charAt(start)) {
                start++;
            }

            final URI uri = new URI(String.join("/", SCHEME_PART, purl.substring(start)));

            validateScheme(uri.getScheme());

            // Check to ensure that none of these parts are parsed. If so, it's an invalid purl.
            if (uri.getRawAuthority() != null) {
                throw new MalformedPackageURLException("Invalid purl: A purl must NOT contain a URL Authority ");
            }

            // subpath is optional - check for existence
            final String rawFragment = uri.getRawFragment();
            if (rawFragment != null && !rawFragment.isEmpty()) {
                this.subpath = validatePath(parsePath(rawFragment, true), true);
            }
            // qualifiers are optional - check for existence
            final String rawQuery = uri.getRawQuery();
            if (rawQuery != null && !rawQuery.isEmpty()) {
                this.qualifiers = parseQualifiers(rawQuery);
            }
            // this is the rest of the purl that needs to be parsed
            String remainder = uri.getRawPath();
            // trim trailing '/'
            int end = remainder.length() - 1;
            while (end > 0 && '/' == remainder.charAt(end)) {
                end--;
            }
            remainder = remainder.substring(0, end + 1);
            // there is exactly one leading '/' at this point
            start = 1;
            // type
            int index = remainder.indexOf('/', start);
            if (index <= start) {
                throw new MalformedPackageURLException("Invalid purl: does not contain both a type and name");
            }
            this.type = toLowerCase(validateType(remainder.substring(start, index)));

            start = index + 1;

            // version is optional - check for existence
            index = remainder.lastIndexOf('@');
            if (index >= start) {
                this.version = validateVersion(this.type, percentDecode(remainder.substring(index + 1)));
                remainder = remainder.substring(0, index);
            }

            // The 'remainder' should now consist of an optional namespace and the name
            index = remainder.lastIndexOf('/');
            if (index <= start) {
                this.name = validateName(this.type, percentDecode(remainder.substring(start)));
            } else {
                this.name = validateName(this.type, percentDecode(remainder.substring(index + 1)));
                remainder = remainder.substring(0, index);
                this.namespace = validateNamespace(this.type, parsePath(remainder.substring(start), false));
            }
            verifyTypeConstraints(this.type, this.namespace, this.name);
        } catch (URISyntaxException e) {
            throw new MalformedPackageURLException("Invalid purl: " + e.getMessage(), e);
        }
    }

    /**
     * Some purl types may have specific constraints. This method attempts to verify them.
     * @param type the purl type
     * @param namespace the purl namespace
     * @throws MalformedPackageURLException if constraints are not met
     */
    private static void verifyTypeConstraints(String type, @Nullable String namespace, @Nullable String name)
            throws MalformedPackageURLException {
        if (StandardTypes.MAVEN.equals(type)) {
            if (isEmpty(namespace) || isEmpty(name)) {
                throw new MalformedPackageURLException(
                        "The PackageURL specified is invalid. Maven requires both a namespace and name.");
            }
        }
    }

    private static @Nullable Map<String, String> parseQualifiers(final @Nullable Map<String, String> qualifiers)
            throws MalformedPackageURLException {
        if (qualifiers == null || qualifiers.isEmpty()) {
            return null;
        }

        try {
            final TreeMap<String, String> results = qualifiers.entrySet().stream()
                    .filter(entry -> !isEmpty(entry.getValue()))
                    .collect(
                            TreeMap::new,
                            (map, value) -> map.put(toLowerCase(value.getKey()), value.getValue()),
                            TreeMap::putAll);
            return validateQualifiers(results);
        } catch (ValidationException ex) {
            throw new MalformedPackageURLException(ex.getMessage());
        }
    }

    static @Nullable Map<String, String> parseQualifiers(final String encodedString)
            throws MalformedPackageURLException {
        try {
            final TreeMap<String, String> results = Arrays.stream(encodedString.split("&"))
                    .collect(
                            TreeMap::new,
                            (map, value) -> {
                                final String[] entry = value.split("=", 2);
                                if (entry.length == 2 && !entry[1].isEmpty()) {
                                    String key = toLowerCase(entry[0]);
                                    if (map.put(key, percentDecode(entry[1])) != null) {
                                        throw new ValidationException(
                                                "Duplicate package qualifier encountered. More then one value was specified for "
                                                        + key);
                                    }
                                }
                            },
                            TreeMap::putAll);
            return validateQualifiers(results);
        } catch (ValidationException e) {
            throw new MalformedPackageURLException(e);
        }
    }

    private static String[] parsePath(final String path, final boolean isSubpath) {
        return Arrays.stream(path.split("/"))
                .filter(segment -> !segment.isEmpty() && !(isSubpath && (".".equals(segment) || "..".equals(segment))))
                .map(PackageURL::percentDecode)
                .toArray(String[]::new);
    }

    private String encodePath(final String path, BitSet safe) {
        return Arrays.stream(path.split("/"))
                .map(source -> percentEncode(source, safe))
                .collect(Collectors.joining("/"));
    }

    /**
     * Evaluates if the specified Package URL has the same values up to, but excluding
     * the qualifier (querystring).
     * This includes equivalence of the scheme, type, namespace, name, and version, but excludes qualifier and subpath
     * from evaluation.
     *
     * @deprecated This method is no longer recommended and will be removed from a future release.
     * <p> Use {@link PackageURL#isCoordinatesEquals} instead.</p>
     *
     * @param purl the Package URL to evaluate
     * @return true if equivalence passes, false if not
     * @since 1.2.0
     */
    // @Deprecated(since = "1.4.0", forRemoval = true)
    @Deprecated
    public boolean isBaseEquals(final PackageURL purl) {
        return isCoordinatesEquals(purl);
    }

    /**
     * Evaluates if the specified Package URL has the same values up to, but excluding
     * the qualifier (querystring).
     * This includes equivalence of the scheme, type, namespace, name, and version, but excludes qualifier and subpath
     * from evaluation.
     *
     * @param purl the Package URL to evaluate, not {@code null}
     * @return true if equivalence passes, false if not
     * @since 1.4.0
     */
    public boolean isCoordinatesEquals(final PackageURL purl) {
        return type.equals(purl.type)
                && Objects.equals(namespace, purl.namespace)
                && name.equals(purl.name)
                && Objects.equals(version, purl.version);
    }

    /**
     * Returns only the canonicalized coordinates of the Package URL which includes the type, namespace, name,
     * and version, and which omits the qualifier and subpath.
     * @return A canonicalized PackageURL String excluding the qualifier and subpath.
     * @since 1.4.0
     */
    public String getCoordinates() {
        return canonicalize(true);
    }

    /**
     * Evaluates if the specified Package URL has the same canonical value. This method
     * canonicalizes the Package URLs being evaluated and performs an equivalence on the
     * canonical values. Canonical equivalence is especially useful for qualifiers, which
     * can be in any order, but have a predictable order in canonicalized form.
     *
     * @param purl the Package URL to evaluate, not {@code null}
     * @return true if equivalence passes, false if not
     * @since 1.2.0
     */
    public boolean isCanonicalEquals(final PackageURL purl) {
        return this.canonicalize().equals(purl.canonicalize());
    }

    private static boolean isEmpty(@Nullable String value) {
        return value == null || value.isEmpty();
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final PackageURL other = (PackageURL) o;
        return type.equals(other.type)
                && Objects.equals(namespace, other.namespace)
                && name.equals(other.name)
                && Objects.equals(version, other.version)
                && Objects.equals(qualifiers, other.qualifiers)
                && Objects.equals(subpath, other.subpath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, namespace, name, version, qualifiers, subpath);
    }

    /**
     * Convenience constants that defines common Package-URL 'type's.
     */
    public static final class StandardTypes {
        /**
         * Arch Linux and other users of the libalpm/pacman package manager.
         *
         * @since 1.6.0
         */
        public static final String ALPM = "alpm";
        /**
         * APK-based packages.
         *
         * @since 1.6.0
         */
        public static final String APK = "apk";
        /**
         * Bitbucket-based packages.
         */
        public static final String BITBUCKET = "bitbucket";
        /**
         * Bitnami-based packages.
         *
         * @since 1.6.0
         */
        public static final String BITNAMI = "bitnami";
        /**
         * Rust.
         *
         * @since 1.2.0
         */
        public static final String CARGO = "cargo";
        /**
         * CocoaPods.
         *
         * @since 1.6.0
         */
        public static final String COCOAPODS = "cocoapods";
        /**
         * Composer PHP packages.
         */
        public static final String COMPOSER = "composer";
        /**
         * Conan C/C++ packages.
         *
         * @since 1.6.0
         */
        public static final String CONAN = "conan";
        /**
         * Conda packages.
         *
         * @since 1.6.0
         */
        public static final String CONDA = "conda";
        /**
         * CPAN Perl packages.
         *
         * @since 1.6.0
         */
        public static final String CPAN = "cpan";
        /**
         * CRAN R packages.
         *
         * @since 1.6.0
         */
        public static final String CRAN = "cran";
        /**
         * Debian, Debian derivatives, and Ubuntu packages.
         *
         * @since 1.6.0
         */
        public static final String DEB = "deb";
        /**
         * Docker images.
         */
        public static final String DOCKER = "docker";
        /**
         *  RubyGems.
         */
        public static final String GEM = "gem";
        /**
         * Plain, generic packages that do not fit anywhere else, such as for "upstream-from-distro" packages.
         */
        public static final String GENERIC = "generic";
        /**
         * GitHub-based packages.
         */
        public static final String GITHUB = "github";
        /**
         * Go packages.
         */
        public static final String GOLANG = "golang";
        /**
         * Haskell packages.
         */
        public static final String HACKAGE = "hackage";
        /**
         * Hex packages.
         *
         * @since 1.6.0
         */
        public static final String HEX = "hex";
        /**
         * Hugging Face ML models.
         *
         * @since 1.6.0
         */
        public static final String HUGGINGFACE = "huggingface";
        /**
         * Lua packages installed with LuaRocks.
         *
         * @since 1.6.0
         */
        public static final String LUAROCKS = "luarocks";
        /**
         * Maven JARs and related artifacts.
         */
        public static final String MAVEN = "maven";
        /**
         * MLflow ML models (Azure ML, Databricks, etc.).
         *
         * @since 1.6.0
         */
        public static final String MLFLOW = "mlflow";
        /**
         *  Nixos packages
         *
         * @since 1.6.0
         */
        public static final String NIX = "nix";
        /**
         * Node NPM packages.
         */
        public static final String NPM = "npm";
        /**
         * NuGet .NET packages.
         */
        public static final String NUGET = "nuget";
        /**
         * All artifacts stored in registries that conform to the
         * <a href="https://github.com/opencontainers/distribution-spec">OCI Distribution Specification</a>, including
         * container images built by Docker and others.
         *
         * @since 1.6.0
         */
        public static final String OCI = "oci";
        /**
         * Dart and Flutter packages.
         *
         * @since 1.6.0
         */
        public static final String PUB = "pub";
        /**
         * Python packages.
         */
        public static final String PYPI = "pypi";
        /**
         * QNX packages.
         *
         * @since 1.6.0
         */
        public static final String QPKG = "qpkg";
        /**
         * RPMs.
         */
        public static final String RPM = "rpm";
        /**
         * ISO-IEC 19770-2 Software Identification (SWID) tags.
         *
         * @since 1.6.0
         */
        public static final String SWID = "swid";
        /**
         * Swift packages.
         *
         * @since 1.6.0
         */
        public static final String SWIFT = "swift";
        /**
         * Debian, Debian derivatives, and Ubuntu packages.
         *
         * @deprecated use {@link #DEB} instead
         */
        @Deprecated
        public static final String DEBIAN = "deb";
        /**
         * Nixos packages.
         *
         * @since 1.1.0
         * @deprecated use {@link #NIX} instead
         */
        @Deprecated
        public static final String NIXPKGS = "nix";

        private StandardTypes() {}
    }
}
