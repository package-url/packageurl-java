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

import aQute.bnd.annotation.spi.ServiceProvider;
import com.github.packageurl.MalformedPackageURLException;
import com.github.packageurl.PackageURL;
import com.github.packageurl.spi.PackageTypeProvider;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.osgi.annotation.bundle.Requirement;

public final class PackageTypeProviders {
    private PackageTypeProviders() {}

    @ServiceProvider(value = PackageTypeProvider.class, resolution = Requirement.Resolution.MANDATORY)
    public static class Apk extends LowercaseNamespaceAndNameTypeProvider {}

    @ServiceProvider(value = PackageTypeProvider.class, resolution = Requirement.Resolution.MANDATORY)
    public static class Bitbucket extends LowercaseNamespaceAndNameTypeProvider {}

    @ServiceProvider(value = PackageTypeProvider.class, resolution = Requirement.Resolution.MANDATORY)
    public static class Bitnami extends LowercaseNamespacePackageTypeProvider {}

    @ServiceProvider(value = PackageTypeProvider.class, resolution = Requirement.Resolution.MANDATORY)
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

    @ServiceProvider(value = PackageTypeProvider.class, resolution = Requirement.Resolution.MANDATORY)
    public static class Composer extends LowercaseNamespaceAndNameTypeProvider {}

    @ServiceProvider(value = PackageTypeProvider.class, resolution = Requirement.Resolution.MANDATORY)
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

    @ServiceProvider(value = PackageTypeProvider.class, resolution = Requirement.Resolution.MANDATORY)
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

    @ServiceProvider(value = PackageTypeProvider.class, resolution = Requirement.Resolution.MANDATORY)
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

    @ServiceProvider(value = PackageTypeProvider.class, resolution = Requirement.Resolution.MANDATORY)
    public static class Deb extends LowercaseNamespaceAndNameTypeProvider {}

    @ServiceProvider(value = PackageTypeProvider.class, resolution = Requirement.Resolution.MANDATORY)
    public static class Generic implements PackageTypeProvider {}

    @ServiceProvider(value = PackageTypeProvider.class, resolution = Requirement.Resolution.MANDATORY)
    public static class Github extends LowercaseNamespaceAndNameTypeProvider {}

    @ServiceProvider(value = PackageTypeProvider.class, resolution = Requirement.Resolution.MANDATORY)
    public static class Golang extends LowercaseNamespacePackageTypeProvider {}

    @ServiceProvider(value = PackageTypeProvider.class, resolution = Requirement.Resolution.MANDATORY)
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

    @ServiceProvider(value = PackageTypeProvider.class, resolution = Requirement.Resolution.MANDATORY)
    public static class Hex extends LowercaseNamespaceAndNameTypeProvider {}

    @ServiceProvider(value = PackageTypeProvider.class, resolution = Requirement.Resolution.MANDATORY)
    public static class Huggingface extends LowercaseVersionPackageTypeProvider {}

    @ServiceProvider(value = PackageTypeProvider.class, resolution = Requirement.Resolution.MANDATORY)
    public static class Luarocks extends LowercaseVersionPackageTypeProvider {}

    @ServiceProvider(value = PackageTypeProvider.class, resolution = Requirement.Resolution.MANDATORY)
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

    @ServiceProvider(value = PackageTypeProvider.class, resolution = Requirement.Resolution.MANDATORY)
    public static class Mlflow implements PackageTypeProvider {
        private static @Nullable String normalizeName(@Nullable String name, @Nullable Map<String, String> qualifiers)
                throws MalformedPackageURLException {
            if (qualifiers != null) {
                String repositoryUrl = qualifiers.get("repository_url");

                if (repositoryUrl != null) {
                    String host;

                    try {
                        URI url = new URI(repositoryUrl);
                        host = url.getHost();

                        if (name != null && host.matches(".*[.]?azuredatabricks.net$")) {
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

    @ServiceProvider(value = PackageTypeProvider.class, resolution = Requirement.Resolution.MANDATORY)
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

    @ServiceProvider(value = PackageTypeProvider.class, resolution = Requirement.Resolution.MANDATORY)
    public static class Pub extends LowercaseNamePackageTypeProvider {}

    @ServiceProvider(value = PackageTypeProvider.class, resolution = Requirement.Resolution.MANDATORY)
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

    @ServiceProvider(value = PackageTypeProvider.class, resolution = Requirement.Resolution.MANDATORY)
    public static class Qpkg extends LowercaseNamespacePackageTypeProvider {}

    @ServiceProvider(value = PackageTypeProvider.class, resolution = Requirement.Resolution.MANDATORY)
    public static class Rpm extends LowercaseNamespacePackageTypeProvider {}

    @ServiceProvider(value = PackageTypeProvider.class, resolution = Requirement.Resolution.MANDATORY)
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
