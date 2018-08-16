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

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import java.io.IOException;
import java.io.InputStream;
import java.util.TreeMap;

/**
 * Test cases for PackageURL parsing
 *
 * Original test cases retrieved from: https://raw.githubusercontent.com/package-url/purl-spec/master/test-suite-data.json
 *
 * @author Steve Springett
 */
public class PackageURLTest {

    private static JSONArray json = new JSONArray();

    @BeforeClass
    public static void setup() throws IOException {
        InputStream is = PackageURLTest.class.getResourceAsStream("/test-suite-data.json");
        String jsonTxt = IOUtils.toString(is, "UTF-8");
        json = new JSONArray(jsonTxt);
    }

    @Test
    public void testConstructorParsing() throws Exception {
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
                    new PackageURL(purlString);
                    Assert.fail();
                } catch (MalformedPackageURLException e) {
                    Assert.assertNotNull(e.getMessage());
                }
                continue;
            }

            PackageURL purl = new PackageURL(purlString);
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
                for (String key: qualifiers.keySet()) {
                    String value = qualifiers.getString(key);
                    Assert.assertTrue(purl.getQualifiers().containsKey(key));
                    Assert.assertEquals(value, purl.getQualifiers().get(key));
                }
            }
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testConstructorParameters() throws MalformedPackageURLException {
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

            TreeMap map = new TreeMap();
            if (qualifiers != null) {
                map.putAll(qualifiers.toMap());
            }

            if (invalid) {
                try {
                    new PackageURL(type, namespace, name, version, map, subpath);
                    Assert.fail();
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
                for (String key: qualifiers.keySet()) {
                    String value = qualifiers.getString(key);
                    Assert.assertTrue(purl.getQualifiers().containsKey(key));
                    Assert.assertEquals(value, purl.getQualifiers().get(key));
                }
            }
        }
    }

    @Test
    public void testStandardTypes() {
        Assert.assertEquals(PackageURL.StandardTypes.BITBUCKET, "bitbucket");
        Assert.assertEquals(PackageURL.StandardTypes.COMPOSER, "composer");
        Assert.assertEquals(PackageURL.StandardTypes.DEBIAN, "deb");
        Assert.assertEquals(PackageURL.StandardTypes.DOCKER, "docker");
        Assert.assertEquals(PackageURL.StandardTypes.GEM, "gem");
        Assert.assertEquals(PackageURL.StandardTypes.GENERIC, "generic");
        Assert.assertEquals(PackageURL.StandardTypes.GITHUB, "github");
        Assert.assertEquals(PackageURL.StandardTypes.GOLANG, "golang");
        Assert.assertEquals(PackageURL.StandardTypes.MAVEN, "maven");
        Assert.assertEquals(PackageURL.StandardTypes.NPM, "npm");
        Assert.assertEquals(PackageURL.StandardTypes.NUGET, "nuget");
        Assert.assertEquals(PackageURL.StandardTypes.PYPI, "pypi");
        Assert.assertEquals(PackageURL.StandardTypes.RPM, "rpm");
    }

}
