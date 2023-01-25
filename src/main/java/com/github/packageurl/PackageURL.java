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

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
    private static final String UTF8 = StandardCharsets.UTF_8.name();
    private static final Pattern PATH_SPLITTER = Pattern.compile("/");

    /**
     * Constructs a new PackageURL object by parsing the specified string.
     *
     * @param purl a valid package URL string to parse
     * @throws MalformedPackageURLException if parsing fails
     * @since 1.0.0
     */
    public PackageURL(final String purl) throws MalformedPackageURLException {
        parse(purl);
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
     * @param type       the type of package (i.e. maven, npm, gem, etc)
     * @param namespace  the name prefix (i.e. group, owner, organization)
     * @param name       the name of the package
     * @param version    the version of the package
     * @param qualifiers an array of key/value pair qualifiers
     * @param subpath    the subpath string
     * @throws MalformedPackageURLException if parsing fails
     * @since 1.0.0
     */
    public PackageURL(final String type, final String namespace, final String name, final String version,
                      final TreeMap<String, String> qualifiers, final String subpath)
            throws MalformedPackageURLException {

        this.scheme = validateScheme("pkg");
        this.type = validateType(type);
        this.namespace = validateNamespace(namespace);
        this.name = validateName(name);
        this.version = validateVersion(version);
        this.qualifiers = validateQualifiers(qualifiers);
        this.subpath = validatePath(subpath, true);
        verifyTypeConstraints(this.type, this.namespace, this.name);
    }

    /**
     * The PackageURL scheme constant
     */
    private String scheme;

    /**
     * The package "type" or package "protocol" such as maven, npm, nuget, gem, pypi, etc.
     * Required.
     */
    private String type;

    /**
     * The name prefix such as a Maven groupid, a Docker image owner, a GitHub user or organization.
     * Optional and type-specific.
     */
    private String namespace;

    /**
     * The name of the package.
     * Required.
     */
    private String name;

    /**
     * The version of the package.
     * Optional.
     */
    private String version;

    /**
     * Extra qualifying data for a package such as an OS, architecture, a distro, etc.
     * Optional and type-specific.
     */
    private Map<String, String> qualifiers;

    /**
     * Extra subpath within a package, relative to the package root.
     * Optional.
     */
    private String subpath;

    /**
     * The cached version of the canonical form.
     */
    private String canonicalizedForm = null;

    /**
     * Returns the package url scheme.
     *
     * @return the scheme
     * @since 1.0.0
     */
    public String getScheme() {
        return scheme;
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
    public String getNamespace() {
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
    public String getVersion() {
        return version;
    }

    /**
     * Returns extra qualifying data for a package such as an OS, architecture, a distro, etc.
     * This method returns an UnmodifiableMap.
     * @return qualifiers
     * @since 1.0.0
     */
    public Map<String, String> getQualifiers() {
        return Collections.unmodifiableMap(qualifiers);
    }

    /**
     * Returns extra subpath within a package, relative to the package root.
     *
     * @return the subpath
     * @since 1.0.0
     */
    public String getSubpath() {
        return subpath;
    }

    private String validateScheme(final String value) throws MalformedPackageURLException {
        if ("pkg".equals(value)) {
            return "pkg";
        }
        throw new MalformedPackageURLException("The PackageURL scheme is invalid");
    }

    private String validateType(final String value) throws MalformedPackageURLException {
        if (value == null || value.isEmpty()) {
            throw new MalformedPackageURLException("The PackageURL type cannot be null or empty");
        }
        if (value.charAt(0) >= '0' && value.charAt(0) <= '9') {
            throw new MalformedPackageURLException("The PackageURL type cannot start with a number");
        }
        final String retVal = value.toLowerCase();
        if (retVal.chars().anyMatch(c -> !(c == '.' || c == '+' || c == '-'
                || (c >= 'a' && c <= 'z')
                || (c >= '0' && c <= '9')))) {
            throw new MalformedPackageURLException("The PackageURL type contains invalid characters");
        }
        return retVal;
    }

    private String validateNamespace(final String value) throws MalformedPackageURLException {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return validateNamespace(value.split("/"));
    }

    private String validateNamespace(final String[] values) throws MalformedPackageURLException {
        if (values == null || values.length == 0) {
            return null;
        }
        final String tempNamespace = validatePath(values, false);

        String retVal;
        switch (type) {
            case StandardTypes.BITBUCKET:
            case StandardTypes.DEBIAN:
            case StandardTypes.GITHUB:
            case StandardTypes.GOLANG:
            case StandardTypes.RPM:
                retVal = tempNamespace.toLowerCase();
                break;
            default:
                retVal = tempNamespace;
                break;
        }
        return retVal;
    }

    private String validateName(final String value) throws MalformedPackageURLException {
        if (value == null || value.isEmpty()) {
            throw new MalformedPackageURLException("The PackageURL name specified is invalid");
        }
        String temp;
        switch (type) {
            case StandardTypes.BITBUCKET:
            case StandardTypes.DEBIAN:
            case StandardTypes.GITHUB:
            case StandardTypes.GOLANG:
                temp = value.toLowerCase();
                break;
            case StandardTypes.PYPI:
                temp = value.replaceAll("_", "-").toLowerCase();
                break;
            default:
                temp = value;
                break;
        }
        return temp;
    }

    private String validateVersion(final String value) {
        if (value == null) {
            return null;
        }
        return value;
    }

    private Map<String, String> validateQualifiers(final Map<String, String> values) throws MalformedPackageURLException {
        if (values == null) {
            return null;
        }
        for (Map.Entry<String, String> entry : values.entrySet()) {
            validateKey(entry.getKey());
            final String value = entry.getValue();
            if (value == null || value.isEmpty()) {
                throw new MalformedPackageURLException("The PackageURL specified contains a qualifier key with an empty or null value");
            }
        }
        return values;
    }

    private String validateKey(final String value) throws MalformedPackageURLException {
        if (value == null || value.isEmpty()) {
            throw new MalformedPackageURLException("Qualifier key is invalid: " + value);
        }
        final String retValue = value.toLowerCase();
        if ((value.charAt(0) >= '0' && value.charAt(0) <= '9')
                || !value.chars().allMatch(c -> (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '.' || c == '-' || c == '_')) {
            throw new MalformedPackageURLException("Qualifier key is invalid: " + value);
        }
        return retValue;
    }

    private String validatePath(final String value, final boolean isSubpath) throws MalformedPackageURLException {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return validatePath(value.split("/"), isSubpath);
    }

    private String validatePath(final String[] segments, final boolean isSubpath) throws MalformedPackageURLException {
        if (segments == null || segments.length == 0) {
            return null;
        }
        try {
            return Arrays.stream(segments)
                    .map(segment -> {
                        if (isSubpath && ("..".equals(segment) || ".".equals(segment))) {
                            throw new ValidationException("Segments in the subpath may not be a period ('.') or repeated period ('..')");
                        } else if (segment.contains("/")) {
                            throw new ValidationException("Segments in the namespace and subpath may not contain a forward slash ('/')");
                        } else if (segment.isEmpty()) {
                            throw new ValidationException("Segments in the namespace and subpath may not be empty");
                        }
                        return segment;
                    }).collect(Collectors.joining("/"));
        } catch (ValidationException ex) {
            throw new MalformedPackageURLException(ex.getMessage());
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
        if (canonicalizedForm != null) {
            return canonicalizedForm;
        }
        final StringBuilder purl = new StringBuilder();
        purl.append(scheme).append(":");
        if (type != null) {
            purl.append(type);
        }
        purl.append("/");
        if (namespace != null) {
            purl.append(encodePath(namespace));
            purl.append("/");
        }
        if (name != null) {
            purl.append(percentEncode(name));
        }
        if (version != null) {
            purl.append("@").append(percentEncode(version));
        }
        if (! coordinatesOnly) {
            if (qualifiers != null && qualifiers.size() > 0) {
                purl.append("?");
                qualifiers.entrySet().stream().forEachOrdered((entry) -> {
                    purl.append(entry.getKey().toLowerCase());
                    purl.append("=");
                    purl.append(percentEncode(entry.getValue()));
                    purl.append("&");
                });
                purl.setLength(purl.length() - 1);
            }
            if (subpath != null) {
                purl.append("#").append(encodePath(subpath));
            }
        }
        canonicalizedForm = purl.toString();
        return canonicalizedForm;
    }

    /**
     * Encodes the input in conformance with RFC 3986.
     *
     * @param input the String to encode
     * @return an encoded String
     */
    private String percentEncode(final String input) {
        try {
            return URLEncoder.encode(input, UTF8)
                    .replace("+", "%2B")
                    // "*" is a reserved character in RFC 3986.
                    .replace("*", "%2A")
                    // "~" is an unreserved character in RFC 3986.
                    .replace("%7E", "~");
        } catch (UnsupportedEncodingException e) {
            return input; // this should never occur
        }
    }

    /**
     * Optionally decodes a String, if it's encoded. If String is not encoded,
     * method will return the original input value.
     *
     * @param input the value String to decode
     * @return a decoded String
     */
    private String percentDecode(final String input) {
        if (input == null) {
            return null;
        }
        try {
            final String decoded = URLDecoder.decode(input, UTF8);
            if (!decoded.equals(input)) {
                return decoded;
            }
        } catch (UnsupportedEncodingException e) {
            return input; // this should never occur
        }
        return input;
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
        if (purl == null || purl.trim().isEmpty()) {
            throw new MalformedPackageURLException("Invalid purl: Contains an empty or null value");
        }

        try {
            final URI uri = new URI(purl);
            // Check to ensure that none of these parts are parsed. If so, it's an invalid purl.
            if (uri.getUserInfo() != null || uri.getPort() != -1) {
                throw new MalformedPackageURLException("Invalid purl: Contains parts not supported by the purl spec");
            }

            this.scheme = validateScheme(uri.getScheme());

            // subpath is optional - check for existence
            if (uri.getRawFragment() != null && !uri.getRawFragment().isEmpty()) {
                this.subpath = validatePath(parsePath(uri.getRawFragment(), true), true);
            }
            // This is the purl (minus the scheme) that needs parsed.
            final StringBuilder remainder = new StringBuilder(uri.getRawSchemeSpecificPart());

            // qualifiers are optional - check for existence
            int index = remainder.lastIndexOf("?");
            if (index >= 0) {
                this.qualifiers = parseQualifiers(remainder.substring(index + 1));
                remainder.setLength(index);
            }

            // trim leading and trailing '/'
            int end = remainder.length() - 1;
            while (end > 0 && '/' == remainder.charAt(end)) {
                end--;
            }
            if (end < remainder.length() - 1) {
                remainder.setLength(end + 1);
            }
            int start = 0;
            while (start < remainder.length() && '/' == remainder.charAt(start)) {
                start++;
            }
            //there is no need for the "expensive" delete operation if the start is tracked and used throughout the rest
            // of the parsing.
            //if (start > 0) {
            //    remainder.delete(0, start);
            //}

            // type
            index = remainder.indexOf("/", start);
            if (index <= start) {
                throw new MalformedPackageURLException("Invalid purl: does not contain both a type and name");
            }
            this.type = validateType(remainder.substring(start, index).toLowerCase());
            //remainder.delete(0, index + 1);
            start = index + 1;

            // version is optional - check for existence
            index = remainder.lastIndexOf("@");
            if (index >= start) {
                this.version = validateVersion(percentDecode(remainder.substring(index + 1)));
                remainder.setLength(index);
            }

            // The 'remainder' should now consist of the an optional namespace, and the name
            index = remainder.lastIndexOf("/");
            if (index <= start) {
                this.name = validateName(percentDecode(remainder.substring(start)));
            } else {
                this.name = validateName(percentDecode(remainder.substring(index + 1)));
                remainder.setLength(index);
                this.namespace = validateNamespace(parsePath(remainder.substring(start), false));
            }
            verifyTypeConstraints(this.type, this.namespace, this.name);
        } catch (URISyntaxException e) {
            throw new MalformedPackageURLException("Invalid purl: " + e.getMessage());
        }
    }

    /**
     * Some purl types may have specific constraints. This method attempts to verify them.
     * @param type the purl type
     * @param namespace the purl namespace
     * @param name the purl name
     * @throws MalformedPackageURLException if constraints are not met
     */
    private void verifyTypeConstraints(String type, String namespace, String name) throws MalformedPackageURLException {
        if (StandardTypes.MAVEN.equals(type)) {
            if (namespace == null || namespace.isEmpty() || name == null || name.isEmpty()) {
                throw new MalformedPackageURLException("The PackageURL specified is invalid. Maven requires both a namespace and name.");
            }
        }
    }

    @SuppressWarnings("StringSplitter")//reason: surprising behavior is okay in this case
    private Map<String, String> parseQualifiers(final String encodedString) throws MalformedPackageURLException {
        try {
            final TreeMap<String, String> results = Arrays.stream(encodedString.split("&"))
                    .collect(TreeMap<String, String>::new,
                            (map, value) -> {
                                final String[] entry = value.split("=", 2);
                                if (entry.length == 2 && !entry[1].isEmpty()) {
                                    if (map.put(entry[0].toLowerCase(), percentDecode(entry[1])) != null) {
                                        throw new ValidationException("Duplicate package qualifier encountere - more then one value was specified for " + entry[0].toLowerCase());
                                    }
                                }
                            },
                            TreeMap<String, String>::putAll);
            return validateQualifiers(results);
        } catch (ValidationException ex) {
            throw new MalformedPackageURLException(ex.getMessage());
        }
    }

    @SuppressWarnings("StringSplitter")//reason: surprising behavior is okay in this case
    private String[] parsePath(final String value, final boolean isSubpath) throws MalformedPackageURLException {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return PATH_SPLITTER.splitAsStream(value)
                .filter(segment -> !segment.isEmpty() && !(isSubpath && (".".equals(segment) || "..".equals(segment))))
                .map(segment -> percentDecode(segment))
                .toArray(String[]::new);
    }

    private String encodePath(final String path) {
        return Arrays.stream(path.split("/")).map(segment -> percentEncode(segment)).collect(Collectors.joining("/"));
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
     * @param purl the Package URL to evaluate
     * @return true if equivalence passes, false if not
     * @since 1.4.0
     */
    public boolean isCoordinatesEquals(final PackageURL purl) {
        return Objects.equals(scheme, purl.scheme) &&
                Objects.equals(type, purl.type) &&
                Objects.equals(namespace, purl.namespace) &&
                Objects.equals(name, purl.name) &&
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
     * @param purl the Package URL to evaluate
     * @return true if equivalence passes, false if not
     * @since 1.2.0
     */
    public boolean isCanonicalEquals(final PackageURL purl) {
        return (this.canonicalize().equals(purl.canonicalize()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final PackageURL other = (PackageURL) o;
        return Objects.equals(scheme, other.scheme) &&
                Objects.equals(type, other.type) &&
                Objects.equals(namespace, other.namespace) &&
                Objects.equals(name, other.name) &&
                Objects.equals(version, other.version) &&
                Objects.equals(qualifiers, other.qualifiers) &&
                Objects.equals(subpath, other.subpath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scheme, type, namespace, name, version, qualifiers, subpath);
    }

    /**
     * Convenience constants that defines common Package-URL 'type's.
     *
     * @since 1.0.0
     */
    public static class StandardTypes {
        public static final String BITBUCKET = "bitbucket";
        public static final String CARGO = "cargo";
        public static final String COMPOSER = "composer";
        public static final String DEBIAN = "deb";
        public static final String DOCKER = "docker";
        public static final String GEM = "gem";
        public static final String GENERIC = "generic";
        public static final String GITHUB = "github";
        public static final String GOLANG = "golang";
        public static final String HEX = "hex";
        public static final String MAVEN = "maven";
        public static final String NPM = "npm";
        public static final String NUGET = "nuget";
        public static final String PYPI = "pypi";
        public static final String RPM = "rpm";
    }

}
