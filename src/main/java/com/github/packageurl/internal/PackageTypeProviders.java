package com.github.packageurl.internal;

import aQute.bnd.annotation.spi.ServiceProvider;
import com.github.packageurl.MalformedPackageURLException;
import com.github.packageurl.PackageURL;
import com.github.packageurl.spi.PackageTypeProvider;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.osgi.annotation.bundle.Requirement;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public final class PackageTypeProviders {
    private PackageTypeProviders() {}

    @ServiceProvider(value = PackageTypeProvider.class, resolution = Requirement.Resolution.MANDATORY)
    public static class ApkPackageTypeProvider extends LowercaseNamespaceAndNameTypeProvider {}

    @ServiceProvider(value = PackageTypeProvider.class, resolution = Requirement.Resolution.MANDATORY)
    public static class BitbucketPackageTypeProvider extends LowercaseNamespaceAndNameTypeProvider {}

    @ServiceProvider(value = PackageTypeProvider.class, resolution = Requirement.Resolution.MANDATORY)
    public static class BitnamiPackageTypeProvider extends LowercaseNamespacePackageTypeProvider {}

    @ServiceProvider(value = PackageTypeProvider.class, resolution = Requirement.Resolution.MANDATORY)
    public static class CocoapodsPackageTypeProvider implements PackageTypeProvider {
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
    public static class ComposerPackageTypeProvider extends LowercaseNamespaceAndNameTypeProvider {}

    @ServiceProvider(value = PackageTypeProvider.class, resolution = Requirement.Resolution.MANDATORY)
    public static class ConanPackageTypeProvider implements PackageTypeProvider {
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
    public static class CpanPackageTypeProvider implements PackageTypeProvider {
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
    public static class CranPackageTypeProvider implements PackageTypeProvider {
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
    public static class DebPackageTypeProvider extends LowercaseNamespaceAndNameTypeProvider {}

    @ServiceProvider(value = PackageTypeProvider.class, resolution = Requirement.Resolution.MANDATORY)
    public static class GenericPackageTypeProvider implements PackageTypeProvider {}

    @ServiceProvider(value = PackageTypeProvider.class, resolution = Requirement.Resolution.MANDATORY)
    public static class GithubPackageTypeProvider extends LowercaseNamespaceAndNameTypeProvider {}

    @ServiceProvider(value = PackageTypeProvider.class, resolution = Requirement.Resolution.MANDATORY)
    public static class GolangPackageTypeProvider extends LowercaseNamespacePackageTypeProvider {}

    @ServiceProvider(value = PackageTypeProvider.class, resolution = Requirement.Resolution.MANDATORY)
    public static class HackagePackageTypeProvider implements PackageTypeProvider {
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
    public static class HexPackageTypeProvider extends LowercaseNamespaceAndNameTypeProvider {}

    @ServiceProvider(value = PackageTypeProvider.class, resolution = Requirement.Resolution.MANDATORY)
    public static class HuggingfacePackageTypeProvider extends LowercaseVersionPackageTypeProvider {}

    @ServiceProvider(value = PackageTypeProvider.class, resolution = Requirement.Resolution.MANDATORY)
    public static class LuarocksPackageTypeProvider extends LowercaseVersionPackageTypeProvider {}

    @ServiceProvider(value = PackageTypeProvider.class, resolution = Requirement.Resolution.MANDATORY)
    public static class MavenPackageTypeProvider implements PackageTypeProvider {
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
    public static class MlflowPackageTypeProvider implements PackageTypeProvider {
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
    public static class OciPackageTypeProvider implements PackageTypeProvider {
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
            return new PackageURL(
                    type,
                    namespace,
                    StringUtil.toLowerCase(name),
                    version != null ? StringUtil.toLowerCase(version) : null,
                    qualifiers,
                    subpath);
        }
    }

    @ServiceProvider(value = PackageTypeProvider.class, resolution = Requirement.Resolution.MANDATORY)
    public static class PubPackageTypeProvider extends LowercaseNamePackageTypeProvider {}

    @ServiceProvider(value = PackageTypeProvider.class, resolution = Requirement.Resolution.MANDATORY)
    public static class PypiPackageTypeProvider implements PackageTypeProvider {
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
    public static class QpkgPackageTypeProvider extends LowercaseNamespacePackageTypeProvider {}

    @ServiceProvider(value = PackageTypeProvider.class, resolution = Requirement.Resolution.MANDATORY)
    public static class RpmPackageTypeProvider extends LowercaseNamespacePackageTypeProvider {}

    @ServiceProvider(value = PackageTypeProvider.class, resolution = Requirement.Resolution.MANDATORY)
    public static class SwiftPackageTypeProvider implements PackageTypeProvider {
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
                    type, namespace != null ? StringUtil.toLowerCase(namespace) : null, name, version, qualifiers, subpath);
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
                    type, namespace, name, version != null ? StringUtil.toLowerCase(version) : null, qualifiers, subpath);
        }
    }
}
