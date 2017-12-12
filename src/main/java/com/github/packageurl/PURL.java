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

/**
 * purl stands for package URL.
 *
 * A purl is a URL composed of six components:
 *
 * type:namespace/name@version?qualifiers#subpath
 *
 * Components are separated by a specific character for unambiguous parsing.
 * A purl must NOT contain a URL Authority i.e. there is no support for username,
 * password, host and port components. A namespace segment may sometimes look
 * like a host but its interpretation is specific to a type.
 *
 * SPEC: https://github.com/package-url/purl-spec
 *
 * @author Steve Springett
 * @since 1.0.0
 */
public final class PURL implements java.io.Serializable {

    private static final long serialVersionUID = 3243226021636427586L;

    public PURL(String purl) throws MalformedPURLException {
        parse(purl);
    }

    public PURL(String type, String namespace, String name, String version, String qualifiers, String subpath)
            throws MalformedPURLException {
        validateType(type);
        validateNamespace(namespace);
        validateName(name);
        validateVersion(version);
        validateQualifiers(qualifiers);
        validateSubpath(subpath);
    }

    /**
     * The package "type" or package "protocol" such as maven, npm, nuget, gem, pypi, etc.
     * Required.
     */
    private String type;

    /**
     * The name prefix such as a Maven groupid, a Docker image owner, a GitHub user or organization.
     * Optional and type-specific.
     */
    private String namespace;

    /**
     * The name of the package.
     * Required.
     */
    private String name;

    /**
     * The version of the package.
     * Optional.
     */
    private String version;

    /**
     * Extra qualifying data for a package such as an OS, architecture, a distro, etc.
     * Optional and type-specific.
     */
    private String qualifiers;

    /**
     * Extra subpath within a package, relative to the package root.
     * Optional.
     */
    private String subpath;


    /**
     * Returns the package "type" or package "protocol" such as maven, npm, nuget, gem, pypi, etc.
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the package "type" or package "protocol" such as maven, npm, nuget, gem, pypi, etc.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Fluent: Sets the package "type" or package "protocol" such as maven, npm, nuget, gem, pypi, etc.
     */
    public PURL type(String type) {
        this.type = type;
        return this;
    }

    /**
     * Returns the name prefix such as a Maven groupid, a Docker image owner, a GitHub user or organization.
     * @return the namespace
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * Sets the name prefix such as a Maven groupid, a Docker image owner, a GitHub user or organization.
     * @param namespace the namespace
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    /**
     * Fluent: Sets the name prefix such as a Maven groupid, a Docker image owner, a GitHub user or organization.
     * @param namespace the namespace
     * @return the namespace
     */
    public PURL namespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    /**
     * Returns the name of the package.
     * @return the name of the package
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the package.
     * @param name the name of the package
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Fluent: Sets the name of the package.
     * @param name the name of the package
     */
    public PURL name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Returns the version of the package.
     * @return the version of the package
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the version of the package.
     * @param version the version of the package.
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Fluent: Sets the version of the package.
     * @param version the version of the package.
     */
    public PURL version(String version) {
        this.version = version;
        return this;
    }

    /**
     * Returns extra qualifying data for a package such as an OS, architecture, a distro, etc.
     * @return qualifiers
     */
    public String getQualifiers() {
        return qualifiers;
    }

    /**
     * Sets extra qualifying data for a package such as an OS, architecture, a distro, etc.
     * @param qualifiers the qualifiers
     */
    public void setQualifiers(String qualifiers) {
        this.qualifiers = qualifiers;
    }

    /**
     * Fluent: Sets extra qualifying data for a package such as an OS, architecture, a distro, etc.
     * @param qualifiers the qualifiers
     */
    public PURL qualifiers(String qualifiers) {
        this.qualifiers = qualifiers;
        return this;
    }

    /**
     * Returns extra subpath within a package, relative to the package root.
     * @return the subpath
     */
    public String getSubpath() {
        return subpath;
    }

    /**
     * Sets extra subpath within a package, relative to the package root.
     * @param subpath the subpath
     */
    public void setSubpath(String subpath) {
        this.subpath = subpath;
    }

    /**
     * Fluent: Sets extra subpath within a package, relative to the package root.
     * @param subpath the subpath
     */
    public PURL subpath(String subpath) {
        this.subpath = subpath;
        return this;
    }

    private void parse(String purl) throws MalformedPURLException {
        //todo parse the purl and validate each of the fields to the spec
    }

    private void validateType(String type) throws MalformedPURLException {
        //todo
    }

    private void validateNamespace(String namespace) throws MalformedPURLException {
        //todo
    }

    private void validateName(String name) throws MalformedPURLException {
        //todo
    }

    private void validateVersion(String version) throws MalformedPURLException {
        //todo
    }

    private void validateQualifiers(String qualifiers) throws MalformedPURLException {
        //todo
    }

    private void validateSubpath(String subpath) throws MalformedPURLException {
        //todo
    }

    public String canonicalize() {
        return null; //todo
    }

    @Override
    public boolean equals(Object o) {
        return false; //todo
    }

    @Override
    public String toString() {
        return null; //todo
    }
}
