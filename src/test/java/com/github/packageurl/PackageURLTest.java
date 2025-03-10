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

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.TreeMap;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test cases for PackageURL parsing
 * <p>
 * Original test cases retrieved from: https://raw.githubusercontent.com/package-url/purl-spec/master/test-suite-data.json
 *
 * @author Steve Springett
 */
public class PackageURLTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    private static JSONArray json = new JSONArray();

    private static Locale defaultLocale;

    @BeforeClass
    public static void setup() throws IOException {
        InputStream is = PackageURLTest.class.getResourceAsStream("/test-suite-data.json");
        String jsonTxt = IOUtils.toString(is, "UTF-8");
        json = new JSONArray(jsonTxt);
        defaultLocale = Locale.getDefault();
        Locale.setDefault(new Locale("tr"));
    }

    @AfterClass
    public static void resetLocale() {
        Locale.setDefault(defaultLocale);
    }

    @Test
    public void testConstructorParsing() throws Exception {
        exception = ExpectedException.none();
        for (int i = 0; i < json.length(); i++) {
            JSONObject testDefinition = json.getJSONObject(i);

            final String purlString = testDefinition.getString("purl");
            final String cpurlString = testDefinition.optString("canonical_purl");
            final boolean invalid = testDefinition.getBoolean("is_invalid");

            System.out.println("Running test on: " + purlString);

            final String type = testDefinition.optString("type", null);
            final String namespace = testDefinition.optString("namespace", null);
            final String name = testDefinition.optString("name", null);
            final String version = testDefinition.optString("version", null);
            final JSONObject qualifiers = testDefinition.optJSONObject("qualifiers");
            final String subpath = testDefinition.optString("subpath", null);

            if (invalid) {
                try {
                    PackageURL purl = new PackageURL(purlString);
                    Assert.fail("Inavlid purl should have caused an exception: " + purl.toString());
                } catch (MalformedPackageURLException e) {
                    Assert.assertNotNull(e.getMessage());
                }
                continue;
            }

            PackageURL purl = new PackageURL(purlString);

            Assert.assertEquals("pkg", purl.getScheme());
            Assert.assertEquals(type, purl.getType());
            Assert.assertEquals(namespace, purl.getNamespace());
            Assert.assertEquals(name, purl.getName());
            Assert.assertEquals(version, purl.getVersion());
            Assert.assertEquals(subpath, purl.getSubpath());
            if (qualifiers == null) {
                Assert.assertNull(purl.getQualifiers());
            } else {
                Assert.assertNotNull(purl.getQualifiers());
                Assert.assertEquals(qualifiers.length(), purl.getQualifiers().size());
                qualifiers.keySet().forEach((key) -> {
                    String value = qualifiers.getString(key);
                    Assert.assertTrue(purl.getQualifiers().containsKey(key));
                    Assert.assertEquals(value, purl.getQualifiers().get(key));
                });
            }
            Assert.assertEquals(cpurlString, purl.canonicalize());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testConstructorParameters() throws MalformedPackageURLException {
        exception = ExpectedException.none();
        for (int i = 0; i < json.length(); i++) {
            JSONObject testDefinition = json.getJSONObject(i);

            final String purlString = testDefinition.getString("purl");
            final String cpurlString = testDefinition.optString("canonical_purl");
            final boolean invalid = testDefinition.getBoolean("is_invalid");

            System.out.println("Running test on: " + purlString);

            final String type = testDefinition.optString("type", null);
            final String namespace = testDefinition.optString("namespace", null);
            final String name = testDefinition.optString("name", null);
            final String version = testDefinition.optString("version", null);
            final JSONObject qualifiers = testDefinition.optJSONObject("qualifiers");
            final String subpath = testDefinition.optString("subpath", null);

            TreeMap<String, String> map = null;
            if (qualifiers != null) {
                map = qualifiers.toMap().entrySet().stream().collect(
                        TreeMap<String, String>::new,
                        (qmap, entry) -> qmap.put(entry.getKey(), (String) entry.getValue()),
                        TreeMap<String, String>::putAll
                );
            }

            if (invalid) {
                try {
                    PackageURL purl = new PackageURL(type, namespace, name, version, map, subpath);
                    Assert.fail("Invalid package url components should have caused an exception: " + purl.toString());
                } catch (MalformedPackageURLException e) {
                    Assert.assertNotNull(e.getMessage());
                }
                continue;
            }

            PackageURL purl = new PackageURL(type, namespace, name, version, map, subpath);

            Assert.assertEquals(cpurlString, purl.canonicalize());
            Assert.assertEquals("pkg", purl.getScheme());
            Assert.assertEquals(type, purl.getType());
            Assert.assertEquals(namespace, purl.getNamespace());
            Assert.assertEquals(name, purl.getName());
            Assert.assertEquals(version, purl.getVersion());
            Assert.assertEquals(subpath, purl.getSubpath());
            if (qualifiers != null) {
                Assert.assertNotNull(purl.getQualifiers());
                Assert.assertEquals(qualifiers.length(), purl.getQualifiers().size());
                qualifiers.keySet().forEach((key) -> {
                    String value = qualifiers.getString(key);
                    Assert.assertTrue(purl.getQualifiers().containsKey(key));
                    Assert.assertEquals(value, purl.getQualifiers().get(key));
                });
            }
        }
    }

    @Test
    public void testConstructor() throws MalformedPackageURLException {
        exception = ExpectedException.none();

        PackageURL purl = new PackageURL("pkg:generic/namespace/name@1.0.0#");
        Assert.assertEquals("generic", purl.getType());
        Assert.assertNull(purl.getSubpath());

        purl = new PackageURL("pkg:generic/namespace/name@1.0.0?key=value==");
        Assert.assertEquals("generic", purl.getType());
        Assert.assertEquals(1, purl.getQualifiers().size());
        Assert.assertTrue(purl.getQualifiers().containsValue("value=="));

        purl = new PackageURL("validtype", "name");
        Assert.assertNotNull(purl);

    }

    @Test
    public void testConstructorWithEmptyType() throws MalformedPackageURLException {
        exception.expect(MalformedPackageURLException.class);

        PackageURL purl = new PackageURL("", "name");
        Assert.fail("constructor with an empty type should have thrown an error and this line should not be reached");
    }

    @Test
    public void testConstructorWithInvalidCharsType() throws MalformedPackageURLException {
        exception.expect(MalformedPackageURLException.class);

        PackageURL purl = new PackageURL("invalid^type", "name");
        Assert.fail("constructor with `invalid^type` should have thrown an error and this line should not be reached");
    }

    @Test
    public void testConstructorWithInvalidNumberType() throws MalformedPackageURLException {
        exception.expect(MalformedPackageURLException.class);

        PackageURL purl = new PackageURL("0invalid", "name");
        Assert.fail("constructor with `0invalid` should have thrown an error and this line should not be reached");
    }

    @Test
    public void testConstructorWithInvalidSubpath() throws MalformedPackageURLException {
        exception.expect(MalformedPackageURLException.class);

        PackageURL purl = new PackageURL("pkg:GOLANG/google.golang.org/genproto@abcdedf#invalid/%2F/subpath");
        Assert.fail("constructor with `invalid/%2F/subpath` should have thrown an error and this line should not be reached");
    }


    @Test
    public void testConstructorWithNullPurl() throws MalformedPackageURLException {
        exception.expect(MalformedPackageURLException.class);

        PackageURL purl = new PackageURL(null);
        Assert.fail("constructor with null purl should have thrown an error and this line should not be reached");
    }

    @Test
    public void testConstructorWithEmptyPurl() throws MalformedPackageURLException {
        exception.expect(MalformedPackageURLException.class);

        PackageURL purl = new PackageURL("");
        Assert.fail("constructor with empty purl should have thrown an error and this line should not be reached");
    }

    @Test
    public void testConstructorWithPortNumber() throws MalformedPackageURLException {
        exception.expect(MalformedPackageURLException.class);

        PackageURL purl = new PackageURL("pkg://generic:8080/name");
        Assert.fail("constructor with port number should have thrown an error and this line should not be reached");
    }

    @Test
    public void testConstructorWithUsername() throws MalformedPackageURLException {
        exception.expect(MalformedPackageURLException.class);

        PackageURL purl = new PackageURL("pkg://user@generic/name");
        Assert.fail("constructor with username should have thrown an error and this line should not be reached");
    }

    @Test
    public void testConstructorWithInvalidUrl() throws MalformedPackageURLException {
        exception.expect(MalformedPackageURLException.class);

        PackageURL purl = new PackageURL("invalid url");
        Assert.fail("constructor with invalid url should have thrown an error and this line should not be reached");
    }

    @Test
    public void testConstructorWithDuplicateQualifiers() throws MalformedPackageURLException {
        exception.expect(MalformedPackageURLException.class);

        PackageURL purl = new PackageURL("pkg://generic/name?key=one&key=two");
        Assert.fail("constructor with url with duplicate qualifiers should have thrown an error and this line should not be reached");
    }

    @Test
    public void testConstructorDuplicateQualifiersMixedCase() throws MalformedPackageURLException {
        exception.expect(MalformedPackageURLException.class);

        PackageURL purl = new PackageURL("pkg://generic/name?key=one&KEY=two");
        Assert.fail("constructor with url with duplicate qualifiers should have thrown an error and this line should not be reached");
    }

    @Test
    public void testConstructorWithUppercaseKey() throws MalformedPackageURLException {
        PackageURL purl = new PackageURL("pkg://generic/name?KEY=one");
        Assert.assertNotNull(purl.getQualifiers());
        Assert.assertEquals("one", purl.getQualifiers().get("key"));
        TreeMap<String, String> qualifiers = new TreeMap<>();
        qualifiers.put("key", "one");
        PackageURL purl2 = new PackageURL("generic", null, "name", null, qualifiers, null);
        Assert.assertEquals(purl, purl2);
    }

    @Test
    public void testConstructorWithEmptyKey() throws MalformedPackageURLException {
        PackageURL purl = new PackageURL("pkg://generic/name?KEY");
        Assert.assertNull(purl.getQualifiers());
        TreeMap<String, String> qualifiers = new TreeMap<>();
        qualifiers.put("KEY", null);
        PackageURL purl2 = new PackageURL("generic", null, "name", null, qualifiers, null);
        Assert.assertEquals(purl, purl2);
        qualifiers.put("KEY", "");
        PackageURL purl3 = new PackageURL("generic", null, "name", null, qualifiers, null);
        Assert.assertEquals(purl2, purl3);
    }

    @Test
    public void testStandardTypes() {
        exception = ExpectedException.none();
        Assert.assertEquals(PackageURL.StandardTypes.BITBUCKET, "bitbucket");
        Assert.assertEquals(PackageURL.StandardTypes.CARGO, "cargo");
        Assert.assertEquals(PackageURL.StandardTypes.COMPOSER, "composer");
        Assert.assertEquals(PackageURL.StandardTypes.DEBIAN, "deb");
        Assert.assertEquals(PackageURL.StandardTypes.DOCKER, "docker");
        Assert.assertEquals(PackageURL.StandardTypes.GEM, "gem");
        Assert.assertEquals(PackageURL.StandardTypes.GENERIC, "generic");
        Assert.assertEquals(PackageURL.StandardTypes.GITHUB, "github");
        Assert.assertEquals(PackageURL.StandardTypes.GOLANG, "golang");
        Assert.assertEquals(PackageURL.StandardTypes.HEX, "hex");
        Assert.assertEquals(PackageURL.StandardTypes.MAVEN, "maven");
        Assert.assertEquals(PackageURL.StandardTypes.NPM, "npm");
        Assert.assertEquals(PackageURL.StandardTypes.NUGET, "nuget");
        Assert.assertEquals(PackageURL.StandardTypes.PYPI, "pypi");
        Assert.assertEquals(PackageURL.StandardTypes.RPM, "rpm");
        Assert.assertEquals(PackageURL.StandardTypes.NIXPKGS, "nixpkgs");
        Assert.assertEquals(PackageURL.StandardTypes.HACKAGE, "hackage");
    }

    @Test
    public void testBaseEquals() throws Exception {
        PackageURL p1 = new PackageURL("pkg:generic/acme/example-component@1.0.0?key1=value1&key2=value2");
        PackageURL p2 = new PackageURL("pkg:generic/acme/example-component@1.0.0");
        Assert.assertTrue(p1.isBaseEquals(p2));
    }

    @Test
    public void testCanonicalEquals() throws Exception {
        PackageURL p1 = new PackageURL("pkg:generic/acme/example-component@1.0.0?key1=value1&key2=value2");
        PackageURL p2 = new PackageURL("pkg:generic/acme/example-component@1.0.0?key2=value2&key1=value1");
        Assert.assertTrue(p1.isCanonicalEquals(p2));
    }

    @Test
    public void testGetCoordinates() throws Exception {
        PackageURL purl = new PackageURL("pkg:generic/acme/example-component@1.0.0?key1=value1&key2=value2");
        Assert.assertEquals("pkg:generic/acme/example-component@1.0.0", purl.getCoordinates());
    }

    @Test
    public void testGetCoordinatesNoCacheIssue89() throws Exception {
        PackageURL purl = new PackageURL("pkg:generic/acme/example-component@1.0.0?key1=value1&key2=value2");
        purl.canonicalize();
        Assert.assertEquals("pkg:generic/acme/example-component@1.0.0", purl.getCoordinates());
    }

    @Test
    public void testNpmCaseSensitive() throws Exception {
        // e.g. https://www.npmjs.com/package/base64/v/1.0.0
        PackageURL base64Lowercase = new PackageURL("pkg:npm/base64@1.0.0");
        Assert.assertEquals(base64Lowercase.getType(), "npm");
        Assert.assertEquals(base64Lowercase.getName(), "base64");
        Assert.assertEquals(base64Lowercase.getVersion(), "1.0.0");

        // e.g. https://www.npmjs.com/package/Base64/v/1.0.0
        PackageURL base64Uppercase = new PackageURL("pkg:npm/Base64@1.0.0");
        Assert.assertEquals(base64Uppercase.getType(), "npm");
        Assert.assertEquals(base64Uppercase.getName(), "Base64");
        Assert.assertEquals(base64Uppercase.getVersion(), "1.0.0");
    }
}
