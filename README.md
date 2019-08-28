[![Build Status](https://travis-ci.com/package-url/packageurl-java.svg?branch=master)](https://travis-ci.com/package-url/packageurl-java)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.package-url/packageurl-java/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.package-url/packageurl-java)
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
Package URL is available on the Maven Central Repository. These can be used without having to compile 
the project yourself.

```xml
<dependencies>
    <dependency>
        <groupId>com.github.package-url</groupId>
        <artifactId>packageurl-java</artifactId>
        <version>1.1.1</version>
    </dependency>
</dependencies>
```

Usage
-------------------

Creates a new PackageURL object from a purl string:
```java
PackageURL purl = new PackageURL(purlString);
````

Creates a new PackageURL object from parameters:
```java
PackageURL purl = new PackageURL(type, namespace, name, version, qualifiers, subpath);
````

Creates a new PackageURL object using the builder pattern:
```java
PackageURL purl = PackageURLBuilder.aPackageURL()
    .withType("type")
    .withNamespace("namespace")
    .withName("name")
    .withVersion("version")
    .withQualifier("key","value")
    .withSubpath("subpath")
    .build();
```

License
-------------------

Permission to modify and redistribute is granted under the terms of the 
[MIT License](https://github.com/package-url/packageurl-java/blob/master/LICENSE)

[license-image]: https://img.shields.io/badge/license-mit%20license-brightgreen.svg
[license-url]: https://github.com/package-url/packageurl-java/blob/master/LICENSE
