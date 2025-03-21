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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class PackageURLBuilderTest {

    static Stream<Arguments> packageURLBuilder() throws IOException {
        return PurlParameters.getTestDataFromFiles("test-suite-data.json", "components-constructor-only.json");
    }

    @ParameterizedTest(name = "{0}: {1}")
    @MethodSource
    void packageURLBuilder(
            String description,
            @Nullable String ignoredPurl,
            PurlParameters parameters,
            String canonicalPurl,
            boolean invalid)
            throws MalformedPackageURLException {
        if (parameters.getType() == null || parameters.getName() == null) {
            assertTrue(invalid, "valid test case with type or name `null`");
            return;
        }
        PackageURLBuilder builder =
                PackageURLBuilder.aPackageURL().withType(parameters.getType()).withName(parameters.getName());
        String namespace = parameters.getNamespace();
        if (namespace != null) {
            builder.withNamespace(namespace);
        }
        String version = parameters.getVersion();
        if (version != null) {
            builder.withVersion(version);
        }
        parameters.getQualifiers().forEach(builder::withQualifier);
        String subpath = parameters.getSubpath();
        if (subpath != null) {
            builder.withSubpath(subpath);
        }
        if (invalid) {
            assertThrows(MalformedPackageURLException.class, builder::build);
        } else {
            assertEquals(parameters.getType(), builder.getType(), "type");
            assertEquals(parameters.getNamespace(), builder.getNamespace(), "namespace");
            assertEquals(parameters.getName(), builder.getName(), "name");
            assertEquals(parameters.getVersion(), builder.getVersion(), "version");
            assertEquals(parameters.getQualifiers(), builder.getQualifiers(), "qualifiers");
            assertEquals(parameters.getSubpath(), builder.getSubpath(), "subpath");
            PackageURL actual = builder.build();
            assertEquals(canonicalPurl, actual.canonicalize(), "canonical PURL");
        }
    }

    @Test
    void packageURLBuilderException1() throws MalformedPackageURLException {
        PackageURL purl = PackageURLBuilder.aPackageURL()
                .withType("type")
                .withName("name")
                .withQualifier("key", "")
                .build();
        assertEquals(0, purl.getQualifiers().size(), "qualifier count");
    }

    @Test
    void packageURLBuilderException1Null() throws MalformedPackageURLException {
        PackageURL purl = PackageURLBuilder.aPackageURL()
                .withType("type")
                .withName("name")
                .withQualifier("key", null)
                .build();
        assertEquals(0, purl.getQualifiers().size(), "qualifier count");
    }

    @Test
    void packageURLBuilderException2() {
        assertThrowsExactly(
                MalformedPackageURLException.class,
                () -> {
                    PackageURLBuilder.aPackageURL()
                            .withType("type")
                            .withNamespace("invalid//namespace")
                            .withName("name")
                            .build();
                },
                "Build should fail due to invalid namespace");
    }

    @Test
    void packageURLBuilderException3() {
        assertThrowsExactly(
                MalformedPackageURLException.class,
                () -> {
                    PackageURLBuilder.aPackageURL()
                            .withType("typ^e")
                            .withSubpath("invalid/name%2Fspace")
                            .withName("name")
                            .build();
                },
                "Build should fail due to invalid subpath");
    }

    @Test
    void packageURLBuilderException4() {
        assertThrowsExactly(
                MalformedPackageURLException.class,
                () -> {
                    PackageURLBuilder.aPackageURL()
                            .withType("0_type")
                            .withName("name")
                            .build();
                },
                "Build should fail due to invalid type");
    }

    @Test
    void packageURLBuilderException5() {
        assertThrowsExactly(
                MalformedPackageURLException.class,
                () -> {
                    PackageURLBuilder.aPackageURL()
                            .withType("ype")
                            .withName("name")
                            .withQualifier("0_key", "value")
                            .build();
                },
                "Build should fail due to invalid qualifier key");
    }

    @Test
    void packageURLBuilderException6() {
        assertThrowsExactly(
                MalformedPackageURLException.class,
                () -> {
                    PackageURLBuilder.aPackageURL()
                            .withType("ype")
                            .withName("name")
                            .withQualifier("", "value")
                            .build();
                },
                "Build should fail due to invalid qualifier key");
    }

    @Test
    void editBuilder1() throws MalformedPackageURLException {
        PackageURL p = new PackageURL("pkg:generic/namespace/name@1.0.0?k=v#s");
        PackageURLBuilder b = PackageURLBuilder.aPackageURL(p);
        assertBuilderMatch(p, b);

        assertBuilderMatch(new PackageURL("pkg:generic/namespace/name@1.0.0#s"), b.withoutQualifiers());
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

    @Test
    void qualifiers() throws MalformedPackageURLException {
        Map<String, String> qualifiers = new HashMap<>();
        qualifiers.put("key2", "value2");
        Map<String, String> qualifiers2 = new HashMap<>();
        qualifiers.put("key3", "value3");
        PackageURL purl = PackageURLBuilder.aPackageURL()
                .withType(PackageURL.StandardTypes.GENERIC)
                .withNamespace("")
                .withName("name")
                .withVersion("version")
                .withQualifier("key", "value")
                .withQualifier("next", "value")
                .withQualifiers(qualifiers)
                .withQualifier("key4", "value4")
                .withQualifiers(qualifiers2)
                .withSubpath("")
                .withoutQualifiers(Collections.singleton("key4"))
                .build();

        assertEquals("pkg:generic/name@version?key=value&key2=value2&key3=value3&next=value", purl.toString());
    }

    @Test
    void fromExistingPurl() throws MalformedPackageURLException {
        String purl = "pkg:generic/namespace/name@1.0.0?k=v#s";
        PackageURL p = new PackageURL(purl);
        PackageURL purl2 = PackageURLBuilder.aPackageURL(p).build();
        PackageURL purl3 = PackageURLBuilder.aPackageURL(purl).build();
        assertEquals(p, purl2);
        assertEquals(purl2, purl3);
    }

    private void assertBuilderMatch(PackageURL expected, PackageURLBuilder actual) throws MalformedPackageURLException {

        assertEquals(expected.toString(), actual.build().toString());
        assertEquals(expected.getType(), actual.getType());
        assertEquals(expected.getNamespace(), actual.getNamespace());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getVersion(), actual.getVersion());
        assertEquals(expected.getSubpath(), actual.getSubpath());

        Map<String, String> eQualifiers = expected.getQualifiers();
        Map<String, String> aQualifiers = actual.getQualifiers();

        assertEquals(eQualifiers, aQualifiers);

        eQualifiers.forEach((k, v) -> assertEquals(v, actual.getQualifier(k)));
    }
}
