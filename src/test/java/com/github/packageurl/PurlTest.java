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

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test cases for PURL parsing
 *
 * Original test cases retrieved from: https://github.com/package-url/packageurl-python/blob/master/test-suite-data.json
 */
public class PurlTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testValidMaven() throws Exception {
        exception = ExpectedException.none();
        PURL purl = new PURL("maven:org.apache.commons/io@1.3.4");
        Assert.assertEquals("maven:org.apache.commons/io@1.3.4", purl.canonicalize());
        Assert.assertEquals("maven", purl.getType());
        Assert.assertEquals("org.apache.commons", purl.getNamespace());
        Assert.assertEquals("io", purl.getName());
        Assert.assertEquals("1.3.4", purl.getVersion());
        Assert.assertNull(purl.getQualifiers());
        Assert.assertNull(purl.getSubpath());
    }

    @Test
    public void testValidMavenWithoutVersion() throws Exception {
        exception = ExpectedException.none();
        PURL purl = new PURL("maven:org.apache.commons/io");
        Assert.assertEquals("maven:org.apache.commons/io", purl.canonicalize());
        Assert.assertEquals("maven", purl.getType());
        Assert.assertEquals("org.apache.commons", purl.getNamespace());
        Assert.assertEquals("io", purl.getName());
        Assert.assertNull(purl.getVersion());
        Assert.assertNull(purl.getQualifiers());
        Assert.assertNull(purl.getSubpath());
    }

    @Test
    public void testValidGoWithoutVersionAndWithSubpath() throws Exception {
        exception = ExpectedException.none();
        PURL purl = new PURL("GO:google.golang.org/genproto#/googleapis/api/annotations/");
        Assert.assertEquals("go:google.golang.org/genproto#googleapis/api/annotations", purl.canonicalize());
        Assert.assertEquals("go", purl.getType());
        Assert.assertEquals("google.golang.org", purl.getNamespace());
        Assert.assertEquals("genproto", purl.getName());
        Assert.assertNull(purl.getVersion());
        Assert.assertNull(purl.getQualifiers());
        Assert.assertEquals("googleapis/api/annotations", purl.getSubpath());
    }

    @Test
    public void testValidGoWithVersionAndSubpath() throws Exception {
        exception = ExpectedException.none();
        PURL purl = new PURL("GO:google.golang.org/genproto@abcdedf#/googleapis/api/annotations/");
        Assert.assertEquals("go:google.golang.org/genproto@abcdedf#googleapis/api/annotations", purl.canonicalize());
        Assert.assertEquals("go", purl.getType());
        Assert.assertEquals("google.golang.org", purl.getNamespace());
        Assert.assertEquals("genproto", purl.getName());
        Assert.assertEquals("abcdedf", purl.getVersion());
        Assert.assertNull(purl.getQualifiers());
        Assert.assertEquals("googleapis/api/annotations", purl.getSubpath());
    }

    @Test
    public void testBitbucketNamespaceAndNameShouldBeLowercased() throws Exception {
        exception = ExpectedException.none();
        PURL purl = new PURL("bitbucket:birKenfeld/pyGments-main@244fd47e07d1014f0aed9c");
        Assert.assertEquals("bitbucket:birkenfeld/pygments-main@244fd47e07d1014f0aed9c", purl.canonicalize());
        Assert.assertEquals("bitbucket", purl.getType());
        Assert.assertEquals("birkenfeld", purl.getNamespace());
        Assert.assertEquals("pygments-main", purl.getName());
        Assert.assertEquals("244fd47e07d1014f0aed9c", purl.getVersion());
        Assert.assertNull(purl.getQualifiers());
        Assert.assertNull(purl.getSubpath());
    }

    @Test
    public void testGithubNamespaceAndNameShouldBeLowercased() throws Exception {
        exception = ExpectedException.none();
        PURL purl = new PURL("github:package-url/purl-spec@244fd47e07d1004f0aed9c");
        Assert.assertEquals("github:package-url/purl-spec@244fd47e07d1004f0aed9c", purl.canonicalize());
        Assert.assertEquals("github", purl.getType());
        Assert.assertEquals("package-url", purl.getNamespace());
        Assert.assertEquals("purl-spec", purl.getName());
        Assert.assertEquals("244fd47e07d1004f0aed9c", purl.getVersion());
        Assert.assertNull(purl.getQualifiers());
        Assert.assertNull(purl.getSubpath());
    }

    @Test
    public void testDebianCanUseQualifiers() throws Exception {
        exception = ExpectedException.none();
        PURL purl = new PURL("deb:curl@7.50.3-1?arch=i386&distro=jessie");
        Assert.assertEquals("deb:curl@7.50.3-1?arch=i386&distro=jessie", purl.canonicalize());
        Assert.assertEquals("deb", purl.getType());
        Assert.assertNull(purl.getNamespace());
        Assert.assertEquals("curl", purl.getName());
        Assert.assertEquals("7.50.3-1", purl.getVersion());
        Assert.assertNull(purl.getQualifiers()); //todo {"arch": "i386", "distro": "jessie"}
        Assert.assertNull(purl.getSubpath());
    }

    @Test
    public void testDockerUsesQualifiersAndHashImageIdAsVersions() throws Exception {
        exception = ExpectedException.none();
        PURL purl = new PURL("docker:customer/dockerimage@sha256:244fd47e07d1004f0aed9c?repository_url=gcr.io");
        Assert.assertEquals("docker:customer/dockerimage@sha256:244fd47e07d1004f0aed9c?repository_url=gcr.io", purl.canonicalize());
        Assert.assertEquals("docker", purl.getType());
        Assert.assertEquals("customer", purl.getNamespace());
        Assert.assertEquals("dockerimage", purl.getName());
        Assert.assertEquals("sha256:244fd47e07d1004f0aed9c", purl.getVersion());
        Assert.assertNull(purl.getQualifiers()); //todo {"repository_url": "gcr.io"}
        Assert.assertNull(purl.getSubpath());
    }

    @Test
    public void testGemUsesQualifiers() throws Exception {
        exception = ExpectedException.none();
        PURL purl = new PURL("gem:jruby-launcher@1.1.2?Platform=java");
        Assert.assertEquals("gem:jruby-launcher@1.1.2?platform=java", purl.canonicalize());
        Assert.assertEquals("gem", purl.getType());
        Assert.assertNull(purl.getNamespace());
        Assert.assertEquals("jruby-launcher", purl.getName());
        Assert.assertEquals("1.1.2", purl.getVersion());
        Assert.assertNull(purl.getQualifiers()); //todo {"platform": "java"}
        Assert.assertNull(purl.getSubpath());
    }

    @Test
    public void testMavenUsesQualifiers() throws Exception {
        exception = ExpectedException.none();
        PURL purl = new PURL("Maven:org.apache.xmlgraphics/batik-anim@1.9.1?repositorY_url=repo.spring.io/release&Packaging=sources");
        Assert.assertEquals("maven:org.apache.xmlgraphics/batik-anim@1.9.1?packaging=sources&repository_url=repo.spring.io/release", purl.canonicalize());
        Assert.assertEquals("maven", purl.getType());
        Assert.assertEquals("org.apache.xmlgraphics", purl.getNamespace());
        Assert.assertEquals("batik-anim", purl.getName());
        Assert.assertEquals("1.9.1", purl.getVersion());
        Assert.assertNull(purl.getQualifiers()); //todo {"packaging": "sources", "repository_url": "repo.spring.io/release"}
        Assert.assertNull(purl.getSubpath());
    }

    @Test
    public void testNpmCanBeScoped() throws Exception {
        exception = ExpectedException.none();
        PURL purl = new PURL("npm:%40angular/animation@12.3.1");
        Assert.assertEquals("npm:%40angular/animation@12.3.1", purl.canonicalize());
        Assert.assertEquals("npm", purl.getType());
        Assert.assertEquals("@angular", purl.getNamespace());
        Assert.assertEquals("animation", purl.getName());
        Assert.assertEquals("12.3.1", purl.getVersion());
        Assert.assertNull(purl.getQualifiers());
        Assert.assertNull(purl.getSubpath());
    }

    @Test
    public void testNugetNamesAreCaseSensitive() throws Exception {
        exception = ExpectedException.none();
        PURL purl = new PURL("Nuget:EnterpriseLibrary.Common@6.0.1304");
        Assert.assertEquals("nuget:EnterpriseLibrary.Common@6.0.1304", purl.canonicalize());
        Assert.assertEquals("nuget", purl.getType());
        Assert.assertNull(purl.getNamespace());
        Assert.assertEquals("EnterpriseLibrary.Common", purl.getName());
        Assert.assertEquals("6.0.1304", purl.getVersion());
        Assert.assertNull(purl.getQualifiers());
        Assert.assertNull(purl.getSubpath());
    }

    @Test
    public void testPypiNamesHaveSpecialRulesAndNotCaseSensitive() throws Exception {
        exception = ExpectedException.none();
        PURL purl = new PURL("PYPI:Django_package@1.11.1.dev1");
        Assert.assertEquals("pypi:django-package@1.11.1.dev1", purl.canonicalize());
        Assert.assertEquals("pypi", purl.getType());
        Assert.assertNull(purl.getNamespace());
        Assert.assertEquals("django-package", purl.getName());
        Assert.assertEquals("1.11.1.dev1", purl.getVersion());
        Assert.assertNull(purl.getQualifiers());
        Assert.assertNull(purl.getSubpath());
    }

    @Test
    public void testRpmUseQualifiers() throws Exception {
        exception = ExpectedException.none();
        PURL purl = new PURL("Rpm:curl@7.50.3-1.fc25?Arch=i386&Distro=fedora-25");
        Assert.assertEquals("rpm:curl@7.50.3-1.fc25?arch=i386&distro=fedora-25", purl.canonicalize());
        Assert.assertEquals("rpm", purl.getType());
        Assert.assertNull(purl.getNamespace());
        Assert.assertEquals("curl", purl.getName());
        Assert.assertEquals("7.50.3-1.fc25", purl.getVersion());
        Assert.assertNull(purl.getQualifiers()); //todo {"arch": "i386", "distro": "fedora-25"}
        Assert.assertNull(purl.getSubpath());
    }

    @Test
    public void testTypeIsRequired() throws Exception {
        exception.expect(MalformedPURLException.class);
        PURL purl = new PURL("EnterpriseLibrary.Common@6.0.1304");
    }

    @Test
    public void testNameIsRequired() throws Exception {
        exception.expect(MalformedPURLException.class);
        PURL purl = new PURL("maven:@1.3.4");
    }

    @Test
    public void testSingleSlashAfterTypeIsNotSignificant() throws Exception {
        exception = ExpectedException.none();
        PURL purl = new PURL("maven:/org.apache.commons/io");
        Assert.assertEquals("maven:org.apache.commons/io", purl.canonicalize());
        Assert.assertEquals("maven", purl.getType());
        Assert.assertEquals("org.apache.commons", purl.getNamespace());
        Assert.assertEquals("io", purl.getName());
        Assert.assertNull(purl.getVersion());
        Assert.assertNull(purl.getQualifiers());
        Assert.assertNull(purl.getSubpath());
    }

    @Test
    public void testDoubleSlashAfterTypeIsNotSignificant() throws Exception {
        exception = ExpectedException.none();
        PURL purl = new PURL("maven://org.apache.commons/io");
        Assert.assertEquals("maven:org.apache.commons/io", purl.canonicalize());
        Assert.assertEquals("maven", purl.getType());
        Assert.assertEquals("org.apache.commons", purl.getNamespace());
        Assert.assertEquals("io", purl.getName());
        Assert.assertNull(purl.getVersion());
        Assert.assertNull(purl.getQualifiers());
        Assert.assertNull(purl.getSubpath());
    }

    @Test
    public void testTripleSlashAfterTypeIsNotSignificant() throws Exception {
        exception = ExpectedException.none();
        PURL purl = new PURL("maven:///org.apache.commons/io");
        Assert.assertEquals("maven:org.apache.commons/io", purl.canonicalize());
        Assert.assertEquals("maven", purl.getType());
        Assert.assertEquals("org.apache.commons", purl.getNamespace());
        Assert.assertEquals("io", purl.getName());
        Assert.assertNull(purl.getVersion());
        Assert.assertNull(purl.getQualifiers());
        Assert.assertNull(purl.getSubpath());
    }

}
