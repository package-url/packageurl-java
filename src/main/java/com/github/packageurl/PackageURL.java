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
import java.util.Map;
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
    public PackageURL(String purl) throws MalformedPackageURLException {
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
    public PackageURL(String type, String name) throws MalformedPackageURLException {
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
    public PackageURL(String type, String namespace, String name, String version, TreeMap<String, String> qualifiers, String subpath)
            throws MalformedPackageURLException {

        this.scheme = validateScheme("pkg");
        this.type = validateType(type);
        this.namespace = validateNamespace(namespace);
        this.name = validateName(name);
        this.version = validateVersion(version);
        this.qualifiers = validateQualifiers(qualifiers);
        this.subpath = validatePath(subpath, true);
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
     *
     * @return qualifiers
     * @since 1.0.0
     */
    public Map<String, String> getQualifiers() {
        return qualifiers;
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

    private String validateScheme(String value) throws MalformedPackageURLException {
        if ("pkg".equals(value)) {
            return "pkg";
        }
        throw new MalformedPackageURLException("The PackageURL scheme is invalid");
    }

    private String validateType(String value) throws MalformedPackageURLException {
        if (value == null || value.isEmpty()) {
            throw new MalformedPackageURLException("The PackageURL type cannot be null or empty");
        }
        String retVal = value.toLowerCase();
        if (retVal.chars().anyMatch(c -> !(c == '.' || c == '+' || c == '-'
                || (c >= 'a' && c <= 'z')
                || (c >= '0' && c <= '9')))) {
            throw new MalformedPackageURLException("The PackageURL type contains invalid characters");
        }
        return retVal;
    }

    private String validateNamespace(String value) throws MalformedPackageURLException {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return validateNamespace(value.split("/"));
    }

    private String validateNamespace(String[] values) throws MalformedPackageURLException {
        if (values == null || values.length == 0) {
            return null;
        }
        String tempNamespace = validatePath(values, false);


        String retVal;
        switch (type) {
            case StandardTypes.BITBUCKET:
            case StandardTypes.DEBIAN:
            case StandardTypes.GITHUB:
            case StandardTypes.GOLANG:
            case StandardTypes.NPM:
            case StandardTypes.RPM:
                retVal = tempNamespace.toLowerCase();
                break;
            default:
                retVal = tempNamespace;
                break;
        }
        return retVal;
    }

    private String validateName(String value) throws MalformedPackageURLException {
        if (value == null || value.isEmpty()) {
            throw new MalformedPackageURLException("The PackageURL name specified is invalid");
        }
        String temp;
        switch (type) {
            case StandardTypes.BITBUCKET:
            case StandardTypes.DEBIAN:
            case StandardTypes.GITHUB:
            case StandardTypes.GOLANG:
            case StandardTypes.NPM:
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

    private String validateVersion(String value) {
        if (value == null) {
            return null;
        }
        return value;
    }

    private Map<String, String> validateQualifiers(Map<String, String> values) throws MalformedPackageURLException {
        if (values == null) {
            return null;
        }
        for (Map.Entry<String, String> entry : values.entrySet()) {
            validateKey(entry.getKey());
            String value = entry.getValue();
            if (value == null || value.isEmpty()) {
                throw new MalformedPackageURLException("The PackageURL specified contains a qualifier key with an empty or null value");
            }
        }
        return values;
    }

    private String validateKey(String value) throws MalformedPackageURLException {
        if (value == null || value.isEmpty()
                || !(value.charAt(0) >= 'a' && value.charAt(0) <= 'z')
                || !value.chars().allMatch(c -> (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '.' || c == '-' || c == '_')) {
            throw new MalformedPackageURLException("Qualifier key is invalid: " + value);
        }
        return value;
    }

    private String validatePath(String value, boolean isSubpath) throws MalformedPackageURLException {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return validatePath(value.split("/"), isSubpath);
    }

    private String validatePath(String[] segments, boolean isSubpath) throws MalformedPackageURLException {
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
     * Returns a canonicalized representation of the purl.
     *
     * @return a canonicalized representation of the purl
     * @since 1.0.0
     */
    public String canonicalize() {
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
            purl.append(urlencode(name));
        }
        if (version != null) {
            purl.append("@").append(urlencode(version));
        }
        if (qualifiers != null && qualifiers.size() > 0) {
            purl.append("?");
            for (Map.Entry<String, String> entry : qualifiers.entrySet()) {
                purl.append(entry.getKey().toLowerCase());
                purl.append("=");
                purl.append(urlencode(entry.getValue()));
                purl.append("&");
            }
            purl.setLength(purl.length() - 1);
        }
        if (subpath != null) {
            purl.append("#").append(encodePath(subpath));
        }
        return purl.toString();
    }

    /**
     * Encodes the input in conformance with RFC-3986.
     *
     * @param input the String to encode
     * @return an encoded String
     */
    private String urlencode(String input) {
        try {
            // This SHOULD encoded according to RFC-3986 because URLEncoder alone does not.
            return URLEncoder.encode(input, UTF8)
                    .replace("+", "%20")
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
    private String urldecode(String input) {
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
    private void parse(String purl) throws MalformedPackageURLException {
        if (purl == null || purl.trim().isEmpty()) {
            throw new MalformedPackageURLException("Invalid purl: Contains an empty or null value");
        }

        try {
            URI uri = new URI(purl);
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
            StringBuilder remainder = new StringBuilder(uri.getRawSchemeSpecificPart());

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
            if (start > 0) {
                remainder.delete(0, start);
            }

            // type
            index = remainder.indexOf("/");
            if (index < 0) {
                throw new MalformedPackageURLException("Invalid purl: does not contain both a type and name");
            }
            this.type = validateType(remainder.substring(0, index).toLowerCase());
            remainder.delete(0, index + 1);

            // version is optional - check for existence
            index = remainder.lastIndexOf("@");
            if (index >= 0) {
                this.version = validateVersion(urldecode(remainder.substring(index + 1)));
                remainder.setLength(index);
            }

            // The 'remainder' should now consist of the an optional namespace, and the name

            index = remainder.lastIndexOf("/");
            if (index < 0) {
                this.name = validateName(urldecode(remainder.toString()));
            } else {
                this.name = validateName(urldecode(remainder.substring(index + 1)));
                remainder.setLength(index);

                this.namespace = validateNamespace(parsePath(remainder.toString(), false));
            }
        } catch (URISyntaxException e) {
            throw new MalformedPackageURLException("Invalid purl: " + e.getMessage());
        }
    }

    @SuppressWarnings("StringSplitter")//reason: surprising behavior is okay in this case
    private Map<String, String> parseQualifiers(String encodedString) throws MalformedPackageURLException {
        try {
            TreeMap<String, String> results = Arrays.stream(encodedString.split("&"))
                    .collect(TreeMap<String, String>::new,
                            (map, value) -> {
                                String[] entry = value.split("=", 2);
                                if (entry.length == 2 && !entry[1].isEmpty()) {
                                    if (map.put(entry[0].toLowerCase(), urldecode(entry[1])) != null) {
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
    private String[] parsePath(String value, boolean isSubpath) throws MalformedPackageURLException {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return PATH_SPLITTER.splitAsStream(value)
                .filter(segment -> !segment.isEmpty() && !(isSubpath && (".".equals(segment) || "..".equals(segment))))
                .map(segment -> urldecode(segment))
                .toArray(String[]::new);
    }

    private String encodePath(String path) {
        return Arrays.stream(path.split("/")).map(segment -> urlencode(segment)).collect(Collectors.joining("/"));
    }

    /**
     * Convenience constants that defines common Package-URL 'type's.
     *
     * @since 1.0.0
     */
    public static class StandardTypes {
        public static final String BITBUCKET = "bitbucket";
        public static final String COMPOSER = "composer";
        public static final String DEBIAN = "deb";
        public static final String DOCKER = "docker";
        public static final String GEM = "gem";
        public static final String GENERIC = "generic";
        public static final String GITHUB = "github";
        public static final String GOLANG = "golang";
        public static final String MAVEN = "maven";
        public static final String NPM = "npm";
        public static final String NUGET = "nuget";
        public static final String PYPI = "pypi";
        public static final String RPM = "rpm";
    }

}
