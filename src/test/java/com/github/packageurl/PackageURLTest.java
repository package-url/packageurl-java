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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test cases for PackageURL parsing
 * <p>
 * Original test cases retrieved from:
 * <a href="https://raw.githubusercontent.com/package-url/purl-spec/master/test-suite-data.json">https://raw.githubusercontent.com/package-url/purl-spec/master/test-suite-data.json</a>
 *
 * @author Steve Springett
 */
class PackageURLTest {
    private static final Locale DEFAULT_LOCALE = Locale.getDefault();

    @BeforeAll
    static void setup() {
        Locale.setDefault(new Locale("tr"));
    }

    @AfterAll
    static void resetLocale() {
        Locale.setDefault(DEFAULT_LOCALE);
    }

    @Test
    void validPercentEncoding() throws MalformedPackageURLException {
        PackageURL purl =
                new PackageURL("maven", "com.google.summit", "summit-ast", "2.2.0\n", (Map<String, String>) null, null);
        assertEquals("pkg:maven/com.google.summit/summit-ast@2.2.0%0A", purl.toString());
        PackageURL purl2 =
                new PackageURL("pkg:nuget/%D0%9Cicros%D0%BEft.%D0%95ntit%D1%83Fram%D0%B5work%D0%A1%D0%BEr%D0%B5");
        assertEquals("Мicrosоft.ЕntitуFramеworkСоrе", purl2.getName());
        assertEquals(
                "pkg:nuget/%D0%9Cicros%D0%BEft.%D0%95ntit%D1%83Fram%D0%B5work%D0%A1%D0%BEr%D0%B5", purl2.toString());
    }

    @Test
    void invalidPercentEncoding() throws MalformedPackageURLException {
        assertThrowsExactly(
                MalformedPackageURLException.class,
                () -> new PackageURL("pkg:maven/com.google.summit/summit-ast@2.2.0%"));
        assertThrowsExactly(
                MalformedPackageURLException.class,
                () -> new PackageURL("pkg:maven/com.google.summit/summit-ast@2.2.0%0"));
    }

    static Stream<Arguments> constructorParsing() throws IOException {
        return PurlParameters.getTestDataFromFiles(
                "test-suite-data.json", "custom-suite.json", "string-constructor-only.json");
    }

    @DisplayName("Test constructor parsing")
    @ParameterizedTest(name = "{0}: ''{1}''")
    @MethodSource
    void constructorParsing(
            String description,
            @Nullable String purlString,
            PurlParameters parameters,
            @Nullable String canonicalPurl,
            boolean invalid)
            throws Exception {
        if (invalid) {
            assertThrows(
                    getExpectedException(purlString),
                    () -> new PackageURL(purlString).normalize(),
                    "Parsing '" + purlString + "' should have failed because " + description);
        } else {
            PackageURL purl = new PackageURL(purlString);
            assertPurlEquals(parameters, purl);
            assertEquals(canonicalPurl, purl.canonicalize(), "canonical PURL");
        }
    }

    static Stream<Arguments> constructorParameters() throws IOException {
        return PurlParameters.getTestDataFromFiles(
                "test-suite-data.json", "custom-suite.json", "components-constructor-only.json");
    }

    @DisplayName("Test constructor parameters")
    @ParameterizedTest(name = "{0}: {2}")
    @MethodSource
    void constructorParameters(
            String description,
            @Nullable String purlString,
            PurlParameters parameters,
            @Nullable String canonicalPurl,
            boolean invalid)
            throws Exception {
        if (invalid) {
            assertThrows(
                    getExpectedException(parameters),
                    () -> new PackageURL(
                            parameters.getType(),
                            parameters.getNamespace(),
                            parameters.getName(),
                            parameters.getVersion(),
                            parameters.getQualifiers(),
                            parameters.getSubpath()));
        } else {
            PackageURL purl = new PackageURL(
                    parameters.getType(),
                    parameters.getNamespace(),
                    parameters.getName(),
                    parameters.getVersion(),
                    parameters.getQualifiers(),
                    parameters.getSubpath());
            assertPurlEquals(parameters, purl);
            assertEquals(canonicalPurl, purl.canonicalize(), "canonical PURL");
        }
    }

    static Stream<Arguments> constructorTypeNameSpace() throws IOException {
        return PurlParameters.getTestDataFromFiles("type-namespace-constructor-only.json");
    }

    @ParameterizedTest
    @MethodSource
    void constructorTypeNameSpace(
            String description,
            @Nullable String purlString,
            PurlParameters parameters,
            @Nullable String canonicalPurl,
            boolean invalid)
            throws Exception {
        if (invalid) {
            assertThrows(
                    getExpectedException(parameters), () -> new PackageURL(parameters.getType(), parameters.getName()));
        } else {
            PackageURL purl = new PackageURL(parameters.getType(), parameters.getName());
            assertPurlEquals(parameters, purl);
            assertEquals(canonicalPurl, purl.canonicalize(), "canonical PURL");
        }
    }

    private static void assertPurlEquals(PurlParameters expected, PackageURL actual) {
        assertEquals("pkg", actual.getScheme(), "scheme");
        assertEquals(expected.getType(), actual.getType(), "type");
        assertEquals(emptyToNull(expected.getNamespace()), actual.getNamespace(), "namespace");
        assertEquals(expected.getName(), actual.getName(), "name");
        assertEquals(emptyToNull(expected.getVersion()), actual.getVersion(), "version");
        assertEquals(emptyToNull(expected.getSubpath()), actual.getSubpath(), "subpath");
        assertNotNull(actual.getQualifiers(), "qualifiers");
        assertEquals(actual.getQualifiers(), expected.getQualifiers(), "qualifiers");
    }

    private static Class<? extends Exception> getExpectedException(PurlParameters json) {
        return json.getType() == null || json.getName() == null
                ? NullPointerException.class
                : MalformedPackageURLException.class;
    }

    private static Class<? extends Exception> getExpectedException(@Nullable String purl) {
        return purl == null ? NullPointerException.class : MalformedPackageURLException.class;
    }

    private static @Nullable String emptyToNull(@Nullable String value) {
        return value == null || value.isEmpty() ? null : value;
    }

    @Test
    void standardTypes() {
        assertEquals("alpm", PackageURL.StandardTypes.ALPM);
        assertEquals("apk", PackageURL.StandardTypes.APK);
        assertEquals("bitbucket", PackageURL.StandardTypes.BITBUCKET);
        assertEquals("bitnami", PackageURL.StandardTypes.BITNAMI);
        assertEquals("cocoapods", PackageURL.StandardTypes.COCOAPODS);
        assertEquals("cargo", PackageURL.StandardTypes.CARGO);
        assertEquals("composer", PackageURL.StandardTypes.COMPOSER);
        assertEquals("conan", PackageURL.StandardTypes.CONAN);
        assertEquals("conda", PackageURL.StandardTypes.CONDA);
        assertEquals("cpan", PackageURL.StandardTypes.CPAN);
        assertEquals("cran", PackageURL.StandardTypes.CRAN);
        assertEquals("deb", PackageURL.StandardTypes.DEB);
        assertEquals("docker", PackageURL.StandardTypes.DOCKER);
        assertEquals("gem", PackageURL.StandardTypes.GEM);
        assertEquals("generic", PackageURL.StandardTypes.GENERIC);
        assertEquals("github", PackageURL.StandardTypes.GITHUB);
        assertEquals("golang", PackageURL.StandardTypes.GOLANG);
        assertEquals("hackage", PackageURL.StandardTypes.HACKAGE);
        assertEquals("hex", PackageURL.StandardTypes.HEX);
        assertEquals("huggingface", PackageURL.StandardTypes.HUGGINGFACE);
        assertEquals("luarocks", PackageURL.StandardTypes.LUAROCKS);
        assertEquals("maven", PackageURL.StandardTypes.MAVEN);
        assertEquals("mlflow", PackageURL.StandardTypes.MLFLOW);
        assertEquals("nix", PackageURL.StandardTypes.NIX);
        assertEquals("npm", PackageURL.StandardTypes.NPM);
        assertEquals("nuget", PackageURL.StandardTypes.NUGET);
        assertEquals("qpkg", PackageURL.StandardTypes.QPKG);
        assertEquals("oci", PackageURL.StandardTypes.OCI);
        assertEquals("pub", PackageURL.StandardTypes.PUB);
        assertEquals("pypi", PackageURL.StandardTypes.PYPI);
        assertEquals("rpm", PackageURL.StandardTypes.RPM);
        assertEquals("swid", PackageURL.StandardTypes.SWID);
        assertEquals("swift", PackageURL.StandardTypes.SWIFT);
    }

    @Test
    void coordinatesEquals() throws Exception {
        PackageURL p1 = new PackageURL("pkg:generic/acme/example-component@1.0.0?key1=value1&key2=value2");
        PackageURL p2 = new PackageURL("pkg:generic/acme/example-component@1.0.0");
        assertTrue(p1.isCoordinatesEquals(p2));
    }

    @Test
    void canonicalEquals() throws Exception {
        PackageURL p1 = new PackageURL("pkg:generic/acme/example-component@1.0.0?key1=value1&key2=value2");
        PackageURL p2 = new PackageURL("pkg:generic/acme/example-component@1.0.0?key2=value2&key1=value1");
        assertTrue(p1.isCanonicalEquals(p2));
    }

    @Test
    void getCoordinates() throws Exception {
        PackageURL purl = new PackageURL("pkg:generic/acme/example-component@1.0.0?key1=value1&key2=value2");
        assertEquals("pkg:generic/acme/example-component@1.0.0", purl.getCoordinates());
    }

    @Test
    void getCoordinatesNoCacheIssue89() throws Exception {
        PackageURL purl = new PackageURL("pkg:generic/acme/example-component@1.0.0?key1=value1&key2=value2");
        purl.canonicalize();
        assertEquals("pkg:generic/acme/example-component@1.0.0", purl.getCoordinates());
    }

    @Test
    void npmCaseSensitive() throws Exception {
        // e.g. https://www.npmjs.com/package/base64/v/1.0.0
        PackageURL base64Lowercase = new PackageURL("pkg:npm/base64@1.0.0");
        assertEquals("npm", base64Lowercase.getType());
        assertEquals("base64", base64Lowercase.getName());
        assertEquals("1.0.0", base64Lowercase.getVersion());

        // e.g. https://www.npmjs.com/package/Base64/v/1.0.0
        PackageURL base64Uppercase = new PackageURL("pkg:npm/Base64@1.0.0");
        assertEquals("npm", base64Uppercase.getType());
        assertEquals("Base64", base64Uppercase.getName());
        assertEquals("1.0.0", base64Uppercase.getVersion());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "pkg:alpm/arch/pacman@6.0.1-1?arch=x86_64",
                "pkg:alpm/arch/python-pip@21.0-1?arch=any",
                "pkg:alpm/arch/containers-common@1:0.47.4-4?arch=x86_64",
                "pkg:apk/alpine/curl@7.83.0-r0?arch=x86",
                "pkg:apk/alpine/apk@2.12.9-r3?arch=x86",
                "pkg:bitbucket/birkenfeld/pygments-main@244fd47e07d1014f0aed9c",
                "pkg:bitnami/wordpress?distro=debian-12",
                "pkg:bitnami/wordpress@6.2.0?distro=debian-12",
                "pkg:bitnami/wordpress@6.2.0?arch=arm64&distro=debian-12",
                "pkg:bitnami/wordpress@6.2.0?arch=arm64&distro=photon-4",
                "pkg:cocoapods/AFNetworking@4.0.1",
                "pkg:cocoapods/MapsIndoors@3.24.0",
                "pkg:cocoapods/ShareKit@2.0#Twitter",
                "pkg:cocoapods/GoogleUtilities@7.5.2#NSData+zlib",
                "pkg:cocoapods/GoogleUtilities@7.5.2#NSData+zlib",
                "pkg:cargo/rand@0.7.2",
                "pkg:cargo/clap@2.33.0",
                "pkg:cargo/structopt@0.3.11",
                "pkg:composer/laravel/laravel@5.5.0",
                "pkg:conan/openssl@3.0.3",
                "pkg:conan/openssl.org/openssl@3.0.3?user=bincrafters&channel=stable",
                "pkg:conan/openssl.org/openssl@3.0.3?arch=x86_64&build_type=Debug&compiler=Visual%20Studio&compiler.runtime=MDd&compiler.version=16&os=Windows&shared=True&rrev=93a82349c31917d2d674d22065c7a9ef9f380c8e&prev=b429db8a0e324114c25ec387bfd8281f330d7c5c",
                "pkg:conda/absl-py@0.4.1?build=py36h06a4308_0&channel=main&subdir=linux-64&type=tar.bz2",
                "pkg:cpan/Perl::Version@1.013",
                "pkg:cpan/DROLSKY/DateTime@1.55",
                "pkg:cpan/DateTime@1.55",
                "pkg:cpan/GDT/URI-PackageURL",
                "pkg:cpan/LWP::UserAgent",
                "pkg:cpan/OALDERS/libwww-perl@6.76",
                "pkg:cpan/URI",
                "pkg:cran/A3@1.0.0",
                "pkg:cran/rJava@1.0-4",
                "pkg:cran/caret@6.0-88",
                "pkg:deb/debian/curl@7.50.3-1?arch=i386&distro=jessie",
                "pkg:deb/debian/dpkg@1.19.0.4?arch=amd64&distro=stretch",
                "pkg:deb/ubuntu/dpkg@1.19.0.4?arch=amd64",
                "pkg:deb/debian/attr@1:2.4.47-2?arch=source",
                "pkg:deb/debian/attr@1:2.4.47-2%2Bb1?arch=amd64",
                "pkg:docker/cassandra@latest",
                "pkg:docker/smartentry/debian@dc437cc87d10",
                "pkg:docker/customer/dockerimage@sha256%3A244fd47e07d10?repository_url=gcr.io",
                "pkg:gem/ruby-advisory-db-check@0.12.4",
                "pkg:gem/jruby-launcher@1.1.2?platform=java",
                "pkg:generic/openssl@1.1.10g",
                "pkg:generic/openssl@1.1.10g?download_url=https://openssl.org/source/openssl-1.1.0g.tar.gz&checksum=sha256:de4d501267da",
                "pkg:generic/bitwarderl?vcs_url=git%2Bhttps://git.fsfe.org/dxtr/bitwarderl%40cc55108da32",
                "pkg:github/package-url/purl-spec@244fd47e07d1004",
                "pkg:github/package-url/purl-spec@244fd47e07d1004#everybody/loves/dogs",
                "pkg:golang/github.com/gorilla/context@234fd47e07d1004f0aed9c",
                "pkg:golang/google.golang.org/genproto#googleapis/api/annotations",
                "pkg:golang/github.com/gorilla/context@234fd47e07d1004f0aed9c#api",
                "pkg:hackage/a50@0.5",
                "pkg:hackage/AC-HalfInteger@1.2.1",
                "pkg:hackage/3d-graphics-examples@0.0.0.2",
                "pkg:hex/jason@1.1.2",
                "pkg:hex/acme/foo@2.3.",
                "pkg:hex/phoenix_html@2.13.3#priv/static/phoenix_html.js",
                "pkg:hex/bar@1.2.3?repository_url=https://myrepo.example.com",
                "pkg:huggingface/distilbert-base-uncased@043235d6088ecd3dd5fb5ca3592b6913fd516027",
                "pkg:huggingface/microsoft/deberta-v3-base@559062ad13d311b87b2c455e67dcd5f1c8f65111?repository_url=https://hub-ci.huggingface.co",
                "pkg:luarocks/luasocket@3.1.0-1",
                "pkg:luarocks/hisham/luafilesystem@1.8.0-1",
                "pkg:luarocks/username/packagename@0.1.0-1?repository_url=https://example.com/private_rocks_server/",
                "pkg:maven/org.apache.xmlgraphics/batik-anim@1.9.1",
                "pkg:maven/org.apache.xmlgraphics/batik-anim@1.9.1?type=pom",
                "pkg:maven/org.apache.xmlgraphics/batik-anim@1.9.1?classifier=sources",
                "pkg:maven/org.apache.xmlgraphics/batik-anim@1.9.1?type=zip&classifier=dist",
                "pkg:maven/net.sf.jacob-projec/jacob@1.14.3?classifier=x86&type=dll",
                "pkg:maven/net.sf.jacob-projec/jacob@1.14.3?classifier=x64&type=dll",
                "pkg:maven/groovy/groovy@1.0?repository_url=https://maven.google.com",
                "pkg:mlflow/creditfraud@3?repository_url=https://westus2.api.azureml.ms/mlflow/v1.0/subscriptions/a50f2011-fab8-4164-af23-c62881ef8c95/resourceGroups/TestResourceGroup/providers/Microsoft.MachineLearningServices/workspaces/TestWorkspace",
                "pkg:mlflow/trafficsigns@10?model_uuid=36233173b22f4c89b451f1228d700d49&run_id=410a3121-2709-4f88-98dd-dba0ef056b0a&repository_url=https://adb-5245952564735461.0.azuredatabricks.net/api/2.0/mlflow",
                "pkg:npm/foobar@12.3.1",
                "pkg:npm/%40angular/animation@12.3.1",
                "pkg:npm/mypackage@12.4.5?vcs_url=git://host.com/path/to/repo.git%404345abcd34343",
                "pkg:nuget/EnterpriseLibrary.Common@6.0.1304",
                "pkg:qpkg/blackberry/com.qnx.sdp@7.0.0.SGA201702151847",
                "pkg:qpkg/blackberry/com.qnx.qnx710.foo.bar.qux@0.0.4.01449T202205040833L",
                "pkg:oci/debian@sha256%3A244fd47e07d10?repository_url=docker.io/library/debian&arch=amd64&tag=latest",
                "pkg:oci/debian@sha256%3A244fd47e07d10?repository_url=ghcr.io/debian&tag=bullseye",
                "pkg:oci/static@sha256%3A244fd47e07d10?repository_url=gcr.io/distroless/static&tag=latest",
                "pkg:oci/hello-wasm@sha256%3A244fd47e07d10?tag=v1",
                "pkg:pub/characters@1.2.0",
                "pkg:pub/flutter@0.0.0",
                "pkg:pypi/django@1.11.1",
                "pkg:pypi/django@1.11.1?filename=Django-1.11.1.tar.gz",
                "pkg:pypi/django@1.11.1?filename=Django-1.11.1-py2.py3-none-any.whl",
                "pkg:pypi/django-allauth@12.23",
                "pkg:rpm/fedora/curl@7.50.3-1.fc25?arch=i386&distro=fedora-25",
                "pkg:rpm/centerim@4.22.10-1.el6?arch=i686&epoch=1&distro=fedora-25",
                "pkg:swid/Acme/example.com/Enterprise+Server@1.0.0?tag_id=75b8c285-fa7b-485b-b199-4745e3004d0d",
                "pkg:swid/Fedora@29?tag_id=org.fedoraproject.Fedora-29",
                "pkg:swid/Adobe+Systems+Incorporated/Adobe+InDesign@CC?tag_id=CreativeCloud-CS6-Win-GM-MUL",
                "pkg:swift/github.com/Alamofire/Alamofire@5.4.3",
                "pkg:swift/github.com/RxSwiftCommunity/RxFlow@2.12.4"
            })
    void parseValidTypes(final String purl) {
        assertDoesNotThrow(() -> new PackageURL(purl).normalize());
    }
}
