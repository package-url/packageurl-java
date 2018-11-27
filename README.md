[![Build Status](https://travis-ci.org/package-url/packageurl-java.svg?branch=master)](https://travis-ci.org/package-url/packageurl-java)
[![License][license-image]][license-url]

Package URL (purl) for Java
=========

This project implements a purl parser and class for Java.

Compiling
-------------------

```bash
mvn clean install
````

Maven Usage
-------------------
Package URL for Java is currently pre-release software but snapshot builds can be used and 
are available on the Maven Central Repository. These can be used without having to compile 
the project yourself.

```xml
<dependencies>
    <dependency>
        <groupId>com.github.package-url</groupId>
        <artifactId>packageurl-java</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

Usage
-------------------

Creates a new PURL object from a string:
```java
PackageURL purl = new PackageURL(purlString);
````

Creates a new PURL object from purl parameters:
```java
PackageURL purl = new PackageURL(type, namespace, name, version, qualifiers, subpath);
````

License
-------------------

Permission to modify and redistribute is granted under the terms of the 
[MIT License](https://github.com/package-url/packageurl-java/blob/master/LICENSE)

[license-image]: https://img.shields.io/badge/license-mit%20license-brightgreen.svg
[license-url]: https://github.com/package-url/packageurl-java/blob/master/LICENSE
