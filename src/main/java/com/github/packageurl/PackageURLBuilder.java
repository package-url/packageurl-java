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

import java.util.Map;
import java.util.Set;
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
        // empty constructor for utility class
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
    public PackageURLBuilder withType(final String type) {
        this.type = type;
        return this;
    }

    /**
     * Adds the package namespace.
     *
     * @param namespace the package namespace
     * @return a reference to the builder
     * @see PackageURL#getNamespace()
     */
    public PackageURLBuilder withNamespace(final String namespace) {
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
    public PackageURLBuilder withName(final String name) {
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
    public PackageURLBuilder withVersion(final String version) {
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
    public PackageURLBuilder withSubpath(final String subpath) {
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
    public PackageURLBuilder withQualifier(final String key, final String value) {
        if (qualifiers == null) {
            qualifiers = new TreeMap<>();
        }
        qualifiers.put(key, value);
        return this;
    }

    /**
     * Adds the package qualifiers.
     *
     * @param qualifiers the package qualifiers
     * @return a reference to the builder
     * @see PackageURL#getQualifiers()
     */
    public PackageURLBuilder withQualifiers(final Map<String, String> qualifiers) {
        if (qualifiers == null) {
            this.qualifiers = null;
        } else {
            if (this.qualifiers == null) {
                this.qualifiers = new TreeMap<>(qualifiers);
            } else {
                this.qualifiers.putAll(qualifiers);
            }
        }
        return this;
    }

    /**
     * Removes a package qualifier. This is a no-op if the qualifier is not present.
     * @param key the package qualifier key to remove
     * @return a reference to the builder
     */
    public PackageURLBuilder withoutQualifier(final String key) {
        if (qualifiers != null) {
            qualifiers.remove(key);
            if (qualifiers.isEmpty()) { qualifiers = null; }
        }
        return this;
    }

    /**
     * Removes a package qualifier. This is a no-op if the qualifier is not present.
     * @param keys the package qualifier keys to remove
     * @return a reference to the builder
     */
    public PackageURLBuilder withoutQualifiers(final Set<String> keys) {
        if (this.qualifiers != null) {
            keys.forEach(k -> this.qualifiers.remove(k));
            if (this.qualifiers.isEmpty()) { this.qualifiers = null; }
        }
        return this;
    }


    /**
     * Removes all qualifiers, if any.
     * @return a reference to this builder.
     */
    public PackageURLBuilder withoutQualifiers() {
        qualifiers = null;
        return this;
    }

    /**
     * Removes all qualifiers, if any.
     * @return a reference to this builder.
     */
    public PackageURLBuilder withNoQualifiers() {
        qualifiers = null;
        return this;
    }

    /**
     * Returns current type value set in the builder.
     * @return type set in this builder
     */
    public String getType() {
        return type;
    }

    /**
     * Returns current namespace value set in the builder.
     * @return namespace set in this builder
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * Returns current name value set in the builder.
     * @return name set in this builder
     */
    public String getName() {
        return name;
    }

    /**
     * Returns current version value set in the builder.
     * @return version set in this builder
     */
    public String getVersion() {
        return version;
    }

    /**
     * Returns current subpath value set in the builder.
     * @return subpath set in this builder
     */
    public String getSubpath() {
        return subpath;
    }

    /**
     * Returns sorted map containing all qualifiers set in this builder.
     * An empty map is returned if no qualifiers is set.
     * @return all qualifiers set in this builder, or an empty map if none are set.
     */
    public TreeMap<String, String> getQualifiers() {
        if (qualifiers == null) { return new TreeMap<>(); }
        return new TreeMap<>(qualifiers);
    }

    /**
     * Returns a currently set qualifier value set in the builder for the specified key.
     * @param key qualifier key
     * @return qualifier value or {@code null} if one is not set.
     */
    public String getQualifier(String key) {
        if (qualifiers == null) { return null; }
        return qualifiers.get(key);
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
