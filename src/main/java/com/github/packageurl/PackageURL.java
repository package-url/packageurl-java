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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * purl stands for package URL.
 *
 * A purl is a URL composed of seven components:
 *
 * scheme:type/namespace/name@version?qualifiers#subpath
 *
 * Components are separated by a specific character for unambiguous parsing.
 * A purl must NOT contain a URL Authority i.e. there is no support for username,
 * password, host and port components. A namespace segment may sometimes look
 * like a host but its interpretation is specific to a type.
 *
 * SPEC: https://github.com/package-url/purl-spec
 *
 * @author Steve Springett
 * @since 1.0.0
 */
public final class PackageURL implements Serializable {

    private static final long serialVersionUID = 3243226021636427586L;
    private static final Pattern TYPE_PATTERN = Pattern.compile("^[a-zA-Z][a-zA-Z0-9.+-]+$");
    private static final Pattern QUALIFIER_KEY_PATTERN = Pattern.compile("^[a-zA-Z][a-zA-Z0-9.-_]+$");

    /**
     * Constructs a new PackageURL object by parsing the specified string.
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
     * @param type the type of package (i.e. maven, npm, gem, etc)
     * @param namespace the name prefix (i.e. group, owner, organization)
     * @param name the name of the package
     * @param version the version of the package
     * @param qualifiers an array of key/value pair qualifiers
     * @param subpath the subpath string
     * @throws MalformedPackageURLException if parsing fails
     * @since 1.0.0
     */
    public PackageURL(String type, String namespace, String name, String version, String qualifiers, String subpath)
            throws MalformedPackageURLException {

        this.scheme = validateScheme("pkg");
        this.type = validateType(type);
        this.namespace = validateNamespace(namespace);
        this.name = validateName(name);
        this.version = validateVersion(version);
        this.qualifiers = validateQualifiers(qualifiers);
        this.subpath = validateSubpath(subpath);
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
    private String qualifiers;

    /**
     * Extra subpath within a package, relative to the package root.
     * Optional.
     */
    private String subpath;

    /**
     * Returns the package url scheme.
     * @return the scheme
     * @since 1.0.0
     */
    public String getScheme() {
        return scheme;
    }

    /**
     * Returns the package "type" or package "protocol" such as maven, npm, nuget, gem, pypi, etc.
     * @return the type
     * @since 1.0.0
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the name prefix such as a Maven groupid, a Docker image owner, a GitHub user or organization.
     * @return the namespace
     * @since 1.0.0
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * Returns the name of the package.
     * @return the name of the package
     * @since 1.0.0
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the version of the package.
     * @return the version of the package
     * @since 1.0.0
     */
    public String getVersion() {
        return version;
    }

    /**
     * Returns extra qualifying data for a package such as an OS, architecture, a distro, etc.
     * @return qualifiers
     * @since 1.0.0
     */
    public String getQualifiers() {
        return qualifiers;
    }

    /**
     * Returns extra subpath within a package, relative to the package root.
     * @return the subpath
     * @since 1.0.0
     */
    public String getSubpath() {
        return subpath;
    }

    private void parse(String purl) throws MalformedPackageURLException {
        if (purl == null || "".equals(purl.trim())) {
            throw new MalformedPackageURLException("Invalid purl: Contains an empty or null value");
        }

        //todo: figure out ignoring ://

        // scheme:type/namespace/name@version?qualifiers#subpath

        try {
            URI uri = new URI(purl);
            // Check to ensure that none of these parts are parsed. If so, it's an invalid purl.
            if (uri.getAuthority() != null || uri.getUserInfo() != null || uri.getHost() != null
                    || uri.getPort() != -1 || uri.getPath() != null || uri.getQuery() != null) {
                throw new MalformedPackageURLException("Invalid purl: Contains parts not supported by the purl spec");
            }

            this.scheme = validateScheme(uri.getScheme());

            // This is the purl (minus the scheme) that needs parsed.
            String parsablePurl = uri.getSchemeSpecificPart();

            String firstPart = null, secondPartVersion = null, secondPartQualifier = null;
            if (parsablePurl.contains("@")) { // Version is optional - check for existence
                firstPart = parsablePurl.split("@")[0];
                secondPartVersion = parsablePurl.split("@")[1];
            }
            if (parsablePurl.contains("?")) { // qualifiers are optional - check for existence
                if (firstPart == null) {
                    firstPart = parsablePurl.split("\\?")[0];
                }
                secondPartQualifier = parsablePurl.split("\\?")[1];
            }
            if (firstPart == null) {
                firstPart = parsablePurl;
            }

            // The firstPartArray may contain type, namespace, and name
            String[] firstPartArray = firstPart.split("/");
            if (firstPartArray.length < 2) { // The array must contain a 'type' and a 'name' at minimum
                throw new MalformedPackageURLException("Invalid purl: Does not contain a minimum of a 'type' and a 'name'");
            }

            String type = firstPartArray[0];
            String name = firstPartArray[firstPartArray.length - 1];
            this.type = validateType(type);
            this.name = validateName(name);


            // Test for namespaces
            if (firstPartArray.length > 2) {
                // purl contains a namespace
                String[] namespaces = Arrays.copyOfRange(firstPartArray, 1, firstPartArray.length - 1);
                String namespace = String.join(",", namespaces);
                this.namespace = validateNamespace(namespace);
            }


            // Split and parse version
            if (secondPartVersion != null) {
                String version = secondPartVersion;
                if (secondPartVersion.contains("?")) {
                    version = secondPartVersion.split("\\?")[0];
                }
                this.version = validateVersion(version);
            }


            // Split and parse qualifiers
            if (secondPartQualifier != null) {
                String qualifiers = secondPartQualifier;
                if (secondPartQualifier.contains("#")) {
                    qualifiers = secondPartQualifier.split("#")[0];
                }
                this.qualifiers = validateQualifiers(qualifiers);
            }


            // Subpath is the last part - nothing to split. Simply validate it if it exists.
            if (uri.getFragment() != null) {
                this.subpath = validateSubpath(uri.getFragment());
            }


        } catch (URISyntaxException e) {
            System.out.println(e);
            // invalid
        }
    }

    private String validateScheme(String scheme) throws MalformedPackageURLException {
        if (!scheme.equals("pkg")) {
            throw new MalformedPackageURLException("The PackageURL scheme is invalid");
        }
        return scheme;
    }

    private String validateType(String type) throws MalformedPackageURLException {
        if (!TYPE_PATTERN.matcher(type).matches()) {
            throw new MalformedPackageURLException("The PackageURL type specified is invalid");
        }
        return type.toLowerCase(); // Type always needs to be lowercase
    }

    private String validateNamespace(String namespace) {
        return namespace.toLowerCase(); // Namespace always needs to be lowercase
    }

    private String validateName(String name) {
        return name.toLowerCase(); // Name always needs to be lowercase
    }

    private String validateVersion(String version) {
        return version;
    }

    private String validateQualifiers(String qualifiers) {
        StringBuilder modified = new StringBuilder();
        String[] pairs = qualifiers.split("&");
        for (String pair: pairs) {
            if (pair.contains("=")) {
                String[] kvpair = pair.split("=");
                //todo??????
                if (this.type.equals("maven") && kvpair[0].equalsIgnoreCase("packaging")) {
                    modified.append("classifier=").append(kvpair[1]);
                } else {
                    modified.append(pair);
                }
                modified.append("&");
            }
        }

        String modifiedString = modified.toString();
        return ((modifiedString.endsWith("&")) ? modifiedString.substring(0, modifiedString.length() - 1) : modifiedString).toLowerCase();
    }

    private String validateSubpath(String subpath) {
        return stripLeadingAndTrailingSlash(subpath); // leading and trailing slashes always need to be removed
    }

    /**
     * Returns a canonicalized representation of the purl.
     * @return a canonicalized representation of the purl
     */
    public String canonicalize() {
        StringBuilder purl = new StringBuilder();
        purl.append(scheme).append(":");
        if (type != null) {
            purl.append(type);
        }
        purl.append("/");
        if (namespace != null) {
            purl.append(namespace);
            purl.append("/");
        }
        if (name != null) {
            purl.append(name);
        }
        if (version != null) {
            purl.append("@").append(version);
        }
        if (qualifiers != null) {
            purl.append("?").append(qualifiers);
        }
        if (subpath != null) {
            purl.append("#").append(subpath);
        }
        return purl.toString();
    }

    /**
     * Returns the purl.
     * @return the purl
     */
    @Override
    public String toString() {
        StringBuilder purl = new StringBuilder();
        purl.append(scheme).append(":");
        if (type != null) {
            purl.append(type);
        }
        purl.append("/");
        if (namespace != null) {
            purl.append(namespace);
            purl.append("/");
        }
        if (name != null) {
            purl.append(name);
        }
        if (version != null) {
            purl.append("@").append(version);
        }
        if (qualifiers != null) {
            purl.append("?").append(qualifiers);
        }
        if (subpath != null) {
            purl.append("#").append(subpath);
        }
        return purl.toString();
    }

    private String stripLeadingAndTrailingSlash(String input) {
        if (input.startsWith("/")) {
            input = input.substring(1, input.length());
        }
        if (input.endsWith("/")) {
            input = input.substring(0, input.length() -1);
        }
        return input;
    }
}
