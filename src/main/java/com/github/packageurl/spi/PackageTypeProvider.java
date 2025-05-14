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
package com.github.packageurl.spi;

import com.github.packageurl.MalformedPackageURLException;
import com.github.packageurl.PackageURL;
import com.github.packageurl.internal.PackageTypeFactory;
import com.github.packageurl.internal.StringUtil;
import java.util.Map;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * This interface defines a common interface for package type providers. Each package
 * type provider must implement this interface to validate and normalize package URLs.
 * <p>
 * Implementations of this interface are registered as OSGi services.
 * <p>
 * The {@link PackageTypeFactory} class is responsible for loading and managing the available
 * package type providers.
 * These providers are used to validate and normalize package URLs based on their
 * specific type.
 * Classes must be added to {@code META-INF/services/com.github.packageurl.spi.PackageTypeProvider} in order for
 * {@link java.util.ServiceLoader ServiceLoader} to find them.
 *
 */
public interface PackageTypeProvider {
    /**
     * Validates the components of a package URL.
     *
     * @param type the type of the package
     * @param namespace the namespace of the package
     * @param name the name of the package
     * @param version the version of the package
     * @param qualifiers the qualifiers of the package
     * @param subpath the subpath of the package
     * @throws MalformedPackageURLException if the components are not valid
     */
    default void validateComponents(
            @NonNull String type,
            @Nullable String namespace,
            @NonNull String name,
            @Nullable String version,
            @Nullable Map<String, String> qualifiers,
            @Nullable String subpath)
            throws MalformedPackageURLException {}

    /**
     * Normalizes the components of a package URL.
     *
     * @param type the type of the package
     * @param namespace the namespace of the package
     * @param name the name of the package
     * @param version the version of the package
     * @param qualifiers the qualifiers of the package
     * @param subpath the subpath of the package
     * @return a normalized PackageURL object
     * @throws MalformedPackageURLException if the components are not valid
     */
    default @NonNull PackageURL normalizeComponents(
            @NonNull String type,
            @Nullable String namespace,
            @NonNull String name,
            @Nullable String version,
            @Nullable Map<String, String> qualifiers,
            @Nullable String subpath)
            throws MalformedPackageURLException {
        return new PackageURL(type, namespace, name, version, qualifiers, subpath);
    }

    /**
     * Returns the package type of this provider.
     *
     * @return the package type of this provider
     */
    default @NonNull String getPackageType() {
        String type = StringUtil.toLowerCase(getClass().getSimpleName());

        try {
            PackageTypeFactory.validateType(type);
        } catch (MalformedPackageURLException e) {
            throw new IllegalArgumentException(
                    "Package type provider name '" + type + "' is not a valid package type", e);
        }

        return type;
    }
}
