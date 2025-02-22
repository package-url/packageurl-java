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

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Map;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

public class PackageURLBuilderTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testPackageURLBuilder() throws MalformedPackageURLException {
        exception = ExpectedException.none();

        PackageURL purl = PackageURLBuilder.aPackageURL()
                .withType("my.type-9+")
                .withName("name")
                .build();

        assertEquals("pkg:my.type-9+/name", purl.toString());
        assertEquals("pkg:my.type-9+/name", purl.canonicalize());

        purl = PackageURLBuilder.aPackageURL()
                .withType("type")
                .withNamespace("namespace")
                .withName("name")
                .withVersion("version")
                .withQualifier("key","value")
                .withSubpath("subpath")
                .build();

        assertEquals("pkg:type/namespace/name@version?key=value#subpath", purl.toString());

        purl = PackageURLBuilder.aPackageURL()
                .withType(PackageURL.StandardTypes.GENERIC)
                .withNamespace("namespace")
                .withName("name")
                .withVersion("version")
                .withQualifier("key_1.1-","value")
                .withSubpath("subpath")
                .build();

        assertEquals("pkg:generic/namespace/name@version?key_1.1-=value#subpath", purl.toString());

        purl = PackageURLBuilder.aPackageURL()
                .withType(PackageURL.StandardTypes.GENERIC)
                .withNamespace("/////")
                .withName("name")
                .withVersion("version")
                .withQualifier("key","value")
                .withSubpath("/////")
                .build();

        assertEquals("pkg:generic/name@version?key=value", purl.toString());

        purl = PackageURLBuilder.aPackageURL()
                .withType(PackageURL.StandardTypes.GENERIC)
                .withNamespace("")
                .withName("name")
                .withVersion("version")
                .withQualifier("key","value")
                .withQualifier("next","value")
                .withSubpath("")
                .build();

        assertEquals("pkg:generic/name@version?key=value&next=value", purl.toString());
    }

    @Test
    public void testPackageURLBuilderException1() throws MalformedPackageURLException {
        exception.expect(MalformedPackageURLException.class);
        exception.expectMessage(allOf(containsString("qualifier value"), containsString("empty or null")));
        PackageURLBuilder.aPackageURL()
                .withType("type")
                .withName("name")
                .withQualifier("key","")
                .build();
        Assert.fail("Build should fail due to invalid qualifier (empty value)");
    }

    @Test
    public void testPackageURLBuilderException1Null() throws MalformedPackageURLException {
        exception.expect(NullPointerException.class);
        exception.expectMessage("can not be null");
        PackageURLBuilder.aPackageURL()
                .withType("type")
                .withName("name")
                .withQualifier("key",null)
                .build();
        Assert.fail("Build should fail due to invalid qualifier (null value)");
    }

    @Test
    public void testPackageURLBuilderException2() throws MalformedPackageURLException {
        exception.expect(MalformedPackageURLException.class);
        PackageURLBuilder.aPackageURL()
                .withType("type")
                .withNamespace("invalid//namespace")
                .withName("name")
                .build();
        Assert.fail("Build should fail due to invalid namespace");
    }

    @Test
    public void testPackageURLBuilderException3() throws MalformedPackageURLException {
        exception.expect(MalformedPackageURLException.class);
        PackageURLBuilder.aPackageURL()
                .withType("typ^e")
                .withSubpath("invalid/name%2Fspace")
                .withName("name")
                .build();
        Assert.fail("Build should fail due to invalid subpath");
    }

    @Test
    public void testPackageURLBuilderException4() throws MalformedPackageURLException {
        exception.expect(MalformedPackageURLException.class);
        PackageURLBuilder.aPackageURL()
                .withType("0_type")
                .withName("name")
                .build();
        Assert.fail("Build should fail due to invalid type");
    }

    @Test
    public void testPackageURLBuilderException5() throws MalformedPackageURLException {
        exception.expect(MalformedPackageURLException.class);
        PackageURLBuilder.aPackageURL()
                .withType("ype")
                .withName("name")
                .withQualifier("0_key","value")
                .build();
        Assert.fail("Build should fail due to invalid qualifier key");
    }

    @Test
    public void testPackageURLBuilderException6() throws MalformedPackageURLException {
        exception.expect(MalformedPackageURLException.class);
        PackageURLBuilder.aPackageURL()
                .withType("ype")
                .withName("name")
                .withQualifier("","value")
                .build();
        Assert.fail("Build should fail due to invalid qualifier key");
    }

    @Test
    public void testEditBuilder1() throws MalformedPackageURLException {

        PackageURL p = new PackageURL("pkg:generic/namespace/name@1.0.0?k=v#s");
        PackageURLBuilder b = p.toBuilder();
        assertBuilderMatch(p, b);

        assertBuilderMatch(new PackageURL("pkg:generic/namespace/name@1.0.0#s"), b.withNoQualifiers());
        b.withType("maven")
                .withNamespace("org.junit")
                .withName("junit5")
                .withVersion("3.1.2")
                .withSubpath("sub")
                .withQualifier("repo", "maven")
                .withQualifier("dark", "matter")
                .withQualifier("ping", "pong")
                .withoutQualifier("dark");

        assertBuilderMatch(new PackageURL("pkg:maven/org.junit/junit5@3.1.2?repo=maven&ping=pong#sub"), b);

    }

    private void assertBuilderMatch(PackageURL expected, PackageURLBuilder actual) throws MalformedPackageURLException {

        Assert.assertEquals(expected.toString(), actual.build().toString());
        Assert.assertEquals(expected.getType(), actual.getType());
        Assert.assertEquals(expected.getNamespace(), actual.getNamespace());
        Assert.assertEquals(expected.getName(), actual.getName());
        Assert.assertEquals(expected.getVersion(), actual.getVersion());
        Assert.assertEquals(expected.getSubpath(), actual.getSubpath());

        Map<String, String> eQualifiers = expected.getQualifiers();
        Map<String, String> aQualifiers = actual.getQualifiers();

        if (eQualifiers != null) {
            eQualifiers.forEach((k,v)-> {
                Assert.assertEquals(v, aQualifiers.remove(k));
                Assert.assertEquals(v, actual.getQualifier(k));
            });
        }

        Assert.assertTrue(aQualifiers.isEmpty());

    }

}