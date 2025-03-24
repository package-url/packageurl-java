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

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.jspecify.annotations.Nullable;

/**
 * A builder construct for Package-URL objects.
 *
 * @since 1.1.0
 */
public final class PackageURLBuilder {
    private @Nullable String type = null;
    private @Nullable String namespace = null;
    private @Nullable String name = null;
    private @Nullable String version = null;
    private @Nullable String subpath = null;
    private @Nullable Map<String, String> qualifiers = null;

    private PackageURLBuilder() {
        // empty constructor for utility class
    }

    /**
     * Obtains a reference to a new builder object.
     *
     * @return a new builder object
     */
    public static PackageURLBuilder aPackageURL() {
        return new PackageURLBuilder();
    }

    private static PackageURLBuilder toBuilder(PackageURL packageURL) {
        return PackageURLBuilder.aPackageURL()
                .withType(packageURL.getType())
                .withNamespace(packageURL.getNamespace())
                .withName(packageURL.getName())
                .withVersion(packageURL.getVersion())
                .withQualifiers(packageURL.getQualifiers())
                .withSubpath(packageURL.getSubpath());
    }

    /**
     * Obtains a reference to a new builder object initialized with the existing {@link PackageURL} object.
     *
     * @param packageURL the existing Package URL object
     * @return a new builder object
     * @since 2.0.0
     */
    public static PackageURLBuilder aPackageURL(final PackageURL packageURL) {
        return toBuilder(packageURL);
    }

    /**
     * Obtain a reference to a new builder object initialized with the existing Package URL string.
     *
     * @param purl the existing Package URL string
     * @return a new builder object
     * @throws MalformedPackageURLException if an error occurs while parsing the input
     * @since 2.0.0
     */
    public static PackageURLBuilder aPackageURL(final String purl) throws MalformedPackageURLException {
        return toBuilder(new PackageURL(purl));
    }

    /**
     * Adds the package URL type.
     *
     * @param type the package type, not {@code null}
     * @return a reference to the builder
     * @throws NullPointerException if the argument is {@code null}
     * @see PackageURL#getName()
     * @see com.github.packageurl.PackageURL.StandardTypes
     */
    public PackageURLBuilder withType(final String type) {
        this.type = requireNonNull(type, "type");
        return this;
    }

    /**
     * Adds the package namespace.
     *
     * @param namespace the package namespace or {@code null}
     * @return a reference to the builder
     * @see PackageURL#getNamespace()
     */
    public PackageURLBuilder withNamespace(final @Nullable String namespace) {
        this.namespace = namespace;
        return this;
    }

    /**
     * Adds the package name.
     *
     * @param name the package name, not {@code null}
     * @return a reference to the builder
     * @throws NullPointerException if the argument is {@code null}
     * @see PackageURL#getName()
     */
    public PackageURLBuilder withName(final String name) {
        this.name = requireNonNull(name, "name");
        return this;
    }

    /**
     * Adds the package version.
     *
     * @param version the package version or {@code null}
     * @return a reference to the builder
     * @see PackageURL#getVersion()
     */
    public PackageURLBuilder withVersion(final @Nullable String version) {
        this.version = version;
        return this;
    }

    /**
     * Adds the package subpath.
     *
     * @param subpath the package subpath or {@code null}
     * @return a reference to the builder
     * @see PackageURL#getSubpath()
     */
    public PackageURLBuilder withSubpath(final @Nullable String subpath) {
        this.subpath = subpath;
        return this;
    }

    /**
     * Adds a package qualifier.
     * <p>
     *     If {@code value} is empty or {@code null}, the given qualifier is removed instead.
     * </p>
     *
     * @param key the package qualifier key, not {@code null}
     * @param value the package qualifier value or {@code null}
     * @return a reference to the builder
     * @throws NullPointerException if {@code key} is {@code null}
     * @see PackageURL#getQualifiers()
     */
    public PackageURLBuilder withQualifier(final String key, final @Nullable String value) {
        requireNonNull(key, "qualifier key can not be null");
        if (value == null || value.isEmpty()) {
            if (qualifiers != null) {
                qualifiers.remove(key);
            }
        } else {
            if (qualifiers == null) {
                qualifiers = new TreeMap<>();
            }
            qualifiers.put(requireNonNull(key, "qualifier key can not be null"), value);
        }
        return this;
    }

    /**
     * Adds the package qualifiers.
     *
     * @param qualifiers the package qualifiers, or {@code null}
     * @return a reference to the builder
     * @see PackageURL#getQualifiers()
     * @since 2.0.0
     */
    public PackageURLBuilder withQualifiers(final @Nullable Map<String, String> qualifiers) {
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
     *
     * @param key the package qualifier key to remove
     * @return a reference to the builder
     * @throws NullPointerException if {@code key} is {@code null}
     * @since 1.5.0
     */
    public PackageURLBuilder withoutQualifier(final String key) {
        if (qualifiers != null) {
            qualifiers.remove(requireNonNull(key));
            if (qualifiers.isEmpty()) {
                qualifiers = null;
            }
        }
        return this;
    }

    /**
     * Removes a package qualifier. This is a no-op if the qualifier is not present.
     * @param keys the package qualifier keys to remove
     * @return a reference to the builder
     * @since 2.0.0
     */
    public PackageURLBuilder withoutQualifiers(final Set<String> keys) {
        if (this.qualifiers != null) {
            keys.forEach(k -> this.qualifiers.remove(k));
            if (this.qualifiers.isEmpty()) {
                this.qualifiers = null;
            }
        }
        return this;
    }

    /**
     * Removes all qualifiers, if any.
     * @return a reference to this builder.
     * @since 2.0.0
     */
    public PackageURLBuilder withoutQualifiers() {
        qualifiers = null;
        return this;
    }

    /**
     * Removes all qualifiers, if any.
     *
     * @return a reference to this builder.
     * @deprecated use {@link #withoutQualifiers()} instead
     * @since 1.5.0
     */
    @Deprecated
    public PackageURLBuilder withNoQualifiers() {
        return withoutQualifiers();
    }

    /**
     * Returns current type value set in the builder.
     *
     * @return type set in this builder
     * @since 1.5.0
     */
    public @Nullable String getType() {
        return type;
    }

    /**
     * Returns current namespace value set in the builder.
     *
     * @return namespace set in this builder
     * @since 1.5.0
     */
    public @Nullable String getNamespace() {
        return namespace;
    }

    /**
     * Returns current name value set in the builder.
     *
     * @return name set in this builder
     * @since 1.5.0
     */
    public @Nullable String getName() {
        return name;
    }

    /**
     * Returns current version value set in the builder.
     *
     * @return version set in this builder
     * @since 1.5.0
     */
    public @Nullable String getVersion() {
        return version;
    }

    /**
     * Returns current subpath value set in the builder.
     *
     * @return subpath set in this builder
     * @since 1.5.0
     */
    public @Nullable String getSubpath() {
        return subpath;
    }

    /**
     * Returns sorted map containing all qualifiers set in this builder.
     * An empty map is returned if no qualifiers are set
     *
     * @return all qualifiers set in this builder, or an empty map if none are set
     * @since 1.5.0
     */
    public Map<String, String> getQualifiers() {
        return qualifiers != null ? Collections.unmodifiableMap(qualifiers) : Collections.emptyMap();
    }

    /**
     * Returns a currently set qualifier value set in the builder for the specified key.
     *s
     * @param key qualifier key
     * @return qualifier value or {@code null} if one is not set
     * @since 1.5.0
     */
    public @Nullable String getQualifier(String key) {
        return qualifiers == null ? null : qualifiers.get(requireNonNull(key));
    }

    /**
     * Builds the new PackageURL object.
     *
     * @return the new PackageURL object
     * @throws MalformedPackageURLException thrown if the type or name has not been specified or if a field fails validation
     */
    public PackageURL build() throws MalformedPackageURLException {
        if (type == null) {
            throw new MalformedPackageURLException("type is required");
        }
        if (name == null) {
            throw new MalformedPackageURLException("name is required");
        }
        return new PackageURL(type, namespace, name, version, qualifiers, subpath);
    }
}
