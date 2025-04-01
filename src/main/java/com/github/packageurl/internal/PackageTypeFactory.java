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

import aQute.bnd.annotation.spi.ServiceConsumer;
import com.github.packageurl.MalformedPackageURLException;
import com.github.packageurl.PackageURL;
import com.github.packageurl.spi.PackageTypeProvider;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeMap;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.osgi.annotation.bundle.Requirement.Cardinality;
import org.osgi.annotation.bundle.Requirement.Resolution;

@ServiceConsumer(
        value = PackageTypeProvider.class,
        resolution = Resolution.MANDATORY,
        cardinality = Cardinality.MULTIPLE)
public final class PackageTypeFactory implements PackageTypeProvider {
    private static final @NonNull PackageTypeFactory INSTANCE = new PackageTypeFactory();

    public static final @NonNull String TYPE = "__packagetypefactory__";

    private @Nullable Map<@NonNull String, @NonNull PackageTypeProvider> packageTypeProviders;

    private PackageTypeFactory() {}

    public static @NonNull PackageTypeFactory getInstance() {
        return INSTANCE;
    }

    private static @NonNull String normalizeType(@NonNull String type) {
        return StringUtil.toLowerCase(type);
    }

    private static @Nullable String normalizeSubpath(@Nullable String subpath) {
        if (subpath == null) {
            return null;
        }

        String[] segments = subpath.split("/", -1);
        List<String> segmentList = new ArrayList<>(segments.length);

        for (String segment : segments) {
            if (!"..".equals(segment) && !".".equals(segment)) {
                segmentList.add(segment);
            }
        }

        return String.join("/", segmentList);
    }

    private static @Nullable Map<String, String> normalizeQualifiers(@Nullable Map<String, String> qualifiers)
            throws MalformedPackageURLException {
        if (qualifiers == null) {
            return null;
        }

        Set<Map.Entry<String, String>> entries = qualifiers.entrySet();
        Map<String, String> map = new TreeMap<>();

        for (Map.Entry<String, String> entry : entries) {
            String key = StringUtil.toLowerCase(entry.getKey());

            if (map.put(key, entry.getValue()) != null) {
                throw new MalformedPackageURLException("duplicate qualifiers key '" + key + "'");
            }
        }

        return Collections.unmodifiableMap(map);
    }

    private static void validateQualifiers(@Nullable Map<String, String> qualifiers)
            throws MalformedPackageURLException {
        if (qualifiers == null || qualifiers.isEmpty()) {
            return;
        }

        Set<Map.Entry<String, String>> entries = qualifiers.entrySet();

        for (Map.Entry<String, String> entry : entries) {
            String key = entry.getKey();

            if (!key.chars().allMatch(StringUtil::isValidCharForKey)) {
                throw new MalformedPackageURLException("checks for invalid qualifier keys. The qualifier key '" + key
                        + "' contains invalid characters");
            }
        }
    }

    public static void validateType(@NonNull String type) throws MalformedPackageURLException {
        if (type.isEmpty()) {
            throw new MalformedPackageURLException("a type is always required");
        }

        char first = type.charAt(0);

        if (!StringUtil.isAlpha(first)) {
            throw new MalformedPackageURLException("check for type that starts with number: '" + first + "'");
        }

        Map<Integer, Character> map = new LinkedHashMap<>(type.length());
        type.chars().filter(c -> !StringUtil.isValidCharForType(c)).forEach(c -> map.put(c, (char) c));

        if (!map.isEmpty()) {
            throw new MalformedPackageURLException("check for invalid characters in type: " + map);
        }
    }

    static void validateName(@NonNull String name) throws MalformedPackageURLException {
        if (name.isEmpty()) {
            throw new MalformedPackageURLException("a name is always required");
        }
    }

    @Override
    public void validateComponents(
            @NonNull String type,
            @Nullable String namespace,
            @NonNull String name,
            @Nullable String version,
            @Nullable Map<String, String> qualifiers,
            @Nullable String subpath)
            throws MalformedPackageURLException {
        validateType(type);
        validateName(name);
        validateQualifiers(qualifiers);

        String normalizedType = normalizeType(type);
        Map<String, String> normalizedQualifiers = normalizeQualifiers(qualifiers);
        String normalizedSubpath = normalizeSubpath(subpath);
        PackageTypeProvider archiveStreamProvider = getPackageTypeProviders().get(normalizedType);

        if (archiveStreamProvider != null) {
            archiveStreamProvider.validateComponents(
                    normalizedType, namespace, name, version, normalizedQualifiers, normalizedSubpath);
        }
    }

    @Override
    public @NonNull PackageURL normalizeComponents(
            @NonNull String type,
            @Nullable String namespace,
            @NonNull String name,
            @Nullable String version,
            @Nullable Map<String, String> qualifiers,
            @Nullable String subpath)
            throws MalformedPackageURLException {
        String normalizedType = normalizeType(type);
        Map<String, String> normalizedQualifiers = normalizeQualifiers(qualifiers);
        String normalizedSubpath = normalizeSubpath(subpath);
        PackageTypeProvider archiveStreamProvider = getPackageTypeProviders().get(normalizedType);

        if (archiveStreamProvider != null) {
            return archiveStreamProvider.normalizeComponents(
                    normalizedType, namespace, name, version, normalizedQualifiers, normalizedSubpath);
        }

        return new PackageURL(normalizedType, namespace, name, version, normalizedQualifiers, normalizedSubpath);
    }

    @Override
    public @NonNull String getPackageType() {
        return TYPE;
    }

    @SuppressWarnings("removal")
    private static @NonNull Map<@NonNull String, @NonNull PackageTypeProvider> findAvailablePackageTypeProviders() {
        return AccessController.doPrivileged((PrivilegedAction<Map<String, PackageTypeProvider>>) () -> {
            Map<String, PackageTypeProvider> map = new TreeMap<>();
            ServiceLoader<PackageTypeProvider> loader =
                    ServiceLoader.load(PackageTypeProvider.class, ClassLoader.getSystemClassLoader());

            for (PackageTypeProvider provider : loader) {
                map.put(provider.getPackageType(), provider);
            }

            return Collections.unmodifiableMap(map);
        });
    }

    public @NonNull Map<String, PackageTypeProvider> getPackageTypeProviders() {
        if (packageTypeProviders == null) {
            packageTypeProviders = findAvailablePackageTypeProviders();
        }

        return Collections.unmodifiableMap(packageTypeProviders);
    }
}
