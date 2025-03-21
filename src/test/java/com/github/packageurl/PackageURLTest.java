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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

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

    @SuppressWarnings("deprecation")
    @Test
    void invalidPercentEncoding() throws MalformedPackageURLException {
        assertThrowsExactly(
                MalformedPackageURLException.class,
                () -> new PackageURL("pkg:maven/com.google.summit/summit-ast@2.2.0%"));
        assertThrowsExactly(
                MalformedPackageURLException.class,
                () -> new PackageURL("pkg:maven/com.google.summit/summit-ast@2.2.0%0"));
        PackageURL purl = new PackageURL("pkg:maven/com.google.summit/summit-ast@2.2.0");
        Throwable t1 = assertThrowsExactly(ValidationException.class, () -> purl.uriDecode("%"));
        assertEquals("Incomplete percent encoding at offset 0 with value '%'", t1.getMessage());
        Throwable t2 = assertThrowsExactly(ValidationException.class, () -> PackageURL.percentDecode("a%0"));
        assertEquals("Incomplete percent encoding at offset 1 with value '%0'", t2.getMessage());
        Throwable t3 = assertThrowsExactly(ValidationException.class, () -> PackageURL.percentDecode("aaaa%%0A"));
        assertEquals("Invalid percent encoding char 1 at offset 5 with value '%'", t3.getMessage());
        Throwable t4 = assertThrowsExactly(ValidationException.class, () -> PackageURL.percentDecode("%0G"));
        assertEquals("Invalid percent encoding char 2 at offset 2 with value 'G'", t4.getMessage());
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
            assertThrowsExactly(getExpectedException(purlString), () -> new PackageURL(purlString));
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
            try {
                PackageURL purl = new PackageURL(
                        parameters.getType(),
                        parameters.getNamespace(),
                        parameters.getName(),
                        parameters.getVersion(),
                        parameters.getQualifiers(),
                        parameters.getSubpath());
                // If we get here, then only the scheme can be invalid
                assertPurlEquals(parameters, purl);

                if (canonicalPurl != null && !canonicalPurl.equals(purl.toString())) {
                    throw new MalformedPackageURLException("The PackageURL scheme is invalid for purl: " + purl);
                }

                fail("Invalid package url components of '" + purl + "' should have caused an exception because "
                        + description);
            } catch (Exception e) {
                assertEquals(e.getClass(), getExpectedException(parameters));
            }
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
            assertThrowsExactly(
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
        // XXX: Can't assume canonical fields are equal to the test fields
        // assertEquals(emptyToNull(expected.getSubpath()), actual.getSubpath(), "subpath");
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
        assertEquals("npm", PackageURL.StandardTypes.NPM);
        assertEquals("nuget", PackageURL.StandardTypes.NUGET);
        assertEquals("qpkg", PackageURL.StandardTypes.QPKG);
        assertEquals("oci", PackageURL.StandardTypes.OCI);
        assertEquals("pub", PackageURL.StandardTypes.PUB);
        assertEquals("pypi", PackageURL.StandardTypes.PYPI);
        assertEquals("rpm", PackageURL.StandardTypes.RPM);
        assertEquals("hackage", PackageURL.StandardTypes.HACKAGE);
        assertEquals("hex", PackageURL.StandardTypes.HEX);
        assertEquals("huggingface", PackageURL.StandardTypes.HUGGINGFACE);
        assertEquals("luarocks", PackageURL.StandardTypes.LUAROCKS);
        assertEquals("maven", PackageURL.StandardTypes.MAVEN);
        assertEquals("mlflow", PackageURL.StandardTypes.MLFLOW);
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
}
