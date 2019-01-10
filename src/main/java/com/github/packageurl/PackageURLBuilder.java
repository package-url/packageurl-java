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

import java.util.TreeMap;

/**
 * A builder construct for Package-URL objects.
 */
public final class PackageURLBuilder {
    private String type = null;
    private String namespace = null;
    private String name = null;
    private String version = null;
    private String subpath = null;
    private TreeMap<String, String> qualifiers = null;

    private PackageURLBuilder() {
        //empty constructor for utility class
    }

    /**
     * Obtain a reference to a new builder object.
     *
     * @return a new builder object.
     */
    public static PackageURLBuilder aPackageURL() {
        return new PackageURLBuilder();
    }

    /**
     * Adds the package URL type.
     *
     * @param type the package type
     * @return a reference to the builder
     * @see PackageURL#getName()
     * @see com.github.packageurl.PackageURL.StandardTypes
     */
    public PackageURLBuilder withType(String type) {
        this.type = type;
        return this;
    }

    /**
     * Adds the package URL type.
     *
     * @param type the package type
     * @return a reference to the builder
     * @see PackageURL#getName()
     * @see com.github.packageurl.PackageURL.StandardTypes
     */
    public PackageURLBuilder withType(PackageURL.StandardTypes type) {
        this.type = type.toString();
        return this;
    }

    /**
     * Adds the package namespace.
     *
     * @param namespace the package namespace
     * @return a reference to the builder
     * @see PackageURL#getNamespace()
     */
    public PackageURLBuilder withNamespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    /**
     * Adds the package name.
     *
     * @param name the package name
     * @return a reference to the builder
     * @see PackageURL#getName()
     */
    public PackageURLBuilder withName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Adds the package version.
     *
     * @param version the package version
     * @return a reference to the builder
     * @see PackageURL#getVersion()
     */
    public PackageURLBuilder withVersion(String version) {
        this.version = version;
        return this;
    }

    /**
     * Adds the package subpath.
     *
     * @param subpath the package subpath
     * @return a reference to the builder
     * @see PackageURL#getSubpath()
     */
    public PackageURLBuilder withSubpath(String subpath) {
        this.subpath = subpath;
        return this;
    }

    /**
     * Adds a package qualifier.
     *
     * @param key   the package qualifier key
     * @param value the package qualifier value
     * @return a reference to the builder
     * @see PackageURL#getQualifiers()
     */
    public PackageURLBuilder withQualifier(String key, String value) {
        if (qualifiers == null) {
            qualifiers = new TreeMap<>();
        }
        qualifiers.put(key, value);
        return this;
    }

    /**
     * Builds the new PackageURL object.
     *
     * @return the new PackageURL object
     * @throws MalformedPackageURLException thrown if the type or name has not been specified or if a field fails validation
     */
    public PackageURL build() throws MalformedPackageURLException {
        return new PackageURL(type, namespace, name, version, qualifiers, subpath);
    }
}
