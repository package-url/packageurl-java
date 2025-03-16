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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
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

    private static Stream<Arguments> getTestData() throws IOException {
        return getTestDataFromFile("test-suite-data.json");
    }

    private static Stream<Arguments> getTestDataFromFile(String name) throws IOException {
        try (InputStream is = PackageURLTest.class.getResourceAsStream("/" + name)) {
            assertNotNull(is);
            JSONArray jsonArray = new JSONArray(new JSONTokener(is));
            return IntStream.range(0, jsonArray.length()).mapToObj(jsonArray::getJSONObject).map(PackageURLTest::createTestDefinition);
        }
    }

    @Test
    void validPercentEncoding() throws MalformedPackageURLException {
        PackageURL purl = new PackageURL("maven", "com.google.summit", "summit-ast", "2.2.0\n", null, null);
        assertEquals("pkg:maven/com.google.summit/summit-ast@2.2.0%0A", purl.toString());
        PackageURL purl2 = new PackageURL("pkg:nuget/%D0%9Cicros%D0%BEft.%D0%95ntit%D1%83Fram%D0%B5work%D0%A1%D0%BEr%D0%B5");
        assertEquals("Мicrosоft.ЕntitуFramеworkСоrе", purl2.getName());
        assertEquals("pkg:nuget/%D0%9Cicros%D0%BEft.%D0%95ntit%D1%83Fram%D0%B5work%D0%A1%D0%BEr%D0%B5", purl2.toString());
    }

    @SuppressWarnings("deprecation")
    @Test
    void invalidPercentEncoding() throws MalformedPackageURLException {
        assertThrowsExactly(MalformedPackageURLException.class, () -> new PackageURL("pkg:maven/com.google.summit/summit-ast@2.2.0%"));
        assertThrowsExactly(MalformedPackageURLException.class, () -> new PackageURL("pkg:maven/com.google.summit/summit-ast@2.2.0%0"));
        PackageURL purl = new PackageURL("pkg:maven/com.google.summit/summit-ast@2.2.0");
        Throwable t1 = assertThrowsExactly(ValidationException.class, () -> purl.uriDecode("%"));
        assertEquals("Incomplete percent encoding at offset 0 with value '%'", t1.getMessage());
        Throwable t2 = assertThrowsExactly(ValidationException.class, () -> purl.uriDecode("a%0"));
        assertEquals("Incomplete percent encoding at offset 1 with value '%0'", t2.getMessage());
        Throwable t3 = assertThrowsExactly(ValidationException.class, () -> purl.uriDecode("aaaa%%0A"));
        assertEquals("Invalid percent encoding char 1 at offset 5 with value '%'", t3.getMessage());
        Throwable t4 = assertThrowsExactly(ValidationException.class, () -> purl.uriDecode("%0G"));
        assertEquals("Invalid percent encoding char 2 at offset 2 with value 'G'", t4.getMessage());
    }

    private static Arguments createTestDefinition(JSONObject testDefinition) {
        JSONObject jsonQualifiers = testDefinition.optJSONObject("qualifiers");
        Map<String, Object> qualifiers = (jsonQualifiers != null && !jsonQualifiers.isEmpty()) ? jsonQualifiers.toMap() : Collections.emptyMap();
        return Arguments.of(
                testDefinition.getString("description"),
                testDefinition.getString("purl"),
                testDefinition.optString("canonical_purl"),
                testDefinition.optString("type"),
                testDefinition.optString("namespace", null),
                testDefinition.optString("name", null),
                testDefinition.optString("version", null),
                qualifiers,
                testDefinition.optString("subpath", null),
                testDefinition.getBoolean("is_invalid"));
    }

    @DisplayName("Test constructor parsing")
    @ParameterizedTest(name = "{0}: ''{1}''")
    @MethodSource("getTestData")
    void constructorParsing(String description, String purlString, String cpurlString, String type, String namespace, String name, String version, Map<String, String> qualifiers, String subpath, boolean invalid) throws Exception {
        if (invalid) {
            try {
                PackageURL purl = new PackageURL(purlString);
                fail("Invalid purl should have caused an exception: " + purl);
            } catch (MalformedPackageURLException e) {
                assertNotNull(e.getMessage());
            }

            return;
        }

        PackageURL purl = new PackageURL(purlString);

        assertEquals("pkg", purl.getScheme());
        assertEquals(type, purl.getType());
        assertEquals(namespace, purl.getNamespace());
        assertEquals(name, purl.getName());
        assertEquals(version, purl.getVersion());
        assertEquals(qualifiers, purl.getQualifiers());
        assertEquals(subpath, purl.getSubpath());
        assertEquals(cpurlString, purl.canonicalize());
    }

    @DisplayName("Test constructor parameters")
    @ParameterizedTest(name = "{0}: ({3}, {4}, {5}, {6}, {7}, {8})")
    @MethodSource("getTestData")
    void constructorParameters(String description, String purlString, String cpurlString, String type, String namespace, String name, String version, Map<String, String> qualifiers, String subpath, boolean invalid) throws MalformedPackageURLException {
        if (invalid) {
            try {
                PackageURL purl = new PackageURL(type, namespace, name, version, qualifiers, subpath);
                fail("Invalid package url components should have caused an exception: " + purl);
            } catch (NullPointerException | MalformedPackageURLException e) {
                assertNotNull(e.getMessage());
            }

            return;
        }

        PackageURL purl = new PackageURL(type, namespace, name, version, qualifiers, subpath);

        assertEquals(cpurlString, purl.canonicalize());
        assertEquals("pkg", purl.getScheme());
        assertEquals(type, purl.getType());
        assertEquals(namespace, purl.getNamespace());
        assertEquals(name, purl.getName());
        assertEquals(version, purl.getVersion());
        assertEquals(qualifiers, purl.getQualifiers());
        assertEquals(subpath, purl.getSubpath());
    }

    @Test
    void constructor() throws MalformedPackageURLException {
        PackageURL purl = new PackageURL("pkg:generic/namespace/name@1.0.0#");
        assertEquals("generic", purl.getType());
        assertNull(purl.getSubpath());

        purl = new PackageURL("pkg:generic/namespace/name@1.0.0?key=value==");
        assertEquals("generic", purl.getType());
        assertNotNull(purl.getQualifiers());
        assertEquals(1, purl.getQualifiers().size());
        assertTrue(purl.getQualifiers().containsValue("value=="));

        purl = new PackageURL("validtype", "name");
        assertNotNull(purl);

    }

    @Test
    void constructorWithEmptyType() {
        assertThrowsExactly(MalformedPackageURLException.class, () -> new PackageURL("", "name"), "constructor with an empty type should have thrown an error and this line should not be reached");
    }

    @Test
    void constructorWithInvalidCharsType() {
        assertThrowsExactly(MalformedPackageURLException.class, () -> new PackageURL("invalid^type", "name"), "constructor with `invalid^type` should have thrown an error and this line should not be reached");
    }

    @Test
    void constructorWithInvalidNumberType() {
        assertThrowsExactly(MalformedPackageURLException.class, () -> new PackageURL("0invalid", "name"), "constructor with `0invalid` should have thrown an error and this line should not be reached");
    }

    @Test
    void constructorWithInvalidSubpath() {
        assertThrowsExactly(MalformedPackageURLException.class, () -> new PackageURL("pkg:GOLANG/google.golang.org/genproto@abcdedf#invalid/%2F/subpath"), "constructor with `invalid/%2F/subpath` should have thrown an error and this line should not be reached");
    }


    @Test
    void constructorWithNullPurl() {
        assertThrowsExactly(NullPointerException.class, () -> new PackageURL(null), "constructor with null purl should have thrown an error and this line should not be reached");
    }

    @Test
    void constructorWithEmptyPurl() {
        assertThrowsExactly(MalformedPackageURLException.class, () -> new PackageURL(""), "constructor with empty purl should have thrown an error and this line should not be reached");
    }

    @Test
    void constructorWithPortNumber() {
        assertThrowsExactly(MalformedPackageURLException.class, () -> new PackageURL("pkg://generic:8080/name"), "constructor with port number should have thrown an error and this line should not be reached");
    }

    @Test
    void constructorWithUsername() {
        assertThrowsExactly(MalformedPackageURLException.class, () -> new PackageURL("pkg://user@generic/name"), "constructor with username should have thrown an error and this line should not be reached");
    }

    @Test
    void constructorWithInvalidUrl() {
        assertThrowsExactly(MalformedPackageURLException.class, () -> new PackageURL("invalid url"), "constructor with invalid url should have thrown an error and this line should not be reached");
    }

    @Test
    void constructorWithDuplicateQualifiers() {
        assertThrowsExactly(MalformedPackageURLException.class, () -> new PackageURL("pkg://generic/name?key=one&key=two"), "constructor with url with duplicate qualifiers should have thrown an error and this line should not be reached");
    }

    @Test
    void constructorDuplicateQualifiersMixedCase() {
        assertThrowsExactly(MalformedPackageURLException.class, () -> new PackageURL("pkg://generic/name?key=one&KEY=two"), "constructor with url with duplicate qualifiers should have thrown an error and this line should not be reached");
    }

    @Test
    void constructorWithUppercaseKey() throws MalformedPackageURLException {
        PackageURL purl = new PackageURL("pkg://generic/name?KEY=one");
        assertEquals(1, purl.getQualifiers().size(), "qualifier count");
        assertEquals("one", purl.getQualifiers().get("key"));
        Map<String, String> qualifiers = new TreeMap<>();
        qualifiers.put("key", "one");
        PackageURL purl2 = new PackageURL("generic", null, "name", null, qualifiers, null);
        assertEquals(purl, purl2);
    }

    @Test
    void constructorWithEmptyKey() throws MalformedPackageURLException {
        PackageURL purl = new PackageURL("pkg://generic/name?KEY");
        assertEquals(0, purl.getQualifiers().size(), "qualifier count");
        Map<String, String> qualifiers = new TreeMap<>();
        qualifiers.put("KEY", null);
        PackageURL purl2 = new PackageURL("generic", null, "name", null, qualifiers, null);
        assertEquals(purl, purl2);
        qualifiers.put("KEY", "");
        PackageURL purl3 = new PackageURL("generic", null, "name", null, qualifiers, null);
        assertEquals(purl2, purl3);
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
