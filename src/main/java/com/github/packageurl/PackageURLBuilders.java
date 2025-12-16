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
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import org.jspecify.annotations.Nullable;

/**
 * Builder factories for creating type-safe Package URLs (PURLs).
 * <p>
 * Each method returns a builder enforcing correct fields and allowed qualifiers
 * for that PURL type based on the official
 * <a href="https://github.com/package-url/purl-spec">PURL specification</a>.
 *
 * @since 2.0.0
 */
public final class PackageURLBuilders {
    private PackageURLBuilders() {}

    /**
     * Returns a builder for Arch Linux packages and other users of the libalpm/pacman package manager.
     *
     * @return a new AlpmBuilder
     */
    public static AlpmBuilder alpm() {
        return new AlpmBuilder();
    }

    /**
     * Returns an alpine Linux APK packages.
     *
     * @return a new ApkBuilder
     */
    public static ApkBuilder apk() {
        return new ApkBuilder();
    }

    /**
     * Returns a builder for Bazel modules as specified in
     * <a href="https://bazel.build/external/module">Bazel modules</a>.
     *
     * @return a new BazelBuilder
     */
    public static BazelBuilder bazel() {
        return new BazelBuilder();
    }

    /**
     * Returns a builder for Bitbucket packages.
     *
     * @return a new BitbucketBuilder
     */
    public static BitbucketBuilder bitbucket() {
        return new BitbucketBuilder();
    }

    /**
     * Returns a builder for Bitnami packages.
     *
     * @return a new BitnamiBuilder
     */
    public static BitnamiBuilder bitnami() {
        return new BitnamiBuilder();
    }

    /**
     * Returns a builder for Cargo packages for Rust.
     *
     * @return a new CargoBuilder
     */
    public static CargoBuilder cargo() {
        return new CargoBuilder();
    }

    /**
     * Returns a builder for CocoaPods pods.
     *
     * @return a new CocoapodsBuilder
     */
    public static CocoapodsBuilder cocoapods() {
        return new CocoapodsBuilder();
    }

    /**
     * Returns a builder for Composer PHP packages.
     *
     * @return a new ComposerBuilder
     */
    public static ComposerBuilder composer() {
        return new ComposerBuilder();
    }

    /**
     * Returns a builder for Conan C/C++ packages.
     * <p>
     * The PURL is designed to closely resemble the Conan-native {@code <package-name>/<package-version>@<user>/<channel>}
     * syntax for package references as specified in
     * <a href="https://docs.conan.io/en/1.46/cheatsheet.html#package-terminology">Package terminology</a>.
     *
     * @return a new ConanBuilder
     */
    public static ConanBuilder conan() {
        return new ConanBuilder();
    }

    /**
     * Returns a builder for Conda packages.
     *
     * @return a new CondaBuilder
     */
    public static CondaBuilder conda() {
        return new CondaBuilder();
    }

    /**
     * Returns a builder for perl package distributions published on CPAN.
     *
     * @return a new CpanBuilder
     */
    public static CpanBuilder cpan() {
        return new CpanBuilder();
    }

    /**
     * Returns a build for CRAN R packages.
     *
     * @return a new CranBuilder
     */
    public static CranBuilder cran() {
        return new CranBuilder();
    }

    /**
     * Returns a builder for debian packages, Debian derivatives, and Ubuntu packages.
     *
     * @return a new DebBuilder
     */
    public static DebBuilder deb() {
        return new DebBuilder();
    }

    /**
     * Returns a builder for Docker images.
     *
     * @return a new DockerBuilder
     */
    public static DockerBuilder docker() {
        return new DockerBuilder();
    }

    /**
     * Returns a builder for RubyGems.
     *
     * @return a new GemBuilder
     */
    public static GemBuilder gem() {
        return new GemBuilder();
    }

    /**
     * Returns the builder for plain, generic packages that do not fit anywhere
     * else such as for &quot;upstream-from-distro&quot; packages. In
     * particular this is handy for a plain version control repository such as
     * a bare git repo in combination with a {@code vcs_url}.
     *
     * @return a new GenericBuilder
     */
    public static GenericBuilder generic() {
        return new GenericBuilder();
    }

    /**
     * Returns a builder for GitHub packages.
     *
     * @return a new GithubBuilder
     */
    public static GithubBuilder github() {
        return new GithubBuilder();
    }

    /**
     * Returns a builder Go packages.
     *
     * @return a new GolangBuilder
     */
    public static GolangBuilder golang() {
        return new GolangBuilder();
    }

    /**
     * Returns a builder Haskell packages.
     *
     * @return a new HackageBuilder
     */
    public static HackageBuilder hackage() {
        return new HackageBuilder();
    }

    /**
     * Returns a builder for Hex packages.
     *
     * @return a new HexBuilder
     */
    public static HexBuilder hex() {
        return new HexBuilder();
    }

    /**
     * Returns a builder for Hugging Face ML models.
     *
     * @return a new HuggingfaceBuilder
     */
    public static HuggingfaceBuilder huggingface() {
        return new HuggingfaceBuilder();
    }

    /**
     * Returns a builder for Julia packages
     *
     * @return a new JuliaBuilder
     */
    public static JuliaBuilder julia() {
        return new JuliaBuilder();
    }

    /**
     * Returns a builder for Lua packages installed with LuaRocks.
     *
     * @return a new LuarocksBuilder
     */
    public static LuarocksBuilder luarocks() {
        return new LuarocksBuilder();
    }

    /**
     * Returns a builder for Maven JARs and related artifacts.
     *
     * @return a new MavenBuilder
     */
    public static MavenBuilder maven() {
        return new MavenBuilder();
    }

    /**
     * Returns a builder for MLflow ML models (Azure ML, Databricks, etc.)
     *
     * @return a new MlflowBuilder
     */
    public static MlflowBuilder mlflow() {
        return new MlflowBuilder();
    }

    /**
     * Returns a builder for NPM packages.
     *
     * @return a new NpmBuilder
     */
    public static NpmBuilder npm() {
        return new NpmBuilder();
    }

    /**
     * Returns a builder for NuGet .NET packages.
     *
     * @return a new NugetBuilder
     */
    public static NugetBuilder nuget() {
        return new NugetBuilder();
    }

    /**
     * Returns a builder for artifacts stored in registries that conform to the
     * <a href="https://github.com/opencontainers/distribution-spec">OCI Distribution Specification</a>
     * including container images built by Docker and others.
     *
     * @return a new OciBuilder
     */
    public static OciBuilder oci() {
        return new OciBuilder();
    }

    /**
     * Returns a builder for Dart and Flutter pub packages.
     *
     * @return a new PubBuilder
     */
    public static PubBuilder pub() {
        return new PubBuilder();
    }

    /**
     * Returns a builder for Python packages.
     * <p>
     * a python packages
     *
     * @return a new PypiBuilder
     */
    public static PypiBuilder pypi() {
        return new PypiBuilder();
    }

    /**
     * Returns a QNX packages.
     *
     * @return a new QpkgBuilder
     */
    public static QpkgBuilder qpkg() {
        return new QpkgBuilder();
    }

    /**
     * Returns an RPM packages.
     *
     * @return a new RpmBuilder
     */
    public static RpmBuilder rpm() {
        return new RpmBuilder();
    }

    /**
     * Returns a builder for ISO-IEC 19770-2 Software Identification (SWID) tags.
     *
     * @return a new SwidBuilder
     */
    public static SwidBuilder swid() {
        return new SwidBuilder();
    }

    /**
     * Returns a builder for Swift packages.
     *
     * @return a new SwiftBuilder
     */
    public static SwiftBuilder swift() {
        return new SwiftBuilder();
    }

    /**
     * Common builder shared between the different types.
     *
     * @param <T> the builder type
     */
    public abstract static class Builder<T extends Builder<T>> {
        protected final Map<String, String> qualifiers = new TreeMap<>();

        protected @Nullable String subpath;

        protected Builder() {}

        /**
         * Sets the subpath.
         *
         * @param subpath the subpath
         * @return this builder instance
         */
        @SuppressWarnings("unchecked")
        public T withSubpath(String subpath) {
            this.subpath = subpath;
            return (T) this;
        }

        /**
         * Removes the subpath
         *
         * @return this builder instance
         */
        @SuppressWarnings("unchecked")
        public T withoutSubpath() {
            this.subpath = null;
            return (T) this;
        }

        /**
         * Allows the specification of a version range. The value must adhere to the Version Range Specification.
         *
         * @param vers the value
         * @return this builder instance
         */
        @SuppressWarnings("unchecked")
        public T withVers(String vers) {
            qualifiers.put("vers", vers);
            return (T) this;
        }

        /**
         * Removes the vers.
         *
         * @return this builder instance
         */
        @SuppressWarnings("unchecked")
        public T withoutVers() {
            qualifiers.remove("vers");
            return (T) this;
        }

        /**
         * An extra URL for an alternative, non-default package repository or registry.
         *
         * @param repositoryUrl the repository URL
         * @return this builder instance
         */
        @SuppressWarnings("unchecked")
        public T withRepositoryUrl(String repositoryUrl) {
            qualifiers.put("repository_url", repositoryUrl);
            return (T) this;
        }

        /**
         * Removes the repository URL.
         *
         * @return this builder instance
         */
        @SuppressWarnings("unchecked")
        public T withoutRepositoryUrl() {
            qualifiers.remove("repository_url");
            return (T) this;
        }

        /**
         * An extra URL for a direct package web download URL to optionally qualify a PURL.
         *
         * @param downloadUrl the value
         * @return this builder instance
         */
        @SuppressWarnings("unchecked")
        public T withDownloadUrl(String downloadUrl) {
            qualifiers.put("download_url", downloadUrl);
            return (T) this;
        }

        /**
         * Removes the download URL.
         *
         * @return this builder instance
         */
        @SuppressWarnings("unchecked")
        public T withoutDownloadUrl() {
            qualifiers.remove("download_url");
            return (T) this;
        }

        /**
         * An extra URL for a package version control system URL to optionally qualify a PURL.
         *
         * @param vcsUrl the value
         * @return this builder instance
         */
        @SuppressWarnings("unchecked")
        public T withVcsUrl(String vcsUrl) {
            qualifiers.put("vcs_url", vcsUrl);
            return (T) this;
        }

        /**
         * Removes the VCS URL.
         *
         * @return this builder instance
         */
        @SuppressWarnings("unchecked")
        public T withoutVcsUrl() {
            qualifiers.remove("vcs_url");
            return (T) this;
        }

        /**
         * An extra file name of a package archive.
         *
         * @param fileName the value
         * @return this builder instance
         */
        @SuppressWarnings("unchecked")
        public T withFileName(String fileName) {
            qualifiers.put("file_name", fileName);
            return (T) this;
        }

        /**
         * Removes the file name.
         *
         * @return this builder instance
         */
        @SuppressWarnings("unchecked")
        public T withoutFileName() {
            qualifiers.remove("file_name");
            return (T) this;
        }

        /**
         * A qualifier for one or more checksums stored as a comma-separated list.
         *
         * @param checksum the value
         * @return this builder instance
         */
        @SuppressWarnings("unchecked")
        public T withChecksum(String checksum) {
            qualifiers.put("checksum", checksum);
            return (T) this;
        }

        /**
         * Removes the checksum
         *
         * @return this builder instance
         */
        @SuppressWarnings("unchecked")
        public T withoutChecksum() {
            qualifiers.remove("checksum");
            return (T) this;
        }

        /**
         * Returns empty. This builder has no default repository.
         *
         * @return empty
         */
        public Optional<String> getDefaultRepositoryUrl() {
            return Optional.empty();
        }

        /**
         * Constructs the final Package URL.
         *
         * @return a fully constructed Package URL
         * @throws MalformedPackageURLException if the Package URL is invalid
         */
        public abstract PackageURL build() throws MalformedPackageURLException;
    }

    /**
     * Builder for Arch Linux packages and other users of the libalpm/pacman package manager.
     */
    public static final class AlpmBuilder extends Builder<AlpmBuilder> {
        private @Nullable String vendor;

        private @Nullable String name;

        private @Nullable String version;

        private AlpmBuilder() {}

        /**
         * Sets the vendor such as arch, arch32, archarm, manjaro or msys.
         *
         * @param vendor the vendor value
         * @return this builder instance
         */
        public AlpmBuilder withVendor(String vendor) {
            this.vendor = vendor;
            return this;
        }

        /**
         * Removes the namespace.
         *
         * @return this builder instance
         */
        public AlpmBuilder withoutVendor() {
            this.vendor = null;
            return this;
        }

        /**
         * Sets the name.
         *
         * @param name the name
         * @return this builder instance
         */
        public AlpmBuilder withName(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the version.
         *
         * @param version the version string
         * @return this builder instance
         */
        public AlpmBuilder withVersion(String version) {
            this.version = version;
            return this;
        }

        /**
         * Removes the version.
         *
         * @return this builder instance
         */
        public AlpmBuilder withoutVersion() {
            this.version = null;
            return this;
        }

        /**
         * Sets the package architecture.
         *
         * @param arch the package architecture
         * @return this builder instance
         */
        public AlpmBuilder withArch(String arch) {
            this.qualifiers.put("arch", arch);
            return this;
        }

        /**
         * Removes the package architecture.
         *
         * @return this builder instance
         */
        public AlpmBuilder withoutArch() {
            this.qualifiers.remove("arch");
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public PackageURL build() throws MalformedPackageURLException {
            Objects.requireNonNull(this.name);
            Objects.requireNonNull(this.vendor);
            return PackageURLBuilder.aPackageURL()
                    .withType(PackageURL.StandardTypes.ALPM)
                    .withNamespace(this.vendor)
                    .withName(this.name)
                    .withVersion(this.version)
                    .withQualifiers(!this.qualifiers.isEmpty() ? this.qualifiers : null)
                    .withSubpath(this.subpath)
                    .build();
        }
    }

    /**
     * Builder for Alpine Linux APK-based packages.
     */
    public static final class ApkBuilder extends Builder<ApkBuilder> {
        private @Nullable String vendor;

        private @Nullable String name;

        private @Nullable String version;

        private ApkBuilder() {}

        /**
         * Sets the vendor such as alpine or openwrt. It is not case-sensitive and must be lowercased.
         *
         * @param vendor the vendor such as alpine or openwrt
         * @return this builder instance
         */
        public ApkBuilder withVendor(String vendor) {
            this.vendor = vendor;
            return this;
        }

        /**
         * Removes the vendor.
         *
         * @return this builder instance
         */
        public ApkBuilder withoutVendor() {
            this.vendor = null;
            return this;
        }

        /**
         * Sets the name.
         *
         * @param name the name
         * @return this builder instance
         */
        public ApkBuilder withName(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the version.
         *
         * @param version the version string
         * @return this builder instance
         */
        public ApkBuilder withVersion(String version) {
            this.version = version;
            return this;
        }

        /**
         * Removes the version.
         *
         * @return this builder instance
         */
        public ApkBuilder withoutVersion() {
            this.version = null;
            return this;
        }

        /**
         * Sets the package architecture.
         *
         * @param arch the package architecture
         * @return this builder instance
         */
        public ApkBuilder withArch(String arch) {
            this.qualifiers.put("arch", arch);
            return this;
        }

        /**
         * Removes the package architecture.
         *
         * @return this builder instance
         */
        public ApkBuilder withoutArch() {
            this.qualifiers.remove("arch");
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public PackageURL build() throws MalformedPackageURLException {
            Objects.requireNonNull(this.name);
            Objects.requireNonNull(this.vendor);
            return PackageURLBuilder.aPackageURL()
                    .withType(PackageURL.StandardTypes.APK)
                    .withNamespace(this.vendor)
                    .withName(this.name)
                    .withVersion(this.version)
                    .withQualifiers(!this.qualifiers.isEmpty() ? this.qualifiers : null)
                    .withSubpath(this.subpath)
                    .build();
        }
    }

    /**
     * Builder for Bazel modules as specified in
     * <a href="https://bazel.build/external/module">Bazel modules</a>.
     */
    public static final class BazelBuilder extends Builder<BazelBuilder> {
        /**
         * The default repository is the Bazel Central Registry (BCR).
         */
        public static final String DEFAULT_REPOSITORY_URL = "https://bcr.bazel.build";

        private @Nullable String name;

        private @Nullable String version;

        private @Nullable String label;

        private BazelBuilder() {}

        /**
         * Sets the name as defined in the {@code MODULE.bazel} file.
         *
         * @param name the name
         * @return this builder instance
         */
        public BazelBuilder withName(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the version.
         *
         * @param version the version string
         * @return this builder instance
         */
        public BazelBuilder withVersion(String version) {
            this.version = version;
            return this;
        }

        /**
         * Removes the version.
         *
         * @return this builder instance
         */
        public BazelBuilder withoutVersion() {
            this.version = null;
            return this;
        }

        /**
         * Sets the subpath.
         *
         * @param subpath the subpath
         * @return this builder instance
         * @deprecated use {@link #withLabel(String)}
         */
        @Override
        @Deprecated
        public BazelBuilder withSubpath(String subpath) {
            return withLabel(subpath);
        }

        /**
         * Removes the subpath.
         *
         * @return this builder instance
         * @deprecated use {@link #withoutLabel()}
         */
        @Override
        @Deprecated
        public BazelBuilder withoutSubpath() {
            return withoutLabel();
        }

        /**
         * Sets the optional subpath which MAY refer to a label of a particular
         * package or target in the module
         * (<a href="https://bazel.build/concepts/labels">Labels</a>). The
         * label MUST NOT include a repo name and the leading {@code '//'} MUST
         * be omitted. When referring to targets, the label MUST include the
         * name of the target, separated from the package by {@code ':'}. If
         * there is no target name, subpath is assumed to refer to the whole
         * package.
         *
         * @param label the optional subpath
         * @return this builder instance
         */
        public BazelBuilder withLabel(String label) {
            this.label = label;
            return this;
        }

        /**
         * Removes the optional subpath.
         *
         * @return this builder instance
         */
        public BazelBuilder withoutLabel() {
            this.label = null;
            return this;
        }

        /**
         * The URL of the registry that hosts this Bazel module. If not specified, it defaults to the
         * {@link #DEFAULT_REPOSITORY_URL BCR URL}.
         *
         * @param registry the URL of the registry that hosts this Bazel module
         * @return this builder instance
         */
        public BazelBuilder withRegistry(String registry) {
            this.qualifiers.put("repository_url", registry);
            return this;
        }

        /**
         * Removes the URL of the registry that hosts this Bazel module.
         *
         * @return this builder instance
         */
        public BazelBuilder withoutRegistry() {
            this.qualifiers.remove("repository_url");
            return this;
        }

        /**
         * Returns the Bazel Central Registry (BCR).
         *
         * @return the Bazel Central Registry (BCR)
         */
        @Override
        public Optional<String> getDefaultRepositoryUrl() {
            return Optional.of(DEFAULT_REPOSITORY_URL);
        }

        /**
         * {@inheritDoc}
         */
        public PackageURL build() throws MalformedPackageURLException {
            Objects.requireNonNull(this.name);
            Objects.requireNonNull(this.version);
            return PackageURLBuilder.aPackageURL()
                    .withType(PackageURL.StandardTypes.BAZEL)
                    .withName(this.name)
                    .withVersion(this.version)
                    .withQualifiers(!this.qualifiers.isEmpty() ? this.qualifiers : null)
                    .withSubpath(this.label)
                    .build();
        }
    }

    /**
     * Builder for Bitbucket packages.
     */
    public static final class BitbucketBuilder extends Builder<BitbucketBuilder> {
        public static final String DEFAULT_REPOSITORY_URL = "https://bitbucket.org";

        private @Nullable String organization;

        private @Nullable String name;

        private @Nullable String commit;

        private BitbucketBuilder() {}

        /**
         * Sets the user or organization.
         *
         * @param organization the user or organization
         * @return this builder instance
         */
        public BitbucketBuilder withOrganization(String organization) {
            this.organization = organization;
            return this;
        }

        /**
         * Removes the user or organization.
         *
         * @return this builder instance
         */
        public BitbucketBuilder withoutOrganization() {
            this.organization = null;
            return this;
        }

        /**
         * Sets the repository name.
         *
         * @param name the name
         * @return this builder instance
         */
        public BitbucketBuilder withRepositoryName(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the commit or tag.
         *
         * @param commit the commit or tag
         * @return this builder instance
         */
        public BitbucketBuilder withCommit(String commit) {
            this.commit = commit;
            return this;
        }

        /**
         * Removes the commit or tag.
         *
         * @return this builder instance
         */
        public BitbucketBuilder withoutCommit() {
            this.commit = null;
            return this;
        }

        /**
         * Returns the default Bitbucket repository URL.
         *
         * @return the default Bitbucket repository URL
         */
        @Override
        public Optional<String> getDefaultRepositoryUrl() {
            return Optional.of(DEFAULT_REPOSITORY_URL);
        }

        /**
         * {@inheritDoc}
         */
        public PackageURL build() throws MalformedPackageURLException {
            Objects.requireNonNull(this.name);
            Objects.requireNonNull(this.organization);
            return PackageURLBuilder.aPackageURL()
                    .withType(PackageURL.StandardTypes.BITBUCKET)
                    .withNamespace(this.organization)
                    .withName(this.name)
                    .withVersion(this.commit)
                    .withQualifiers(!this.qualifiers.isEmpty() ? this.qualifiers : null)
                    .withSubpath(this.subpath)
                    .build();
        }
    }

    /**
     * Builder for Bitnami packages.
     */
    public static final class BitnamiBuilder extends Builder<BitnamiBuilder> {
        public static final String DEFAULT_REPOSITORY_URL = "https://downloads.bitnami.com/files/stacksmith";

        private @Nullable String name;

        private @Nullable String version;

        private BitnamiBuilder() {}

        /**
         * Sets the name.
         *
         * @param name the name
         * @return this builder instance
         */
        public BitnamiBuilder withName(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the full package version, including version and revision.
         *
         * @param version the version string
         * @return this builder instance
         */
        public BitnamiBuilder withVersion(String version) {
            this.version = version;
            return this;
        }

        /**
         * Removes the full package version.
         *
         * @return this builder instance
         */
        public BitnamiBuilder withoutVersion() {
            this.version = null;
            return this;
        }

        /**
         * Sets the package architecture. Available values are amd64 (default) and arm64.
         *
         * @param arch the package architecture
         * @return this builder instance
         */
        public BitnamiBuilder withArch(String arch) {
            this.qualifiers.put("arch", arch);
            return this;
        }

        /**
         * Removes the package architecture.
         *
         * @return this builder instance
         */
        public BitnamiBuilder withoutArch() {
            this.qualifiers.remove("arch");
            return this;
        }

        /**
         * Sets the distribution associated with the package.
         *
         * @param distro the distribution associated with the package
         * @return this builder instance
         */
        public BitnamiBuilder withDistro(String distro) {
            this.qualifiers.put("distro", distro);
            return this;
        }

        /**
         * Removes the distribution associated with the package.
         *
         * @return this builder instance
         */
        public BitnamiBuilder withoutDistro() {
            this.qualifiers.remove("distro");
            return this;
        }

        /**
         * Returns the default Bitnami repository URL.
         *
         * @return the default Bitnami repository URL
         */
        @Override
        public Optional<String> getDefaultRepositoryUrl() {
            return Optional.of(DEFAULT_REPOSITORY_URL);
        }

        /**
         * {@inheritDoc}
         */
        public PackageURL build() throws MalformedPackageURLException {
            Objects.requireNonNull(this.name);
            return PackageURLBuilder.aPackageURL()
                    .withType(PackageURL.StandardTypes.BITNAMI)
                    .withName(this.name)
                    .withVersion(this.version)
                    .withQualifiers(!this.qualifiers.isEmpty() ? this.qualifiers : null)
                    .withSubpath(this.subpath)
                    .build();
        }
    }

    /**
     * Builder for Cargo packages for Rust.
     */
    public static final class CargoBuilder extends Builder<CargoBuilder> {
        private @Nullable String name;

        private @Nullable String version;

        private CargoBuilder() {}

        /**
         * Sets the repository name.
         *
         * @param name the repository name
         * @return this builder instance
         */
        public CargoBuilder withName(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the package version.
         *
         * @param version the package version
         * @return this builder instance
         */
        public CargoBuilder withVersion(String version) {
            this.version = version;
            return this;
        }

        /**
         * Removes the version.
         *
         * @return this builder instance
         */
        public CargoBuilder withoutVersion() {
            this.version = null;
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public PackageURL build() throws MalformedPackageURLException {
            Objects.requireNonNull(this.name);
            return PackageURLBuilder.aPackageURL()
                    .withType(PackageURL.StandardTypes.CARGO)
                    .withName(this.name)
                    .withVersion(this.version)
                    .withQualifiers(!this.qualifiers.isEmpty() ? this.qualifiers : null)
                    .withSubpath(this.subpath)
                    .build();
        }
    }

    /**
     * Builder for CocoaPods pods.
     */
    public static final class CocoapodsBuilder extends Builder<CocoapodsBuilder> {
        public static final String DEFAULT_REPOSITORY_URL = "https://cdn.cocoapods.org/";

        private @Nullable String name;

        private @Nullable String packageVersion;

        private CocoapodsBuilder() {}

        /**
         * Sets the pod name.
         *
         * @param name the pod name
         * @return this builder instance
         */
        public CocoapodsBuilder withPodName(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the package version.
         *
         * @param packageVersion the version string
         * @return this builder instance
         */
        public CocoapodsBuilder withPackageVersion(String packageVersion) {
            this.packageVersion = packageVersion;
            return this;
        }

        /**
         * Removes the package version.
         *
         * @return this builder instance
         */
        public CocoapodsBuilder withoutVersion() {
            this.packageVersion = null;
            return this;
        }

        /**
         * Sets the pods subspec.
         *
         * @param subpath the subpath
         * @return this builder instance
         */
        @Override
        public CocoapodsBuilder withSubpath(String subpath) {
            this.subpath = subpath;
            return this;
        }

        /**
         * Removes the pods subspec.
         *
         * @return this builder instance
         */
        @Override
        public CocoapodsBuilder withoutSubpath() {
            this.subpath = null;
            return this;
        }

        /**
         * Returns the default CocoaPods repository URL.
         *
         * @return the default CocoaPods repository URL
         */
        @Override
        public Optional<String> getDefaultRepositoryUrl() {
            return Optional.of(DEFAULT_REPOSITORY_URL);
        }

        /**
         * {@inheritDoc}
         */
        public PackageURL build() throws MalformedPackageURLException {
            Objects.requireNonNull(this.name);
            return PackageURLBuilder.aPackageURL()
                    .withType(PackageURL.StandardTypes.COCOAPODS)
                    .withName(this.name)
                    .withVersion(this.packageVersion)
                    .withQualifiers(!this.qualifiers.isEmpty() ? this.qualifiers : null)
                    .withSubpath(this.subpath)
                    .build();
        }
    }

    /**
     * Builder for Composer PHP packages
     */
    public static final class ComposerBuilder extends Builder<ComposerBuilder> {
        public static final String DEFAULT_REPOSITORY_URL = "https://packagist.org";

        private @Nullable String vendor;

        private @Nullable String name;

        private @Nullable String version;

        private ComposerBuilder() {}

        /**
         * Sets the namespace.
         *
         * @param vendor the namespace
         * @return this builder instance
         */
        public ComposerBuilder withVendor(String vendor) {
            this.vendor = vendor;
            return this;
        }

        /**
         * Removes the namespace.
         *
         * @return this builder instance
         */
        public ComposerBuilder withoutVendor() {
            this.vendor = null;
            return this;
        }

        /**
         * Sets the name.
         *
         * @param name the name
         * @return this builder instance
         */
        public ComposerBuilder withName(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the version.
         *
         * @param version the version string
         * @return this builder instance
         */
        public ComposerBuilder withVersion(String version) {
            this.version = version;
            return this;
        }

        /**
         * Removes the version.
         *
         * @return this builder instance
         */
        public ComposerBuilder withoutVersion() {
            this.version = null;
            return this;
        }

        /**
         * Returns the default Composer repository URL.
         *
         * @return the default Composer repository URL
         */
        @Override
        public Optional<String> getDefaultRepositoryUrl() {
            return Optional.of(DEFAULT_REPOSITORY_URL);
        }

        /**
         * {@inheritDoc}
         */
        public PackageURL build() throws MalformedPackageURLException {
            Objects.requireNonNull(this.name);
            Objects.requireNonNull(this.vendor);
            return PackageURLBuilder.aPackageURL()
                    .withType(PackageURL.StandardTypes.COMPOSER)
                    .withNamespace(this.vendor)
                    .withName(this.name)
                    .withVersion(this.version)
                    .withQualifiers(!this.qualifiers.isEmpty() ? this.qualifiers : null)
                    .withSubpath(this.subpath)
                    .build();
        }
    }

    /**
     * Builder for Conan C/C++ packages. The purl is designed to closely resemble the
     * Conan-native {@code <package-name>/<package-version>@<user>/<channel>} syntax
     * for package references as specified in
     * <a href="https://docs.conan.io/en/1.46/cheatsheet.html#package-terminology">Package terminology</a>.
     */
    public static final class ConanBuilder extends Builder<ConanBuilder> {
        private @Nullable String vendor;

        private @Nullable String name;

        private @Nullable String packageVersion;

        private ConanBuilder() {}

        /**
         * Sets the vendor of the package.
         *
         * @param vendor the vendor of the package
         * @return this builder instance
         */
        public ConanBuilder withVendor(String vendor) {
            this.vendor = vendor;
            return this;
        }

        /**
         * Removes the vendor of the package.
         *
         * @return this builder instance
         */
        public ConanBuilder withoutVendor() {
            this.vendor = null;
            return this;
        }

        /**
         * Sets the Conan {@code <package-name>}.
         *
         * @param name the name
         * @return this builder instance
         */
        public ConanBuilder withPackageName(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the Conan {@code <package-version>}.
         *
         * @param packageVersion the version string
         * @return this builder instance
         */
        public ConanBuilder withPackageVersion(String packageVersion) {
            this.packageVersion = packageVersion;
            return this;
        }

        /**
         * Removes the Conan {@code <package-version>}.
         *
         * @return this builder instance
         */
        public ConanBuilder withoutPackageVersion() {
            this.packageVersion = null;
            return this;
        }

        /**
         * Sets the Conan {@code <user>}. Only required if the Conan package was published with {@code <user>}.
         *
         * @param user the Conan {@code <user>}
         * @return this builder instance
         */
        public ConanBuilder withUser(String user) {
            this.qualifiers.put("user", user);
            return this;
        }

        /**
         * Remove the Conan {@code <user>}.
         *
         * @return this builder instance
         */
        public ConanBuilder withoutUser() {
            this.qualifiers.remove("user");
            return this;
        }

        /**
         * Sets the Conan {@code <channel>}. Only required if the Conan package was published with Conan {@code <channel>}.
         *
         * @param channel the Conan {@code <channel>}
         * @return this builder instance
         */
        public ConanBuilder withChannel(String channel) {
            this.qualifiers.put("channel", channel);
            return this;
        }

        /**
         * Removes the Conan {@code <channel>}.
         *
         * @return this builder instance
         */
        public ConanBuilder withoutChannel() {
            this.qualifiers.remove("channel");
            return this;
        }

        /**
         * Sets the Conan recipe revision (optional). If omitted, the PURL
         * refers to the latest recipe revision available for the given version.
         *
         * @param recipleRevision the Conan recipe revision
         * @return this builder instance
         */
        public ConanBuilder withRecipeRevision(String recipleRevision) {
            this.qualifiers.put("rrev", recipleRevision);
            return this;
        }

        /**
         * Removes the Conan recipe revision.
         *
         * @return this builder instance
         */
        public ConanBuilder withoutRecipeRevision() {
            this.qualifiers.remove("rrev");
            return this;
        }

        /**
         * Sets the Conan package revision (optional). If omitted, the PURL
         * refers to the latest package revision available for the given version and recipe revision.
         *
         * @param packageRevision the Conan package revision
         * @return this builder instance
         */
        public ConanBuilder withPackageRevision(String packageRevision) {
            this.qualifiers.put("prev", packageRevision);
            return this;
        }

        /**
         * Removes the Conan package revision.
         *
         * @return this builder instance
         */
        public ConanBuilder withoutPackageRevision() {
            this.qualifiers.remove("prev");
            return this;
        }

        /**
         * Sets the Conan package architecture.
         *
         * @param arch the architecture string
         * @return this builder instance
         */
        public ConanBuilder withArch(String arch) {
            this.qualifiers.put("arch", arch);
            return this;
        }

        /**
         * Removes the Conan package architecture.
         *
         * @return this builder instance
         */
        public ConanBuilder withoutArch() {
            this.qualifiers.remove("arch");
            return this;
        }

        /**
         * Sets the Conan build type.
         *
         * @param buildType the build type
         * @return this builder instance
         */
        public ConanBuilder withBuildType(String buildType) {
            this.qualifiers.put("build_type", buildType);
            return this;
        }

        /**
         * Removes the Conan build type.
         *
         * @return this builder instance
         */
        public ConanBuilder withoutBuildType() {
            this.qualifiers.remove("build_type");
            return this;
        }

        /**
         * Sets the Conan compiler.
         *
         * @param compiler the compiler name
         * @return this builder instance
         */
        public ConanBuilder withCompiler(String compiler) {
            this.qualifiers.put("compiler", compiler);
            return this;
        }

        /**
         * Removes the Conan compiler.
         *
         * @return this builder instance
         */
        public ConanBuilder withoutCompiler() {
            this.qualifiers.remove("compiler");
            return this;
        }

        /**
         * Sets the Conan compiler runtime.
         *
         * @param compilerRuntime the compiler runtime string
         * @return this builder instance
         */
        public ConanBuilder withCompilerRuntime(String compilerRuntime) {
            this.qualifiers.put("compiler.runtime", compilerRuntime);
            return this;
        }

        /**
         * Removes the Conan compiler runtime.
         *
         * @return this builder instance
         */
        public ConanBuilder withoutCompilerRuntime() {
            this.qualifiers.remove("compiler.runtime");
            return this;
        }

        /**
         * Sets the Conan compiler version.
         *
         * @param compilerVersion the compiler version
         * @return this builder instance
         */
        public ConanBuilder withCompilerVersion(String compilerVersion) {
            this.qualifiers.put("compiler.version", compilerVersion);
            return this;
        }

        /**
         * Removes the Conan compiler version.
         *
         * @return this builder instance
         */
        public ConanBuilder withoutCompilerVersion() {
            this.qualifiers.remove("compiler.version");
            return this;
        }

        /**
         * Sets the Conan operating system.
         *
         * @param os the operating system string
         * @return this builder instance
         */
        public ConanBuilder withOs(String os) {
            this.qualifiers.put("os", os);
            return this;
        }

        /**
         * Removes the Conan operating system.
         *
         * @return this builder instance
         */
        public ConanBuilder withoutOs() {
            this.qualifiers.remove("os");
            return this;
        }

        /**
         * Sets whether the Conan package is shared.
         *
         * @param shared {@code "True"} if shared or {@code "False"} if not
         * @return this builder instance
         */
        public ConanBuilder withShared(String shared) {
            this.qualifiers.put("shared", shared);
            return this;
        }

        /**
         * Removes the shared value.
         *
         * @return this builder instance
         */
        public ConanBuilder withoutShared() {
            this.qualifiers.remove("shared");
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public PackageURL build() throws MalformedPackageURLException {
            Objects.requireNonNull(this.name);
            return PackageURLBuilder.aPackageURL()
                    .withType(PackageURL.StandardTypes.CONAN)
                    .withNamespace(this.vendor)
                    .withName(this.name)
                    .withVersion(this.packageVersion)
                    .withQualifiers(!this.qualifiers.isEmpty() ? this.qualifiers : null)
                    .withSubpath(this.subpath)
                    .build();
        }
    }

    /**
     * Builder for Conda packages.
     */
    public static final class CondaBuilder extends Builder<CondaBuilder> {
        public static final String DEFAULT_REPOSITORY_URL = "https://repo.anaconda.com";

        private @Nullable String name;

        private @Nullable String version;

        private CondaBuilder() {}

        /**
         * Sets the package name.
         *
         * @param name the package name
         * @return this builder instance
         */
        public CondaBuilder withName(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the package version.
         *
         * @param version the package version
         * @return this builder instance
         */
        public CondaBuilder withVersion(String version) {
            this.version = version;
            return this;
        }

        /**
         * Removes the package version.
         *
         * @return this builder instance
         */
        public CondaBuilder withoutVersion() {
            this.version = null;
            return this;
        }

        /**
         * Sets the build string.
         *
         * @param build the build string
         * @return this builder instance
         */
        public CondaBuilder withBuild(String build) {
            this.qualifiers.put("build", build);
            return this;
        }

        /**
         * Removes the build string.
         *
         * @return this builder instance
         */
        public CondaBuilder withoutBuild() {
            this.qualifiers.remove("build");
            return this;
        }

        /**
         * Sets the package stored location.
         *
         * @param channel the package stored location
         * @return this builder instance
         */
        public CondaBuilder withChannel(String channel) {
            this.qualifiers.put("channel", channel);
            return this;
        }

        /**
         * Removes the package stored location.
         *
         * @return this builder instance
         */
        public CondaBuilder withoutChannel() {
            this.qualifiers.remove("channel");
            return this;
        }

        /**
         * Sets the associated platform.
         *
         * @param subdir the associated platform
         * @return this builder instance
         */
        public CondaBuilder withSubdir(String subdir) {
            this.qualifiers.put("subdir", subdir);
            return this;
        }

        /**
         * Removes the associated platform.
         *
         * @return this builder instance
         */
        public CondaBuilder withoutSubdir() {
            this.qualifiers.remove("subdir");
            return this;
        }

        /**
         * Sets the package type.
         *
         * @param packageType the package type
         * @return this builder instance
         */
        public CondaBuilder withPackageType(String packageType) {
            this.qualifiers.put("type", packageType);
            return this;
        }

        /**
         * Removes the package type.
         *
         * @return this builder instance
         */
        public CondaBuilder withoutPackageType() {
            this.qualifiers.remove("type");
            return this;
        }

        /**
         * Returns the default Conda repository URL.
         *
         * @return the default Conda repository URL
         */
        @Override
        public Optional<String> getDefaultRepositoryUrl() {
            return Optional.of(DEFAULT_REPOSITORY_URL);
        }

        /**
         * {@inheritDoc}
         */
        public PackageURL build() throws MalformedPackageURLException {
            Objects.requireNonNull(this.name);
            return PackageURLBuilder.aPackageURL()
                    .withType(PackageURL.StandardTypes.CONDA)
                    .withName(this.name)
                    .withVersion(this.version)
                    .withQualifiers(!this.qualifiers.isEmpty() ? this.qualifiers : null)
                    .withSubpath(this.subpath)
                    .build();
        }
    }

    /**
     * Builder for Perl package distributions published on CPAN.
     */
    public static final class CpanBuilder extends Builder<CpanBuilder> {
        public static final String DEFAULT_REPOSITORY_URL = "https://www.cpan.org/";

        private @Nullable String cpanId;

        private @Nullable String name;

        private @Nullable String version;

        private CpanBuilder() {}

        /**
         * Sets the namespace.
         *
         * @param cpanId the namespace
         * @return this builder instance
         */
        public CpanBuilder withCpanId(String cpanId) {
            this.cpanId = cpanId;
            return this;
        }

        /**
         * Removes the namespace.
         *
         * @return this builder instance
         */
        public CpanBuilder withoutCpanId() {
            this.cpanId = null;
            return this;
        }

        /**
         * Sets the distribution name.
         *
         * @param name the name
         * @return this builder instance
         */
        public CpanBuilder withDistributionName(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the version.
         *
         * @param version the version string
         * @return this builder instance
         */
        public CpanBuilder withVersion(String version) {
            this.version = version;
            return this;
        }

        /**
         * Removes the version.
         *
         * @return this builder instance
         */
        public CpanBuilder withoutVersion() {
            this.version = null;
            return this;
        }

        /**
         * Returns the default CPAN repository URL.
         *
         * @return the default CPAN repository URL.
         */
        @Override
        public Optional<String> getDefaultRepositoryUrl() {
            return Optional.of(DEFAULT_REPOSITORY_URL);
        }

        /**
         * {@inheritDoc}
         */
        public PackageURL build() throws MalformedPackageURLException {
            Objects.requireNonNull(this.name);
            Objects.requireNonNull(this.cpanId);
            return PackageURLBuilder.aPackageURL()
                    .withType(PackageURL.StandardTypes.CPAN)
                    .withNamespace(this.cpanId)
                    .withName(this.name)
                    .withVersion(this.version)
                    .withQualifiers(!this.qualifiers.isEmpty() ? this.qualifiers : null)
                    .withSubpath(this.subpath)
                    .build();
        }
    }

    /**
     * Builder for CRAN R packages.
     */
    public static final class CranBuilder extends Builder<CranBuilder> {
        public static final String DEFAULT_REPOSITORY_URL = "https://cran.r-project.org";

        private @Nullable String name;

        private @Nullable String version;

        private CranBuilder() {}

        /**
         * Sets the package name.
         *
         * @param name the package name
         * @return this builder instance
         */
        public CranBuilder withName(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the package version.
         *
         * @param version the package version
         * @return this builder instance
         */
        public CranBuilder withVersion(String version) {
            this.version = version;
            return this;
        }

        /**
         * Removes the version.
         *
         * @return this builder instance
         */
        public CranBuilder withoutVersion() {
            this.version = null;
            return this;
        }

        /**
         * Returns the default CRAN repository URL.
         *
         * @return the default CRAN repository URL
         */
        @Override
        public Optional<String> getDefaultRepositoryUrl() {
            return Optional.of(DEFAULT_REPOSITORY_URL);
        }

        /**
         * {@inheritDoc}
         */
        public PackageURL build() throws MalformedPackageURLException {
            Objects.requireNonNull(this.name);
            return PackageURLBuilder.aPackageURL()
                    .withType(PackageURL.StandardTypes.CRAN)
                    .withName(this.name)
                    .withVersion(this.version)
                    .withQualifiers(!this.qualifiers.isEmpty() ? this.qualifiers : null)
                    .withSubpath(this.subpath)
                    .build();
        }
    }

    /**
     * Builder for Debian packages, Debian derivatives, and Ubuntu packages.
     */
    public static final class DebBuilder extends Builder<DebBuilder> {
        private @Nullable String vendor;

        private @Nullable String name;

        private @Nullable String version;

        private DebBuilder() {}

        /**
         * Sets the &quot;vendor&quot; name such as &quot;debian&quot; or &quot;ubuntu&quot;.
         *
         * @param vendor the vendor
         * @return this builder instance
         */
        public DebBuilder withVendor(String vendor) {
            this.vendor = vendor;
            return this;
        }

        /**
         * Sets the name.
         *
         * @param name the name
         * @return this builder instance
         */
        public DebBuilder withName(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the version of the binary (or source) package.
         *
         * @param version the version string
         * @return this builder instance
         */
        public DebBuilder withVersion(String version) {
            this.version = version;
            return this;
        }

        /**
         * Removes the version of the binary (or source) package.
         *
         * @return this builder instance
         */
        public DebBuilder withoutVersion() {
            this.version = null;
            return this;
        }

        /**
         * Sets the package architecture.
         *
         * @param arch the package architecture
         * @return this builder instance
         */
        public DebBuilder withArch(String arch) {
            this.qualifiers.put("arch", arch);
            return this;
        }

        /**
         * Removes the package architecture.
         *
         * @return this builder instance
         */
        public DebBuilder withoutArch() {
            this.qualifiers.remove("arch");
            return this;
        }

        /**
         * Sets the distribution associated with the package.
         *
         * @param distro the distribution associated with the package
         * @return this builder instance
         */
        public DebBuilder withDistro(String distro) {
            this.qualifiers.put("distro", distro);
            return this;
        }

        /**
         * Removes the distribution associated with the package.
         *
         * @return this builder instance
         */
        public DebBuilder withoutDistro() {
            this.qualifiers.remove("distro");
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public PackageURL build() throws MalformedPackageURLException {
            Objects.requireNonNull(this.name);
            Objects.requireNonNull(this.vendor);
            return PackageURLBuilder.aPackageURL()
                    .withType(PackageURL.StandardTypes.DEB)
                    .withNamespace(this.vendor)
                    .withName(this.name)
                    .withVersion(this.version)
                    .withQualifiers(!this.qualifiers.isEmpty() ? this.qualifiers : null)
                    .withSubpath(this.subpath)
                    .build();
        }
    }

    /**
     * Builder for Docker images.
     */
    public static final class DockerBuilder extends Builder<DockerBuilder> {
        public static final String DEFAULT_REPOSITORY_URL = "https://hub.docker.com";

        private @Nullable String registry;

        private @Nullable String name;

        private @Nullable String version;

        private DockerBuilder() {}

        /**
         * Sets the registry/user/organization.
         *
         * @param registry the namespace
         * @return this builder instance
         */
        public DockerBuilder withRegistry(String registry) {
            this.registry = registry;
            return this;
        }

        /**
         * Removes the registry/user/organization.
         *
         * @return this builder instance
         */
        public DockerBuilder withoutRegistry() {
            this.registry = null;
            return this;
        }

        /**
         * Sets the name.
         *
         * @param name the name
         * @return this builder instance
         */
        public DockerBuilder withName(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the sha256 image id or tag. Since tags can be moved, a sha256 image id is preferred.
         *
         * @param version the version string
         * @return this builder instance
         */
        public DockerBuilder withVersion(String version) {
            this.version = version;
            return this;
        }

        /**
         * Removes the sha256 image id or tag.
         *
         * @return this builder instance
         */
        public DockerBuilder withoutVersion() {
            this.version = null;
            return this;
        }

        /**
         * Returns the default Docker repository URL.
         *
         * @return the default Docker repository URL
         */
        @Override
        public Optional<String> getDefaultRepositoryUrl() {
            return Optional.of(DEFAULT_REPOSITORY_URL);
        }

        /**
         * {@inheritDoc}
         */
        public PackageURL build() throws MalformedPackageURLException {
            Objects.requireNonNull(this.name);
            return PackageURLBuilder.aPackageURL()
                    .withType(PackageURL.StandardTypes.DOCKER)
                    .withNamespace(this.registry)
                    .withName(this.name)
                    .withVersion(this.version)
                    .withQualifiers(!this.qualifiers.isEmpty() ? this.qualifiers : null)
                    .withSubpath(this.subpath)
                    .build();
        }
    }

    /**
     * Builder for RubyGems.
     */
    public static final class GemBuilder extends Builder<GemBuilder> {
        public static final String DEFAULT_REPOSITORY_URL = "https://rubygems.org";

        private @Nullable String name;

        private @Nullable String version;

        private GemBuilder() {}

        /**
         * Sets the name.
         *
         * @param name the name
         * @return this builder instance
         */
        public GemBuilder withName(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the version.
         *
         * @param version the version string
         * @return this builder instance
         */
        public GemBuilder withVersion(String version) {
            this.version = version;
            return this;
        }

        /**
         * Removes the version.
         *
         * @return this builder instance
         */
        public GemBuilder withoutVersion() {
            this.version = null;
            return this;
        }

        /**
         * Sets an alternative platform, such as java for JRuby. The implied default is ruby for Ruby MRI.
         *
         * @param platform the platform string
         * @return this builder instance
         */
        public GemBuilder withPlatform(String platform) {
            this.qualifiers.put("platform", platform);
            return this;
        }

        /**
         * Removes the platform.
         *
         * @return this builder instance
         */
        public GemBuilder withoutPlatform() {
            this.qualifiers.remove("platform");
            return this;
        }

        /**
         * Returns the default RubyGems repository URL.
         *
         * @return the default RubyGems repository URL.
         */
        @Override
        public Optional<String> getDefaultRepositoryUrl() {
            return Optional.of(DEFAULT_REPOSITORY_URL);
        }

        /**
         * {@inheritDoc}
         */
        public PackageURL build() throws MalformedPackageURLException {
            Objects.requireNonNull(this.name);
            return PackageURLBuilder.aPackageURL()
                    .withType(PackageURL.StandardTypes.GEM)
                    .withName(this.name)
                    .withVersion(this.version)
                    .withQualifiers(!this.qualifiers.isEmpty() ? this.qualifiers : null)
                    .withSubpath(this.subpath)
                    .build();
        }
    }

    /**
     * The generic type is for plain, generic packages that do not fit anywhere
     * else such as for &quot;upstream-from-distro&quot; packages. In
     * particular this is handy for a plain version control
     * repository such as a bare git repo in combination with a {@code vcs_url}.
     */
    public static final class GenericBuilder extends Builder<GenericBuilder> {
        private @Nullable String namespace;

        private @Nullable String name;

        private @Nullable String version;

        private GenericBuilder() {}

        /**
         * Sets the namespace.
         *
         * @param namespace the namespace
         * @return this builder instance
         */
        public GenericBuilder withNamespace(String namespace) {
            this.namespace = namespace;
            return this;
        }

        /**
         * Removes the namespace.
         *
         * @return this builder instance
         */
        public GenericBuilder withoutNamespace() {
            this.namespace = null;
            return this;
        }

        /**
         * Sets the name.
         *
         * @param name the name
         * @return this builder instance
         */
        public GenericBuilder withName(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the version.
         *
         * @param version the version string
         * @return this builder instance
         */
        public GenericBuilder withVersion(String version) {
            this.version = version;
            return this;
        }

        /**
         * Removes the version.
         *
         * @return this builder instance
         */
        public GenericBuilder withoutVersion() {
            this.version = null;
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public PackageURL build() throws MalformedPackageURLException {
            Objects.requireNonNull(this.name);
            return PackageURLBuilder.aPackageURL()
                    .withType(PackageURL.StandardTypes.GENERIC)
                    .withNamespace(this.namespace)
                    .withName(this.name)
                    .withVersion(this.version)
                    .withQualifiers(!this.qualifiers.isEmpty() ? this.qualifiers : null)
                    .withSubpath(this.subpath)
                    .build();
        }
    }

    /**
     * Builder for GitHub packages.
     */
    public static final class GithubBuilder extends Builder<GithubBuilder> {
        public static final String DEFAULT_REPOSITORY_URL = "https://github.com";

        private @Nullable String organization;

        private @Nullable String repositoryName;

        private @Nullable String commit;

        private GithubBuilder() {}

        /**
         * Sets the namespace.
         *
         * @param organization the namespace
         * @return this builder instance
         */
        public GithubBuilder withOrganization(String organization) {
            this.organization = organization;
            return this;
        }

        /**
         * Removes the namespace.
         *
         * @return this builder instance
         */
        public GithubBuilder withoutOrganization() {
            this.organization = null;
            return this;
        }

        /**
         * Sets the repository name.
         *
         * @param repositoryName the repository name
         * @return this builder instance
         */
        public GithubBuilder withRepositoryName(String repositoryName) {
            this.repositoryName = repositoryName;
            return this;
        }

        /**
         * Sets the commit or tag.
         *
         * @param commit the commit or tag
         * @return this builder instance
         */
        public GithubBuilder withCommit(String commit) {
            this.commit = commit;
            return this;
        }

        /**
         * Removes the commit or tag.
         *
         * @return this builder instance
         */
        public GithubBuilder withoutCommit() {
            this.commit = null;
            return this;
        }

        /**
         * Gets the default GitHub repository URL.
         *
         * @return the default GitHub repository URL
         */
        @Override
        public Optional<String> getDefaultRepositoryUrl() {
            return Optional.of(DEFAULT_REPOSITORY_URL);
        }

        /**
         * {@inheritDoc}
         */
        public PackageURL build() throws MalformedPackageURLException {
            Objects.requireNonNull(this.repositoryName);
            return PackageURLBuilder.aPackageURL()
                    .withType(PackageURL.StandardTypes.GITHUB)
                    .withNamespace(this.organization)
                    .withName(this.repositoryName)
                    .withVersion(this.commit)
                    .withQualifiers(!this.qualifiers.isEmpty() ? this.qualifiers : null)
                    .withSubpath(this.subpath)
                    .build();
        }
    }

    /**
     * Builder for Go packages.
     */
    public static final class GolangBuilder extends Builder<GolangBuilder> {
        private @Nullable String namespace;

        private @Nullable String name;

        private @Nullable String version;

        private GolangBuilder() {}

        /**
         * Sets the namespace.
         *
         * @param namespace the namespace
         * @return this builder instance
         */
        public GolangBuilder withNamespace(String namespace) {
            this.namespace = namespace;
            return this;
        }

        /**
         * Sets the name.
         *
         * @param name the name
         * @return this builder instance
         */
        public GolangBuilder withName(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the version.
         *
         * @param version the version
         * @return this builder instance
         */
        public GolangBuilder withVersion(String version) {
            this.version = version;
            return this;
        }

        /**
         * Removes the version.
         *
         * @return this builder instance
         */
        public GolangBuilder withoutVersion() {
            this.version = null;
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public PackageURL build() throws MalformedPackageURLException {
            Objects.requireNonNull(this.namespace);
            Objects.requireNonNull(this.name);
            return PackageURLBuilder.aPackageURL()
                    .withType(PackageURL.StandardTypes.GOLANG)
                    .withNamespace(this.namespace)
                    .withName(this.name)
                    .withVersion(this.version)
                    .withQualifiers(!this.qualifiers.isEmpty() ? this.qualifiers : null)
                    .withSubpath(this.subpath)
                    .build();
        }
    }

    /**
     * Builder for Haskell packages.
     */
    public static final class HackageBuilder extends Builder<HackageBuilder> {
        public static final String DEFAULT_REPOSITORY_URL = "https://hackage.haskell.org";

        private @Nullable String name;

        private @Nullable String version;

        private HackageBuilder() {}

        /**
         * Sets the name.
         *
         * @param name the name
         * @return this builder instance
         */
        public HackageBuilder withName(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the package version.
         *
         * @param version the package version
         * @return this builder instance
         */
        public HackageBuilder withVersion(String version) {
            this.version = version;
            return this;
        }

        /**
         * Removes the package version.
         *
         * @return this builder instance
         */
        public HackageBuilder withoutVersion() {
            this.version = null;
            return this;
        }

        /**
         * Gets the default Haskell repository URL.
         *
         * @return the default Haskell repository URL
         */
        @Override
        public Optional<String> getDefaultRepositoryUrl() {
            return Optional.of(DEFAULT_REPOSITORY_URL);
        }

        /**
         * {@inheritDoc}
         */
        public PackageURL build() throws MalformedPackageURLException {
            Objects.requireNonNull(this.name);
            return PackageURLBuilder.aPackageURL()
                    .withType(PackageURL.StandardTypes.HACKAGE)
                    .withName(this.name)
                    .withVersion(this.version)
                    .withQualifiers(!this.qualifiers.isEmpty() ? this.qualifiers : null)
                    .withSubpath(this.subpath)
                    .build();
        }
    }

    /**
     * Builder for Hex packages.
     */
    public static final class HexBuilder extends Builder<HexBuilder> {
        public static final String DEFAULT_REPOSITORY_URL = "https://repo.hex.pm";

        private @Nullable String organization;

        private @Nullable String name;

        private @Nullable String version;

        private HexBuilder() {}

        /**
         * Sets the organization for private packages.
         *
         * @param organization the organization for private packages
         * @return this builder instance
         */
        public HexBuilder withOrganization(String organization) {
            this.organization = organization;
            return this;
        }

        /**
         * Removes the organization.
         *
         * @return this builder instance
         */
        public HexBuilder withoutOrganization() {
            this.organization = null;
            return this;
        }

        /**
         * Sets the name.
         *
         * @param name the name
         * @return this builder instance
         */
        public HexBuilder withName(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the version.
         *
         * @param version the version
         * @return this builder instance
         */
        public HexBuilder withVersion(String version) {
            this.version = version;
            return this;
        }

        /**
         * Removes the version.
         *
         * @return this builder instance
         */
        public HexBuilder withoutVersion() {
            this.version = null;
            return this;
        }

        /**
         * Gets the default Hex repository URL.
         *
         * @return the default Hex repository URL
         */
        @Override
        public Optional<String> getDefaultRepositoryUrl() {
            return Optional.of(DEFAULT_REPOSITORY_URL);
        }

        /**
         * {@inheritDoc}
         */
        public PackageURL build() throws MalformedPackageURLException {
            Objects.requireNonNull(this.name);
            return PackageURLBuilder.aPackageURL()
                    .withType(PackageURL.StandardTypes.HEX)
                    .withNamespace(this.organization)
                    .withName(this.name)
                    .withVersion(this.version)
                    .withQualifiers(!this.qualifiers.isEmpty() ? this.qualifiers : null)
                    .withSubpath(this.subpath)
                    .build();
        }
    }

    /**
     * Builder for Hugging Face ML models.
     */
    public static final class HuggingfaceBuilder extends Builder<HuggingfaceBuilder> {
        private @Nullable String organization;

        private @Nullable String name;

        private @Nullable String commit;

        private HuggingfaceBuilder() {}

        /**
         * Sets the model repository username or organization.
         *
         * @param namespace the  model repository username or organization
         * @return this builder instance
         */
        public HuggingfaceBuilder withOrganization(String namespace) {
            this.organization = namespace;
            return this;
        }

        /**
         * Sets the model repository name.
         *
         * @param name the name
         * @return this builder instance
         */
        public HuggingfaceBuilder withName(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the model revision Git commit hash.
         *
         * @param commit model revision Git commit hash
         * @return this builder instance
         */
        public HuggingfaceBuilder withCommit(String commit) {
            this.commit = commit;
            return this;
        }

        /**
         * Removes the commit.
         *
         * @return this builder instance
         */
        public HuggingfaceBuilder withoutCommit() {
            this.commit = null;
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public PackageURL build() throws MalformedPackageURLException {
            Objects.requireNonNull(this.name);
            return PackageURLBuilder.aPackageURL()
                    .withType(PackageURL.StandardTypes.HUGGINGFACE)
                    .withNamespace(this.organization)
                    .withName(this.name)
                    .withVersion(this.commit)
                    .withQualifiers(!this.qualifiers.isEmpty() ? this.qualifiers : null)
                    .withSubpath(this.subpath)
                    .build();
        }
    }

    /**
     * Builder for Julia packages.
     */
    public static final class JuliaBuilder extends Builder<JuliaBuilder> {
        public static final String DEFAULT_REPOSITORY_URL = "https://github.com/JuliaRegistries/General";

        private @Nullable String name;

        private @Nullable String version;

        private JuliaBuilder() {}

        /**
         * Sets the package name (without a `.jl` suffix).
         *
         * @param name the package name
         * @return this builder instance
         */
        public JuliaBuilder withName(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the version.
         *
         * @param version the version
         * @return this builder instance
         */
        public JuliaBuilder withVersion(String version) {
            this.version = version;
            return this;
        }

        /**
         * Removes the version.
         *
         * @return this builder instance
         */
        public JuliaBuilder withoutVersion() {
            this.version = null;
            return this;
        }

        /**
         * Sets the Julia package UUID.
         * @param uuid the Julia package UUID
         *
         * @return this builder instance
         */
        public JuliaBuilder withUuid(String uuid) {
            this.qualifiers.put("uuid", uuid);
            return this;
        }

        /**
         * Gets the default Julia repository URL.
         *
         * @return the default Julia repository URL
         */
        @Override
        public Optional<String> getDefaultRepositoryUrl() {
            return Optional.of(DEFAULT_REPOSITORY_URL);
        }

        /**
         * {@inheritDoc}
         */
        public PackageURL build() throws MalformedPackageURLException {
            Objects.requireNonNull(this.name);
            return PackageURLBuilder.aPackageURL()
                    .withType(PackageURL.StandardTypes.JULIA)
                    .withName(this.name)
                    .withVersion(this.version)
                    .withQualifiers(!this.qualifiers.isEmpty() ? this.qualifiers : null)
                    .withSubpath(this.subpath)
                    .build();
        }
    }

    /**
     * Builder for Lua packages installed with LuaRocks.
     */
    public static final class LuarocksBuilder extends Builder<LuarocksBuilder> {
        public static final String DEFAULT_REPOSITORY_URL = "https://luarocks.org";

        private @Nullable String manifest;

        private @Nullable String name;

        private @Nullable String versionRevision;

        private LuarocksBuilder() {}

        /**
         * Sets the user manifest under which the package is registered.
         *
         * @param manifest the user manifest
         * @return this builder instance
         */
        public LuarocksBuilder withManifest(String manifest) {
            this.manifest = manifest;
            return this;
        }

        /**
         * Removes the user manifest.
         *
         * @return this builder instance
         */
        public LuarocksBuilder withoutManifest() {
            this.manifest = null;
            return this;
        }

        /**
         * Sets the LuaRocks package name.
         *
         * @param name the LuaRocks package name
         * @return this builder instance
         */
        public LuarocksBuilder withName(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the full package version, including module version and rockspec revision.
         *
         * @param versionRevision the full package version
         * @return this builder instance
         */
        public LuarocksBuilder withVersionRevision(String versionRevision) {
            this.versionRevision = versionRevision;
            return this;
        }

        /**
         * Removes the version.
         *
         * @return this builder instance
         */
        public LuarocksBuilder withoutVersionRevision() {
            this.versionRevision = null;
            return this;
        }

        /**
         * Sets the LuaRocks rocks server to be used; useful in case a private
         * server is used (optional). If omitted,
         * <a href="https://luarocks.org">https://luarocks.org</a> as the
         * default server is assumed.
         *
         * @param repositoryUrl the repository URL
         * @return this builder instance
         */
        @Override
        public LuarocksBuilder withRepositoryUrl(String repositoryUrl) {
            qualifiers.put("repository_url", repositoryUrl);
            return this;
        }

        /**
         * Gets the default Luarocks repository URL.
         *
         * @return the default Luarocks repository URL
         */
        @Override
        public Optional<String> getDefaultRepositoryUrl() {
            return Optional.of(DEFAULT_REPOSITORY_URL);
        }

        /**
         * {@inheritDoc}
         */
        public PackageURL build() throws MalformedPackageURLException {
            Objects.requireNonNull(this.name);
            return PackageURLBuilder.aPackageURL()
                    .withType(PackageURL.StandardTypes.LUAROCKS)
                    .withNamespace(this.manifest)
                    .withName(this.name)
                    .withVersion(this.versionRevision)
                    .withQualifiers(!this.qualifiers.isEmpty() ? this.qualifiers : null)
                    .withSubpath(this.subpath)
                    .build();
        }
    }

    /**
     * Builder for Maven JARs and related artifacts.
     */
    public static final class MavenBuilder extends Builder<MavenBuilder> {
        /**
         * The Maven Central repository is the public repository for Apache
         * Maven packages. This repository is also mirrored at
         * <a href="https://repo1.maven.org/maven2/">
         * https://repo1.maven.org/maven2/</a>. Use the standard
         * {@code repository_url} qualifier to point to another repository.
         */
        public static final String DEFAULT_REPOSITORY_URL = "https://repo.maven.apache.org/maven2/";

        private @Nullable String groupId;

        private @Nullable String artifactId;

        private @Nullable String version;

        private MavenBuilder() {}

        /**
         * Sets the group identifier.
         *
         * @param groupId the group identifier
         * @return this builder instance
         */
        public MavenBuilder withGroupId(String groupId) {
            this.groupId = groupId;
            return this;
        }

        /**
         * Removes the group identifier.
         *
         * @return this builder instance
         */
        public MavenBuilder withoutGroupId() {
            this.groupId = null;
            return this;
        }

        /**
         * Sets the artifact identifier.
         *
         * @param artifactId the artifact identifier
         * @return this builder instance
         */
        public MavenBuilder withArtifactId(String artifactId) {
            this.artifactId = artifactId;
            return this;
        }

        /**
         * Sets the version.
         *
         * @param version the version
         * @return this builder instance
         */
        public MavenBuilder withVersion(String version) {
            this.version = version;
            return this;
        }

        /**
         * Removes the version.
         *
         * @return this builder instance
         */
        public MavenBuilder withoutVersion() {
            this.version = null;
            return this;
        }

        /**
         * Sets the Maven classifier as defined in the POM documentation.
         *
         * @param classifier the classifier
         * @return this builder instance
         */
        public MavenBuilder withClassifier(String classifier) {
            this.qualifiers.put("classifier", classifier);
            return this;
        }

        /**
         * Removes the classifier.
         *
         * @return this builder instance
         */
        public MavenBuilder withoutClassifier() {
            this.qualifiers.remove("classifier");
            return this;
        }

        /**
         * Sets the Maven type as defined in the POM documentation. Note that
         * Maven uses a concept/coordinate called packaging which does not map
         * directly 1:1 to a file extension. In this use case, we need to
         * construct a link to one of many possible artifacts. Maven itself
         * uses type in a dependency declaration when needed to disambiguate between them.
         *
         * @param type the type
         * @return this builder instance
         */
        public MavenBuilder withType(String type) {
            this.qualifiers.put("type", type);
            return this;
        }

        /**
         * Removes the type.
         *
         * @return this builder instance
         */
        public MavenBuilder withoutType() {
            this.qualifiers.remove("type");
            return this;
        }

        /**
         * Gets the default Maven repository URL.
         *
         * @return the default Maven repository URL.
         */
        @Override
        public Optional<String> getDefaultRepositoryUrl() {
            return Optional.of(DEFAULT_REPOSITORY_URL);
        }

        /**
         * {@inheritDoc}
         */
        public PackageURL build() throws MalformedPackageURLException {
            Objects.requireNonNull(this.groupId);
            Objects.requireNonNull(this.artifactId);
            return PackageURLBuilder.aPackageURL()
                    .withType(PackageURL.StandardTypes.MAVEN)
                    .withNamespace(this.groupId)
                    .withName(this.artifactId)
                    .withVersion(this.version)
                    .withQualifiers(!this.qualifiers.isEmpty() ? this.qualifiers : null)
                    .withSubpath(this.subpath)
                    .build();
        }
    }

    /**
     * Builder for MLflow ML models (Azure ML, Databricks, etc.).
     */
    public static final class MlflowBuilder extends Builder<MlflowBuilder> {
        private @Nullable String name;

        private @Nullable String version;

        private MlflowBuilder() {}

        /**
         * Sets the model name.
         *
         * @param name the model name
         * @return this builder instance
         */
        public MlflowBuilder withName(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the version.
         *
         * @param version the version
         * @return this builder instance
         */
        public MlflowBuilder withVersion(String version) {
            this.version = version;
            return this;
        }

        /**
         * Removes the version.
         *
         * @return this builder instance
         */
        public MlflowBuilder withoutVersion() {
            this.version = null;
            return this;
        }

        /**
         * Sets the {@code model_uuid} as defined in the MLflow documentation.
         *
         * @param modelUuid the {@code model_uuid}
         * @return this builder instance
         */
        public MlflowBuilder withModelUuid(String modelUuid) {
            this.qualifiers.put("model_uuid", modelUuid);
            return this;
        }

        /**
         * Removes the {@code model_uuid}.
         * @return this builder instance
         */
        public MlflowBuilder withoutModelUuid() {
            this.qualifiers.remove("model_uuid");
            return this;
        }

        /**
         * Sets the {@code run_id} as defined in the MLflow documentation.
         *
         * @param runId the {@code run_id}
         * @return this builder instance
         */
        public MlflowBuilder withRunId(String runId) {
            this.qualifiers.put("run_id", runId);
            return this;
        }

        /**
         * Removes the {@code run_id}.
         *
         * @return this builder instance
         */
        public MlflowBuilder withoutRunId() {
            this.qualifiers.remove("run_id");
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public PackageURL build() throws MalformedPackageURLException {
            Objects.requireNonNull(this.name);
            return PackageURLBuilder.aPackageURL()
                    .withType(PackageURL.StandardTypes.MLFLOW)
                    .withName(this.name)
                    .withVersion(this.version)
                    .withQualifiers(!this.qualifiers.isEmpty() ? this.qualifiers : null)
                    .withSubpath(this.subpath)
                    .build();
        }
    }

    /**
     * Builder for NPM packages.
     */
    public static final class NpmBuilder extends Builder<NpmBuilder> {
        /**
         * The default repository is the npm Registry at
         * <a href="https://registry.npmjs.org">https://registry.npmjs.org</a>.
         */
        public static final String DEFAULT_REPOSITORY_URL = "https://registry.npmjs.org/";

        private @Nullable String scope;

        private @Nullable String name;

        private @Nullable String version;

        private NpmBuilder() {}

        /**
         * Sets the scope of a scoped NPM package.
         *
         * @param scope the scope of a scoped NPM package
         * @return this builder instance
         */
        public NpmBuilder withScope(String scope) {
            this.scope = scope;
            return this;
        }

        /**
         * Removes the scope.
         *
         * @return this builder instance
         */
        public NpmBuilder withoutScope() {
            this.scope = null;
            return this;
        }

        /**
         * Sets the name.
         *
         * @param name the name
         * @return this builder instance
         */
        public NpmBuilder withName(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the version.
         *
         * @param version the version
         * @return this builder instance
         */
        public NpmBuilder withVersion(String version) {
            this.version = version;
            return this;
        }

        /**
         * Removes the version.
         *
         * @return this builder instance
         */
        public NpmBuilder withoutVersion() {
            this.version = null;
            return this;
        }

        /**
         * Gets the default NPM repository URL.
         *
         * @return the default NPM repository URL
         */
        @Override
        public Optional<String> getDefaultRepositoryUrl() {
            return Optional.of(DEFAULT_REPOSITORY_URL);
        }

        /**
         * {@inheritDoc}
         */
        public PackageURL build() throws MalformedPackageURLException {
            Objects.requireNonNull(this.name);
            return PackageURLBuilder.aPackageURL()
                    .withType(PackageURL.StandardTypes.NPM)
                    .withNamespace(this.scope)
                    .withName(this.name)
                    .withVersion(this.version)
                    .withQualifiers(!this.qualifiers.isEmpty() ? this.qualifiers : null)
                    .withSubpath(this.subpath)
                    .build();
        }
    }

    /**
     * Builder for NuGet .NET packages.
     */
    public static final class NugetBuilder extends Builder<NugetBuilder> {
        public static final String DEFAULT_REPOSITORY_URL = "https://www.nuget.org";

        private @Nullable String name;

        private @Nullable String version;

        private NugetBuilder() {}

        /**
         * Sets the name.
         *
         * @param name the name
         * @return this builder instance
         */
        public NugetBuilder withName(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the version.
         *
         * @param version the version
         * @return this builder instance
         */
        public NugetBuilder withVersion(String version) {
            this.version = version;
            return this;
        }

        /**
         * Removes the version.
         *
         * @return this builder instance
         */
        public NugetBuilder withoutVersion() {
            this.version = null;
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public PackageURL build() throws MalformedPackageURLException {
            Objects.requireNonNull(this.name);
            return PackageURLBuilder.aPackageURL()
                    .withType(PackageURL.StandardTypes.NUGET)
                    .withName(this.name)
                    .withVersion(this.version)
                    .withQualifiers(!this.qualifiers.isEmpty() ? this.qualifiers : null)
                    .withSubpath(this.subpath)
                    .build();
        }
    }

    /**
     * Builder for artifacts stored in registries that conform to the OCI Distribution
     * Specification
     * <a href="https://github.com/opencontainers/distribution-spec">.https://github.com/opencontainers/distribution-spec</a>
     * including container images built by Docker and others.
     */
    public static final class OciBuilder extends Builder<OciBuilder> {
        private @Nullable String name;

        private @Nullable String version;

        private OciBuilder() {}

        /**
         * Sets the name.
         * <p>
         * The name is the last fragment of the repository name. For example if
         * the repository name is {@code library/debian} then the name is {@code debian}.
         *
         * @param name the name
         * @return this builder instance
         */
        public OciBuilder withName(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the version.
         *
         * @param version the version
         * @return this builder instance
         */
        public OciBuilder withVersion(String version) {
            this.version = version;
            return this;
        }

        /**
         * Removes the version.
         *
         * @return this builder instance
         */
        public OciBuilder withoutVersion() {
            this.version = null;
            return this;
        }

        /**
         * Sets the package architecture.
         *
         * @param arch the package architecture
         * @return this builder instance
         */
        public OciBuilder withArch(String arch) {
            this.qualifiers.put("arch", arch);
            return this;
        }

        /**
         * Removes the package architecture.
         *
         * @return this builder instance
         */
        public OciBuilder withoutArch() {
            this.qualifiers.remove("arch");
            return this;
        }

        /**
         * Sets the artifact tag that may have been associated with the digest at the time.
         *
         * @param tag the artifact tag
         * @return this builder instance
         */
        public OciBuilder withTag(String tag) {
            this.qualifiers.put("tag", tag);
            return this;
        }

        /**
         * Removes the package tag.
         *
         * @return this builder instance
         */
        public OciBuilder withoutTag() {
            this.qualifiers.remove("tag");
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public PackageURL build() throws MalformedPackageURLException {
            Objects.requireNonNull(this.name);
            return PackageURLBuilder.aPackageURL()
                    .withType(PackageURL.StandardTypes.OCI)
                    .withName(this.name)
                    .withVersion(this.version)
                    .withQualifiers(!this.qualifiers.isEmpty() ? this.qualifiers : null)
                    .withSubpath(this.subpath)
                    .build();
        }
    }

    /**
     * Builder for Dart and Flutter pub packages.
     */
    public static final class PubBuilder extends Builder<PubBuilder> {
        public static final String DEFAULT_REPOSITORY_URL = "https://pub.dartlang.org";

        private @Nullable String name;

        private @Nullable String version;

        private PubBuilder() {}

        /**
         * Sets the name.
         *
         * @param name the name
         * @return this builder instance
         */
        public PubBuilder withName(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the version.
         *
         * @param version the version
         * @return this builder instance
         */
        public PubBuilder withVersion(String version) {
            this.version = version;
            return this;
        }

        /**
         * Removes the version.
         *
         * @return this builder instance
         */
        public PubBuilder withoutVersion() {
            this.version = null;
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public PackageURL build() throws MalformedPackageURLException {
            Objects.requireNonNull(this.name);
            return PackageURLBuilder.aPackageURL()
                    .withType(PackageURL.StandardTypes.PUB)
                    .withName(this.name)
                    .withVersion(this.version)
                    .withQualifiers(!this.qualifiers.isEmpty() ? this.qualifiers : null)
                    .withSubpath(this.subpath)
                    .build();
        }
    }

    /**
     * Builder for Python packages.
     */
    public static final class PypiBuilder extends Builder<PypiBuilder> {
        public static final String DEFAULT_REPOSITORY_URL = "https://pypi.org";

        private @Nullable String name;

        private @Nullable String version;

        private PypiBuilder() {}

        /**
         * Sets the name.
         *
         * @param name the name
         * @return this builder instance
         */
        public PypiBuilder withName(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the version.
         *
         * @param version the version
         * @return this builder instance
         */
        public PypiBuilder withVersion(String version) {
            this.version = version;
            return this;
        }

        /**
         * Removes the version.
         *
         * @return this builder instance
         */
        public PypiBuilder withoutVersion() {
            this.version = null;
            return this;
        }

        /**
         * Selects a particular distribution file (case-sensitive).
         * <p>
         * For naming convention, see the Python Packaging User Guide on
         * <a href="https://packaging.python.org/en/latest/specifications/source-distribution-format/#source-distribution-file-name">source distributions</a>
         * and on
         * <a href="https://packaging.python.org/en/latest/specifications/binary-distribution-format/#file-name-convention">binary distributions</a>
         * and the rules for
         * <a href="https://packaging.python.org/en/latest/specifications/platform-compatibility-tags/">platform compatibility tags</a>.
         *
         * @param fileName the distribution file (case-sensitive)
         * @return this builder instance
         */
        @Override
        public PypiBuilder withFileName(String fileName) {
            this.qualifiers.put("file_name", fileName);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public PypiBuilder withoutFileName() {
            this.qualifiers.remove("file_name");
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public PackageURL build() throws MalformedPackageURLException {
            Objects.requireNonNull(this.name);
            return PackageURLBuilder.aPackageURL()
                    .withType(PackageURL.StandardTypes.PYPI)
                    .withName(this.name)
                    .withVersion(this.version)
                    .withQualifiers(!this.qualifiers.isEmpty() ? this.qualifiers : null)
                    .withSubpath(this.subpath)
                    .build();
        }
    }

    /**
     * Builder for QNX packages.
     */
    public static final class QpkgBuilder extends Builder<QpkgBuilder> {
        private @Nullable String vendor;

        private @Nullable String name;

        private @Nullable String version;

        private QpkgBuilder() {}

        /**
         * Sets the vendor of the package.
         *
         * @param vendor the vendor of the package
         * @return this builder instance
         */
        public QpkgBuilder withVendor(String vendor) {
            this.vendor = vendor;
            return this;
        }

        /**
         * Sets the name.
         *
         * @param name the name
         * @return this builder instance
         */
        public QpkgBuilder withName(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the version.
         *
         * @param version the version
         * @return this builder instance
         */
        public QpkgBuilder withVersion(String version) {
            this.version = version;
            return this;
        }

        /**
         * Removes the version.
         *
         * @return this builder instance
         */
        public QpkgBuilder withoutVersion() {
            this.version = null;
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public PackageURL build() throws MalformedPackageURLException {
            Objects.requireNonNull(this.name);
            return PackageURLBuilder.aPackageURL()
                    .withType(PackageURL.StandardTypes.QPKG)
                    .withNamespace(this.vendor)
                    .withName(this.name)
                    .withVersion(this.version)
                    .withQualifiers(!this.qualifiers.isEmpty() ? this.qualifiers : null)
                    .withSubpath(this.subpath)
                    .build();
        }
    }

    /**
     * Builder for RPM packages.
     */
    public static final class RpmBuilder extends Builder<RpmBuilder> {
        private @Nullable String vendor;

        private @Nullable String name;

        private @Nullable String versionRelease;

        private RpmBuilder() {}

        /**
         * Sets the vendor such as Fedora or OpenSUSE.
         *
         * @param vendor the vendor
         * @return this builder instance
         */
        public RpmBuilder withVendor(String vendor) {
            this.vendor = vendor;
            return this;
        }

        /**
         * Sets the name.
         *
         * @param name the name
         * @return this builder instance
         */
        public RpmBuilder withName(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the version-release.
         *
         * @param versionRelease the version-release
         * @return this builder instance
         */
        public RpmBuilder withVersionRelease(String versionRelease) {
            this.versionRelease = versionRelease;
            return this;
        }

        /**
         * Removes the version-release.
         *
         * @return this builder instance
         */
        public RpmBuilder withoutVersionRelease() {
            this.versionRelease = null;
            return this;
        }

        /**
         * Sets the package epoch.
         *
         * @param epoch the package epoch
         * @return this builder instance
         */
        public RpmBuilder withEpoch(String epoch) {
            this.qualifiers.put("epoch", epoch);
            return this;
        }

        /**
         * Removes the package epoch.
         *
         * @return this builder instance
         */
        public RpmBuilder withoutEpoch() {
            this.qualifiers.remove("epoch");
            return this;
        }

        /**
         * Sets the package architecture.
         *
         * @param arch the package architecture
         * @return this builder instance
         */
        public RpmBuilder withArch(String arch) {
            this.qualifiers.put("arch", arch);
            return this;
        }

        /**
         * Removes the package architecture.
         *
         * @return this builder instance
         */
        public RpmBuilder withoutArch() {
            this.qualifiers.remove("arch");
            return this;
        }

        /**
         * Sets the distribution associated with the package.
         *
         * @param distro the distribution associated with the package
         * @return this builder instance
         */
        public RpmBuilder withDistro(String distro) {
            this.qualifiers.put("distro", distro);
            return this;
        }

        /**
         * Removes the distribution associated with the package.
         *
         * @return this builder instance
         */
        public RpmBuilder withoutDistro() {
            this.qualifiers.remove("distro");
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public PackageURL build() throws MalformedPackageURLException {
            Objects.requireNonNull(this.vendor);
            Objects.requireNonNull(this.name);
            return PackageURLBuilder.aPackageURL()
                    .withType(PackageURL.StandardTypes.RPM)
                    .withNamespace(this.vendor)
                    .withName(this.name)
                    .withVersion(this.versionRelease)
                    .withQualifiers(!this.qualifiers.isEmpty() ? this.qualifiers : null)
                    .withSubpath(this.subpath)
                    .build();
        }
    }

    /**
     * Builder for ISO-IEC 19770-2 Software Identification (SWID) tags.
     */
    public static final class SwidBuilder extends Builder<SwidBuilder> {
        private @Nullable String softwareCreator;

        private @Nullable String softwareIdentityName;

        private @Nullable String softwareIdentityVersion;

        private SwidBuilder() {}

        /**
         * Sets the optional name and regid of the entity with a role of {@code softwareCreator}.
         *
         * @param softwareCreator name and regid
         * @return this builder instance
         */
        public SwidBuilder withSoftwareCreator(String softwareCreator) {
            this.softwareCreator = softwareCreator;
            return this;
        }

        /**
         * Removes the name and regid of the entity with a role of {@code softwareCreator}.
         *
         * @return this builder instance
         */
        public SwidBuilder withoutSoftwareCreator() {
            this.softwareCreator = null;
            return this;
        }

        /**
         * Sets the name as defined in the SWID {@code SoftwareIdentity} element.
         *
         * @param name the name
         * @return this builder instance
         */
        public SwidBuilder withSoftwareIdentityName(String name) {
            this.softwareIdentityName = name;
            return this;
        }

        /**
         * Sets the version as defined in the SWID {@code SoftwareIdentity} element.
         *
         * @param softwareIdentityVersion the version
         * @return this builder instance
         */
        public SwidBuilder withSoftwareIdentityVersion(String softwareIdentityVersion) {
            this.softwareIdentityVersion = softwareIdentityVersion;
            return this;
        }

        /**
         * Removes the version.
         *
         * @return this builder instance
         */
        public SwidBuilder withoutSoftwareIdentityVersion() {
            this.softwareIdentityVersion = null;
            return this;
        }

        /**
         * Sets the {@code tagId} as defined in the SWID
         * {@code SoftwareIdentity} element. Per the SWID specification, GUIDs
         * are recommended.
         *
         * @param tagId the {@code tagId}
         * @return this builder instance
         */
        public SwidBuilder withTagId(String tagId) {
            this.qualifiers.put("tag_id", tagId);
            return this;
        }

        /**
         * Removes the {@code tagId}.
         *
         * @return this builder instance
         */
        public SwidBuilder withoutTagId() {
            this.qualifiers.remove("tag_id");
            return this;
        }

        /**
         * Sets the {@code tagVersion} as defined in the SWID
         * {@code SoftwareIdentity} element. Per the SWID specification, GUIDs
         * are recommended.
         *
         * @param tagVersion the {@code tagVersion}
         * @return this builder instance
         */
        public SwidBuilder withTagVersion(String tagVersion) {
            this.qualifiers.put("tag_version", tagVersion);
            return this;
        }

        /**
         * Removes the {@code tagVersion}.
         *
         * @return this builder instance
         */
        public SwidBuilder withoutTagVersion() {
            this.qualifiers.remove("tag_version");
            return this;
        }

        /**
         * Sets the {@code patch} as defined in the SWID {@code
         * SoftwareIdentity} element.
         *
         * @param patch {@code the patch}
         * @return this builder instance
         */
        public SwidBuilder withPatch(String patch) {
            this.qualifiers.put("patch", patch);
            return this;
        }

        /**
         * Removes the {@code patch}.
         *
         * @return this builder instance
         */
        public SwidBuilder withoutPatch() {
            this.qualifiers.remove("patch");
            return this;
        }

        /**
         * Sets the {@code tagCreatorName} as defined in the SWID
         * {@code SoftwareIdentity} element.
         *
         * @param tagCreatorName the {@code tagCreatorName}
         * @return this builder instance
         */
        public SwidBuilder withTagCreatorName(String tagCreatorName) {
            this.qualifiers.put("tag_creator_name", tagCreatorName);
            return this;
        }

        /**
         * Removes the {@code tagCreatorName}.
         *
         * @return this builder instance
         */
        public SwidBuilder withoutTagCreatorName() {
            this.qualifiers.remove("tag_creator_name");
            return this;
        }

        /**
         * Sets the {@code tagCreatorRegid} as defined in the SWID
         * {@code SoftwareIdentity} element.
         *
         * @param tagCreatorRegid the {@code tagCreatorRegid}
         * @return this builder instance
         */
        public SwidBuilder withTagCreatorRegid(String tagCreatorRegid) {
            this.qualifiers.put("tag_creator_regid", tagCreatorRegid);
            return this;
        }

        /**
         * Removes the {@code tagCreatorRegid}.
         *
         * @return this builder instance
         */
        public SwidBuilder withoutTagCreatorRegid() {
            this.qualifiers.remove("tag_creator_regid");
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public PackageURL build() throws MalformedPackageURLException {
            Objects.requireNonNull(this.softwareIdentityName);
            return PackageURLBuilder.aPackageURL()
                    .withType(PackageURL.StandardTypes.SWID)
                    .withNamespace(this.softwareCreator)
                    .withName(this.softwareIdentityName)
                    .withVersion(this.softwareIdentityVersion)
                    .withQualifiers(!this.qualifiers.isEmpty() ? this.qualifiers : null)
                    .withSubpath(this.subpath)
                    .build();
        }
    }

    /**
     * Builder for Swift packages.
     */
    public static final class SwiftBuilder extends Builder<SwiftBuilder> {
        private @Nullable String namespace;

        private @Nullable String repositoryName;

        private @Nullable String packageVersion;

        private SwiftBuilder() {}

        /**
         * Sets the source host and user/organization.
         *
         * @param namespace the source host and user/organization
         * @return this builder instance
         */
        public SwiftBuilder withNamespace(String namespace) {
            this.namespace = namespace;
            return this;
        }

        /**
         * Sets the repository repositoryName.
         *
         * @param repositoryName the repository repositoryName
         * @return this builder instance
         */
        public SwiftBuilder withRepositoryName(String repositoryName) {
            this.repositoryName = repositoryName;
            return this;
        }

        /**
         * Sets the package version.
         *
         * @param packageVersion the package version
         * @return this builder instance
         */
        public SwiftBuilder withPackageVersion(String packageVersion) {
            this.packageVersion = packageVersion;
            return this;
        }

        /**
         * Removes the package version.
         *
         * @return this builder instance
         */
        public SwiftBuilder withoutPackageVersion() {
            this.packageVersion = null;
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public PackageURL build() throws MalformedPackageURLException {
            Objects.requireNonNull(this.namespace);
            Objects.requireNonNull(this.repositoryName);
            return PackageURLBuilder.aPackageURL()
                    .withType(PackageURL.StandardTypes.SWIFT)
                    .withNamespace(this.namespace)
                    .withName(this.repositoryName)
                    .withVersion(this.packageVersion)
                    .withQualifiers(!this.qualifiers.isEmpty() ? this.qualifiers : null)
                    .withSubpath(this.subpath)
                    .build();
        }
    }
}
