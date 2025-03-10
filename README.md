[![Build Status](https://github.com/package-url/packageurl-java/workflows/Maven%20CI/badge.svg)](https://github.com/package-url/packageurl-java/actions?workflow=Maven+CI)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.package-url/packageurl-java/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.package-url/packageurl-java)
[![License][license-image]][license-url]

# Package URL (purl) for Java

This project implements a purl parser and class for Java.

## Requirements

- The library has a **runtime** requirement of JRE 8 or newer.
- To **build** the library from source, you need JDK 17 or newer.

## Compiling

```bash
mvn clean install
````

## Maven Usage
Package URL is available on the Maven Central Repository. These can be used without having to compile 
the project yourself.

```xml
<dependencies>
    <dependency>
        <groupId>com.github.package-url</groupId>
        <artifactId>packageurl-java</artifactId>
        <version>1.5.0</version>
    </dependency>
</dependencies>
```

## Usage
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

Validates a String field in a POJO (Bean Validation):
```java
@PackageURL
private String purl;
```

License
-------------------

Permission to modify and redistribute is granted under the terms of the 
[MIT License](https://github.com/package-url/packageurl-java/blob/master/LICENSE)

[license-image]: https://img.shields.io/badge/license-mit%20license-brightgreen.svg
[license-url]: https://github.com/package-url/packageurl-java/blob/master/LICENSE
