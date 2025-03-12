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
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;
import org.jspecify.annotations.Nullable;

/**
 * <p>Package-URL (aka purl) is a "mostly universal" URL to describe a package. A purl is a URL composed of seven components:</p>
 * <pre>
 * scheme:type/namespace/name@version?qualifiers#subpath
 * </pre>
 * <p>
 * Components are separated by a specific character for unambiguous parsing.
 * A purl must NOT contain a URL Authority i.e. there is no support for username,
 * password, host and port components. A namespace segment may sometimes look
 * like a host but its interpretation is specific to a type.
 * </p>
 * <p>SPEC: <a href="https://github.com/package-url/purl-spec">https://github.com/package-url/purl-spec</a></p>
 *
 * @author Steve Springett
 * @since 1.0.0
 */
public final class PackageURL implements Serializable {

    private static final long serialVersionUID = 3243226021636427586L;

    /**
     * Constructs a new PackageURL object by parsing the specified string.
     *
     * @param purl a valid package URL string to parse
     * @throws MalformedPackageURLException if parsing fails
     * @throws NullPointerException if {@code purl} is {@code null}
     * @since 1.0.0
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
     * @since 1.0.0
     */
    public PackageURL(final String type, final String name) throws MalformedPackageURLException {
        this(type, null, name, null, null, null);
    }

    /**
     * Constructs a new PackageURL object.
     *
     * @param type       the type of package (i.e. maven, npm, gem, etc), not {@code null}
     * @param namespace  the name prefix (i.e. group, owner, organization)
     * @param name       the name of the package, not {@code null}
     * @param version    the version of the package
     * @param qualifiers an array of key/value pair qualifiers
     * @param subpath    the subpath string
     * @throws MalformedPackageURLException if parsing fails
     * @throws NullPointerException if {@code type} or {@code name} are {@code null}
     * @since 1.0.0
     */
    public PackageURL(final String type, final @Nullable String namespace, final String name, final @Nullable String version,
                      final @Nullable TreeMap<String, String> qualifiers, final @Nullable String subpath)
            throws MalformedPackageURLException {
        this.type = toLowerCase(validateType(requireNonNull(type, "type")));
        this.namespace = validateNamespace(namespace);
        this.name = validateName(requireNonNull(name, "name"));
        this.version = validateVersion(type, version);
        this.qualifiers = parseQualifiers(qualifiers);
        this.subpath = validateSubpath(subpath);
        verifyTypeConstraints(this.type, this.namespace);
    }

    /**
     * Constructs a new PackageURL object.
     *
     * @param type the type of package (i.e. maven, npm, gem, etc)
     * @param namespace the name prefix (i.e. group, owner, organization)
     * @param name the name of the package
     * @param version the version of the package
     * @param qualifiers an array of key/value pair qualifiers
     * @param subpath the subpath string
     * @throws MalformedPackageURLException if parsing fails
     * @throws NullPointerException if {@code type} or {@code name} are {@code null}
     * @since 1.6.0
     */
    public PackageURL(final String type, final @Nullable String namespace, final String name, final @Nullable String version,
                      final @Nullable Map<String, String> qualifiers, final @Nullable String subpath)
            throws MalformedPackageURLException {
        this(type, namespace, name, version, (qualifiers != null) ? new TreeMap<>(qualifiers) : null, subpath);
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
     * Converts this {@link PackageURL} to a {@link PackageURLBuilder}.
     *
     * @return the builder
     * @deprecated Use {@link PackageURLBuilder#aPackageURL(PackageURL)} or {@link PackageURLBuilder#aPackageURL(String)}
     */
    public PackageURLBuilder toBuilder() {
        PackageURLBuilder builder = PackageURLBuilder.aPackageURL()
                .withType(getType())
                .withNamespace(getNamespace())
                .withName(getName())
                .withVersion(getVersion())
                .withSubpath(getSubpath());

        if (qualifiers != null) {
            qualifiers.forEach(builder::withQualifier);
        }

        return builder;
    }

    /**
     * Returns the package url scheme.
     *
     * @return the scheme
     * @since 1.0.0
     */
    public String getScheme() {
        return SCHEME;
    }

    /**
     * Returns the package "type" or package "protocol" such as maven, npm, nuget, gem, pypi, etc.
     *
     * @return the type
     * @since 1.0.0
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the name prefix such as a Maven groupid, a Docker image owner, a GitHub user or organization.
     *
     * @return the namespace
     * @since 1.0.0
     */
    public @Nullable String getNamespace() {
        return namespace;
    }

    /**
     * Returns the name of the package.
     *
     * @return the name of the package
     * @since 1.0.0
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the version of the package.
     *
     * @return the version of the package
     * @since 1.0.0
     */
    public @Nullable String getVersion() {
        return version;
    }

    /**
     * Returns extra qualifying data for a package such as an OS, architecture, a distro, etc.
     * This method returns an UnmodifiableMap.
     * @return qualifiers
     * @since 1.0.0
     */
    public @Nullable Map<String, String> getQualifiers() {
        return (qualifiers != null) ? Collections.unmodifiableMap(qualifiers) : null;
    }

    /**
     * Returns extra subpath within a package, relative to the package root.
     *
     * @return the subpath
     * @since 1.0.0
     */
    public @Nullable String getSubpath() {
        return subpath;
    }

     private void validateScheme(final String value) throws MalformedPackageURLException {
            if (!SCHEME.equals(value)) {
                throw new MalformedPackageURLException("The PackageURL scheme '" + value + "' is invalid. It should be '" + SCHEME + "'");
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

    private static void validateChars(String value, IntPredicate predicate, String component) throws MalformedPackageURLException {
        char firstChar = value.charAt(0);

        if (isDigit(firstChar)) {
            throw new MalformedPackageURLException("The PackageURL " + component + " cannot start with a number: " + firstChar);
        }

        String invalidChars = value.chars().filter(predicate.negate()).mapToObj(c -> String.valueOf((char) c)).collect(Collectors.joining(", "));

        if (!invalidChars.isEmpty()) {
            throw new MalformedPackageURLException("The PackageURL " + component + " '" + value + "' contains invalid characters: " + invalidChars);
        }
    }

    private @Nullable String validateNamespace(final @Nullable String value) throws MalformedPackageURLException {
        if (isEmpty(value)) {
            return null;
        }
        return validateNamespace(value.split("/"));
    }

    private @Nullable String validateNamespace(final String[] values) throws MalformedPackageURLException {
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
                    throw new MalformedPackageURLException("The PackageURL specified contains a namespace which is not allowed for type: " + type);
                }
                retVal = null;
                break;
            default:
                retVal = tempNamespace;
                break;
        }
        return retVal;
    }

    private String validateName(final String value) throws MalformedPackageURLException {
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

    private @Nullable String validateVersion(final String type, final @Nullable String value) {
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

    private @Nullable Map<String, String> validateQualifiers(final @Nullable Map<String, String> values) throws MalformedPackageURLException {
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

    private static void validateValue(final String key, final @Nullable String value) throws MalformedPackageURLException {
        if (isEmpty(value)) {
            throw new MalformedPackageURLException("The specified PackageURL contains an empty or null qualifier value for key " + key);
        }
    }

    private @Nullable String validateSubpath(final @Nullable String value) throws MalformedPackageURLException {
        if (isEmpty(value)) {
            return null;
        }
        return validatePath(value.split("/"), true);
    }

    private static @Nullable String validatePath(final String[] segments, final boolean isSubPath) throws MalformedPackageURLException {
        if (segments.length == 0) {
            return null;
        }
        try {
            return Arrays.stream(segments)
                    .peek(segment -> {
                        if (isSubPath && ("..".equals(segment) || ".".equals(segment))) {
                            throw new ValidationException("Segments in the subpath may not be a period ('.') or repeated period ('..')");
                        } else if (segment.contains("/")) {
                            throw new ValidationException("Segments in the namespace and subpath may not contain a forward slash ('/')");
                        } else if (segment.isEmpty()) {
                            throw new ValidationException("Segments in the namespace and subpath may not be empty");
                        }
                    }).collect(Collectors.joining("/"));
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
     * @since 1.0.0
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
        purl.append(SCHEME_PART).append(type).append("/");
        if (namespace != null) {
            purl.append(encodePath(namespace));
            purl.append("/");
        }
        purl.append(percentEncode(name));
        if (version != null) {
            purl.append("@").append(percentEncode(version));
        }
        if (! coordinatesOnly) {
            if (qualifiers != null) {
                purl.append("?");
                qualifiers.forEach((key, value) -> {
                    purl.append(toLowerCase(key));
                    purl.append("=");
                    purl.append(percentEncode(value));
                    purl.append("&");
                });
                purl.setLength(purl.length() - 1);
            }
            if (subpath != null) {
                purl.append("#").append(encodePath(subpath));
            }
        }
        return purl.toString();
    }

    /**
     * Encodes the input in conformance with RFC 3986.
     *
     * @param input the String to encode
     * @return an encoded String
     */
    private String percentEncode(final String input) {
        if (input.isEmpty()) {
            return input;
        }

        StringBuilder builder = new StringBuilder();
        for (byte b : input.getBytes(StandardCharsets.UTF_8)) {
            if (isUnreserved(b)) {
                builder.append((char) b);
            }
            else {
                // Substitution: A '%' followed by the hexadecimal representation of the ASCII value of the replaced character
                builder.append('%');
                builder.append(Integer.toHexString(b).toUpperCase());
            }
        }
        return builder.toString();
    }

    private static boolean isUnreserved(int c) {
        return (isValidCharForKey(c) || c == '~');
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
        return (c ^ 0x20);
    }

    private static String toLowerCase(String s) {
        int pos = indexOfFirstUpperCaseChar(s);

        if (pos == -1) {
            return s;
        }

        char[] chars = s.toCharArray();
        int length = chars.length;

        for (int i = pos; i < length; i++) {
            if (isUpperCase(chars[i])) {
                chars[i] = (char) toLowerCase(chars[i]);
            }
        }

        return new String(chars);
    }

    /**
     * Optionally decodes a String, if it's encoded. If String is not encoded,
     * method will return the original input value.
     *
     * @param input the value String to decode
     * @return a decoded String
     */
    private String percentDecode(final String input) {
        final String decoded = uriDecode(input);
        if (!decoded.equals(input)) {
            return decoded;
        }
        return input;
    }

    /**
     * Decodes a percent-encoded string.
     *
     * @param source string to decode, not {@code null}
     * @return A decoded string
     * @throws NullPointerException if {@code source} is {@code null}
     */
    public static String uriDecode(String source) {
        int length = source.length();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            if (source.charAt(i) == '%') {
                String str = source.substring(i + 1, i + 3);
                char c = (char) Integer.parseInt(str, 16);
                builder.append(c);
                i += 2;
            }
            else {
                builder.append(source.charAt(i));
            }
        }
        return builder.toString();
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
                throw new MalformedPackageURLException("Invalid purl: " + purl + ". It does not start with '" + SCHEME_PART + "'");
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
                this.name = validateName(percentDecode(remainder.substring(start)));
            } else {
                this.name = validateName(percentDecode(remainder.substring(index + 1)));
                remainder = remainder.substring(0, index);
                this.namespace = validateNamespace(parsePath(remainder.substring(start), false));
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
    private void verifyTypeConstraints(String type, @Nullable String namespace, @Nullable String name) throws MalformedPackageURLException {
        if (StandardTypes.MAVEN.equals(type)) {
            if (isEmpty(namespace) || isEmpty(name)) {
                throw new MalformedPackageURLException("The PackageURL specified is invalid. Maven requires both a namespace and name.");
            }
        }
    }

    private @Nullable Map<String, String> parseQualifiers(final @Nullable Map<String, String> qualifiers) throws MalformedPackageURLException {
        if (qualifiers == null || qualifiers.isEmpty()) {
            return null;
        }

        try {
            final TreeMap<String, String> results = qualifiers.entrySet().stream()
                    .filter(entry -> isEmpty(entry.getValue()))
                    .collect(TreeMap::new,
                            (map, value) -> map.put(toLowerCase(value.getKey()), value.getValue()),
                            TreeMap::putAll);
            return validateQualifiers(results);
        } catch (ValidationException ex) {
            throw new MalformedPackageURLException(ex.getMessage());
        }
    }

    @SuppressWarnings("StringSplitter")//reason: surprising behavior is okay in this case
    private @Nullable Map<String, String> parseQualifiers(final String encodedString) throws MalformedPackageURLException {
        try {
            final TreeMap<String, String> results = Arrays.stream(encodedString.split("&"))
                    .collect(TreeMap::new,
                            (map, value) -> {
                                final String[] entry = value.split("=", 2);
                                if (entry.length == 2 && !entry[1].isEmpty()) {
                                    String key = toLowerCase(entry[0]);
                                    if (map.put(key, percentDecode(entry[1])) != null) {
                                        throw new ValidationException("Duplicate package qualifier encountered. More then one value was specified for " + key);
                                    }
                                }
                            },
                            TreeMap::putAll);
            return validateQualifiers(results);
        } catch (ValidationException e) {
            throw new MalformedPackageURLException(e);
        }
    }

    private String[] parsePath(final String path, final boolean isSubpath) {
        return Arrays.stream(path.split("/"))
                .filter(segment -> !segment.isEmpty() && !(isSubpath && (".".equals(segment) || "..".equals(segment))))
                .map(this::percentDecode)
                .toArray(String[]::new);
    }

    private String encodePath(final String path) {
        return Arrays.stream(path.split("/")).map(this::percentEncode).collect(Collectors.joining("/"));
    }

    /**
     * Evaluates if the specified Package URL has the same values up to, but excluding
     * the qualifier (querystring). This includes equivalence of: scheme, type, namespace,
     * name, and version, but excludes qualifier and subpath from evaluation.
     * @deprecated
     * This method is no longer recommended and will be removed from a future release.
     * <p> Use {@link PackageURL#isCoordinatesEquals} instead.</p>
     *
     * @param purl the Package URL to evaluate
     * @return true if equivalence passes, false if not
     * @since 1.2.0
     */
    //@Deprecated(since = "1.4.0", forRemoval = true)
    @Deprecated
    public boolean isBaseEquals(final PackageURL purl) {
        return isCoordinatesEquals(purl);
    }

    /**
     * Evaluates if the specified Package URL has the same values up to, but excluding
     * the qualifier (querystring). This includes equivalence of: scheme, type, namespace,
     * name, and version, but excludes qualifier and subpath from evaluation.
     *
     * @param purl the Package URL to evaluate, not {@code null}
     * @return true if equivalence passes, false if not
     * @since 1.4.0
     */
    public boolean isCoordinatesEquals(final PackageURL purl) {
        return type.equals(purl.type) &&
                Objects.equals(namespace, purl.namespace) &&
                name.equals(purl.name) &&
                Objects.equals(version, purl.version);
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
        return (this.canonicalize().equals(purl.canonicalize()));
    }

    private static boolean isEmpty(@Nullable String value) {
        return value == null || value.isEmpty();
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final PackageURL other = (PackageURL) o;
        return type.equals(other.type) &&
                Objects.equals(namespace, other.namespace) &&
                name.equals(other.name) &&
                Objects.equals(version, other.version) &&
                Objects.equals(qualifiers, other.qualifiers) &&
                Objects.equals(subpath, other.subpath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, namespace, name, version, qualifiers, subpath);
    }

    /**
     * Convenience constants that defines common Package-URL 'type's.
     *
     * @since 1.0.0
     */
    public static final class StandardTypes {
        public static final String ALPM = "alpm";
        public static final String APK = "apk";
        public static final String BITBUCKET = "bitbucket";
        public static final String BITNAMI = "bitnami";
        public static final String CARGO = "cargo";
        public static final String COCOAPODS = "cocoapods";
        public static final String COMPOSER = "composer";
        public static final String CONAN = "conan";
        public static final String CONDA = "conda";
        public static final String CPAN = "cpan";
        public static final String CRAN = "cran";
        public static final String DEB = "deb";
        public static final String DOCKER = "docker";
        public static final String GEM = "gem";
        public static final String GENERIC = "generic";
        public static final String GITHUB = "github";
        public static final String GOLANG = "golang";
        public static final String HACKAGE = "hackage";
        public static final String HEX = "hex";
        public static final String HUGGINGFACE = "huggingface";
        public static final String LUAROCKS = "luarocks";
        public static final String MAVEN = "maven";
        public static final String MLFLOW = "mlflow";
        public static final String NIX = "nix";
        public static final String NPM = "npm";
        public static final String NUGET = "nuget";
        public static final String OCI = "oci";
        public static final String PUB = "pub";
        public static final String PYPI = "pypi";
        public static final String QPKG = "qpkg";
        public static final String RPM = "rpm";
        public static final String SWID = "swid";
        public static final String SWIFT = "swift";
        @Deprecated
        public static final String DEBIAN = "deb";
        @Deprecated
        public static final String NIXPKGS = "nix";

        private StandardTypes() {

        }
    }

}
