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

import aQute.bnd.annotation.Resolution;
import aQute.bnd.annotation.spi.ServiceProvider;
import com.github.packageurl.MalformedPackageURLException;
import com.github.packageurl.PackageURL;
import com.github.packageurl.spi.PackageTypeProvider;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * This class provides a set of package type providers for different package types.
 * Each provider implements the PackageTypeProvider interface and provides its own
 * validation and normalization logic for the components of a package URL.
 */
public final class PackageTypeProviders {
    private PackageTypeProviders() {}

    /**
     * This class provides a package type provider for the "apk" package type.
     */
    @ServiceProvider(value = PackageTypeProvider.class, resolution = Resolution.MANDATORY)
    public static class Apk extends LowercaseNamespaceAndNameTypeProvider {}

    /**
     * This class provides a package type provider for the "bitbucket" package type.
     */
    @ServiceProvider(value = PackageTypeProvider.class, resolution = Resolution.MANDATORY)
    public static class Bitbucket extends LowercaseNamespaceAndNameTypeProvider {}

    /**
     * This class provides a package type provider for the "bitnami" package type.
     */
    @ServiceProvider(value = PackageTypeProvider.class, resolution = Resolution.MANDATORY)
    public static class Bitnami extends LowercaseNamespacePackageTypeProvider {}

    /**
     * This class provides a package type provider for the "cocoapods" package type.
     */
    @ServiceProvider(value = PackageTypeProvider.class, resolution = Resolution.MANDATORY)
    public static class Cocoapods implements PackageTypeProvider {
        @Override
        public void validateComponents(
                @NonNull String type,
                @Nullable String namespace,
                @NonNull String name,
                @Nullable String version,
                @Nullable Map<String, String> qualifiers,
                @Nullable String subpath)
                throws MalformedPackageURLException {
            if (namespace != null && !namespace.isEmpty()) {
                throw new MalformedPackageURLException("invalid cocoapods purl cannot have a namespace");
            }

            if (name.chars().anyMatch(StringUtil::isWhitespace) || name.startsWith(".") || name.contains("+")) {
                throw new MalformedPackageURLException("invalid cocoapods purl invalid name");
            }
        }
    }

    /**
     * This class provides a package type provider for the "composer" package type.
     */
    @ServiceProvider(value = PackageTypeProvider.class, resolution = Resolution.MANDATORY)
    public static class Composer extends LowercaseNamespaceAndNameTypeProvider {}

    /**
     * This class provides a package type provider for the "conan" package type.
     */
    @ServiceProvider(value = PackageTypeProvider.class, resolution = Resolution.MANDATORY)
    public static class Conan implements PackageTypeProvider {
        @Override
        public void validateComponents(
                @NonNull String type,
                @Nullable String namespace,
                @NonNull String name,
                @Nullable String version,
                @Nullable Map<String, String> qualifiers,
                @Nullable String subpath)
                throws MalformedPackageURLException {
            boolean hasChannel = (qualifiers != null && !qualifiers.isEmpty());

            if ((namespace != null && !namespace.isEmpty()) && !hasChannel) {
                throw new MalformedPackageURLException("invalid conan purl only namespace");
            } else if ((namespace == null || namespace.isEmpty()) && hasChannel) {
                throw new MalformedPackageURLException("invalid conan purl only channel qualifier");
            }
        }
    }

    /**
     * This class provides a package type provider for the "cpan" package type.
     */
    @ServiceProvider(value = PackageTypeProvider.class, resolution = Resolution.MANDATORY)
    public static class Cpan implements PackageTypeProvider {
        @Override
        public void validateComponents(
                @NonNull String type,
                @Nullable String namespace,
                @NonNull String name,
                @Nullable String version,
                @Nullable Map<String, String> qualifiers,
                @Nullable String subpath)
                throws MalformedPackageURLException {
            if ((namespace == null || namespace.isEmpty()) && name.indexOf('-') != -1) {
                throw new MalformedPackageURLException("cpan module name like distribution name");
            } else if ((namespace != null && !namespace.isEmpty()) && name.contains("::")) {
                throw new MalformedPackageURLException("cpan distribution name like module name");
            }
        }
    }

    /**
     * This class provides a package type provider for the "cran" package type.
     */
    @ServiceProvider(value = PackageTypeProvider.class, resolution = Resolution.MANDATORY)
    public static class Cran implements PackageTypeProvider {
        @Override
        public void validateComponents(
                @NonNull String type,
                @Nullable String namespace,
                @NonNull String name,
                @Nullable String version,
                @Nullable Map<String, String> qualifiers,
                @Nullable String subpath)
                throws MalformedPackageURLException {
            if (version == null || version.isEmpty()) {
                throw new MalformedPackageURLException("invalid cran purl without version");
            }
        }
    }

    /**
     * This class provides a package type provider for the "deb" package type.
     */
    @ServiceProvider(value = PackageTypeProvider.class, resolution = Resolution.MANDATORY)
    public static class Deb extends LowercaseNamespaceAndNameTypeProvider {}

    /**
     * This class provides a package type provider for the "generic" package type.
     */
    @ServiceProvider(value = PackageTypeProvider.class, resolution = Resolution.MANDATORY)
    public static class Generic implements PackageTypeProvider {}

    /**
     * This class provides a package type provider for the "github" package type.
     */
    @ServiceProvider(value = PackageTypeProvider.class, resolution = Resolution.MANDATORY)
    public static class Github extends LowercaseNamespaceAndNameTypeProvider {}

    /**
     * This class provides a package type provider for the "golang" package type.
     */
    @ServiceProvider(value = PackageTypeProvider.class, resolution = Resolution.MANDATORY)
    public static class Golang extends LowercaseNamespacePackageTypeProvider {}

    /**
     * This class provides a package type provider for the "hackage" package type.
     */
    @ServiceProvider(value = PackageTypeProvider.class, resolution = Resolution.MANDATORY)
    public static class Hackage implements PackageTypeProvider {
        @Override
        public void validateComponents(
                @NonNull String type,
                @Nullable String namespace,
                @NonNull String name,
                @Nullable String version,
                @Nullable Map<String, String> qualifiers,
                @Nullable String subpath)
                throws MalformedPackageURLException {
            if (version == null || version.isEmpty()) {
                throw new MalformedPackageURLException("name and version are always required");
            }
        }
    }

    /**
     * This class provides a package type provider for the "hex" package type.
     */
    @ServiceProvider(value = PackageTypeProvider.class, resolution = Resolution.MANDATORY)
    public static class Hex extends LowercaseNamespaceAndNameTypeProvider {}

    /**
     * This class provides a package type provider for the "huggingface" package type.
     */
    @ServiceProvider(value = PackageTypeProvider.class, resolution = Resolution.MANDATORY)
    public static class Huggingface extends LowercaseVersionPackageTypeProvider {}

    /**
     * This class provides a package type provider for the "luarocks" package type.
     */
    @ServiceProvider(value = PackageTypeProvider.class, resolution = Resolution.MANDATORY)
    public static class Luarocks extends LowercaseVersionPackageTypeProvider {}

    /**
     * This class provides a package type provider for the "maven" package type.
     */
    @ServiceProvider(value = PackageTypeProvider.class, resolution = Resolution.MANDATORY)
    public static class Maven implements PackageTypeProvider {
        @Override
        public void validateComponents(
                @NonNull String type,
                @Nullable String namespace,
                @NonNull String name,
                @Nullable String version,
                @Nullable Map<String, String> qualifiers,
                @Nullable String subpath)
                throws MalformedPackageURLException {
            if (namespace == null || namespace.isEmpty()) {
                throw new MalformedPackageURLException("a namespace is required");
            }
        }
    }

    /**
     * This class provides a package type provider for the "mlflow" package type.
     */
    @ServiceProvider(value = PackageTypeProvider.class, resolution = Resolution.MANDATORY)
    public static class Mlflow implements PackageTypeProvider {
        private static @NonNull String normalizeName(@NonNull String name, @Nullable Map<String, String> qualifiers)
                throws MalformedPackageURLException {
            if (qualifiers != null) {
                String repositoryUrl = qualifiers.get("repository_url");

                if (repositoryUrl != null) {
                    String host;

                    try {
                        URI url = new URI(repositoryUrl);
                        host = url.getHost();

                        if (host.matches(".*[.]?azuredatabricks.net$")) {
                            return StringUtil.toLowerCase(name);
                        }
                    } catch (URISyntaxException e) {
                        throw new MalformedPackageURLException(
                                "'" + repositoryUrl + "' is not a valid URL for repository_url", e);
                    }
                }
            }

            return name;
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
            if (namespace != null && !namespace.isEmpty()) {
                throw new MalformedPackageURLException("a namespace is not allowed for type '" + type + "'");
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
            return new PackageURL(type, namespace, normalizeName(name, qualifiers), version, qualifiers, subpath);
        }
    }

    /**
     * This class provides a package type provider for the "oci" package type.
     */
    @ServiceProvider(value = PackageTypeProvider.class, resolution = Resolution.MANDATORY)
    public static class Oci extends LowercaseNameAndVersionPackageTypeProvider {
        @Override
        public void validateComponents(
                @NonNull String type,
                @Nullable String namespace,
                @NonNull String name,
                @Nullable String version,
                @Nullable Map<String, String> qualifiers,
                @Nullable String subpath)
                throws MalformedPackageURLException {
            if (namespace != null && !namespace.isEmpty()) {
                throw new MalformedPackageURLException("a namespace is not allowed for type '" + type + "'");
            }
        }
    }

    /**
     * This class provides a package type provider for the "pub" package type.
     */
    @ServiceProvider(value = PackageTypeProvider.class, resolution = Resolution.MANDATORY)
    public static class Pub extends LowercaseNamePackageTypeProvider {}

    /**
     * This class provides a package type provider for the "pypi" package type.
     */
    @ServiceProvider(value = PackageTypeProvider.class, resolution = Resolution.MANDATORY)
    public static class Pypi implements PackageTypeProvider {
        @Override
        public @NonNull PackageURL normalizeComponents(
                @NonNull String type,
                @Nullable String namespace,
                @NonNull String name,
                @Nullable String version,
                @Nullable Map<String, String> qualifiers,
                @Nullable String subpath)
                throws MalformedPackageURLException {
            return new PackageURL(
                    type, namespace, StringUtil.toLowerCase(name).replace('_', '-'), version, qualifiers, subpath);
        }
    }

    /**
     * This class provides a package type provider for the "qpkg" package type.
     */
    @ServiceProvider(value = PackageTypeProvider.class, resolution = Resolution.MANDATORY)
    public static class Qpkg extends LowercaseNamespacePackageTypeProvider {}

    /**
     * This class provides a package type provider for the "rpm" package type.
     */
    @ServiceProvider(value = PackageTypeProvider.class, resolution = Resolution.MANDATORY)
    public static class Rpm extends LowercaseNamespacePackageTypeProvider {}

    /**
     * This class provides a package type provider for the "swift" package type.
     */
    @ServiceProvider(value = PackageTypeProvider.class, resolution = Resolution.MANDATORY)
    public static class Swift implements PackageTypeProvider {
        @Override
        public void validateComponents(
                @NonNull String type,
                @Nullable String namespace,
                @Nullable String name,
                @Nullable String version,
                @Nullable Map<String, String> qualifiers,
                @Nullable String subpath)
                throws MalformedPackageURLException {
            if (namespace == null || namespace.isEmpty()) {
                throw new MalformedPackageURLException("invalid swift purl without namespace");
            }

            if (version == null || version.isEmpty()) {
                throw new MalformedPackageURLException("invalid swift purl without version");
            }
        }
    }

    /**
     * This class provides a common interface for the lowercase name and version package types.
     */
    public static class LowercaseNameAndVersionPackageTypeProvider implements PackageTypeProvider {
        @Override
        public @NonNull PackageURL normalizeComponents(
                @NonNull String type,
                @Nullable String namespace,
                @NonNull String name,
                @Nullable String version,
                @Nullable Map<String, String> qualifiers,
                @Nullable String subpath)
                throws MalformedPackageURLException {
            return new PackageURL(
                    type,
                    namespace,
                    StringUtil.toLowerCase(name),
                    version != null ? StringUtil.toLowerCase(version) : null,
                    qualifiers,
                    subpath);
        }
    }

    /**
     * This class provides a common interface for the lowercase name package types.
     */
    public static class LowercaseNamePackageTypeProvider implements PackageTypeProvider {
        @Override
        public @NonNull PackageURL normalizeComponents(
                @NonNull String type,
                @Nullable String namespace,
                @NonNull String name,
                @Nullable String version,
                @Nullable Map<String, String> qualifiers,
                @Nullable String subpath)
                throws MalformedPackageURLException {
            return new PackageURL(type, namespace, StringUtil.toLowerCase(name), version, qualifiers, subpath);
        }
    }

    /**
     * This class provides a common interface for the lowercase namespace and name package types.
     */
    public static class LowercaseNamespaceAndNameTypeProvider implements PackageTypeProvider {
        @Override
        public @NonNull PackageURL normalizeComponents(
                @NonNull String type,
                @Nullable String namespace,
                @NonNull String name,
                @Nullable String version,
                @Nullable Map<String, String> qualifiers,
                @Nullable String subpath)
                throws MalformedPackageURLException {
            return new PackageURL(
                    type,
                    namespace != null ? StringUtil.toLowerCase(namespace) : null,
                    StringUtil.toLowerCase(name),
                    version,
                    qualifiers,
                    subpath);
        }
    }

    /**
     * This class provides a common interface for the lowercase namespace package types.
     */
    public static class LowercaseNamespacePackageTypeProvider implements PackageTypeProvider {
        @Override
        public @NonNull PackageURL normalizeComponents(
                @NonNull String type,
                @Nullable String namespace,
                @NonNull String name,
                @Nullable String version,
                @Nullable Map<String, String> qualifiers,
                @Nullable String subpath)
                throws MalformedPackageURLException {
            return new PackageURL(
                    type,
                    namespace != null ? StringUtil.toLowerCase(namespace) : null,
                    name,
                    version,
                    qualifiers,
                    subpath);
        }
    }

    /**
     * This class provides a common interface for the lowercase version package types.
     */
    public static class LowercaseVersionPackageTypeProvider implements PackageTypeProvider {
        @Override
        public @NonNull PackageURL normalizeComponents(
                @NonNull String type,
                @Nullable String namespace,
                @NonNull String name,
                @Nullable String version,
                @Nullable Map<String, String> qualifiers,
                @Nullable String subpath)
                throws MalformedPackageURLException {
            return new PackageURL(
                    type,
                    namespace,
                    name,
                    version != null ? StringUtil.toLowerCase(version) : null,
                    qualifiers,
                    subpath);
        }
    }
}
