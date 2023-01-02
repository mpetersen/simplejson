# SimpleJSON

A fast and simple JSON parser written in Java.

## Usage

The `SimpleJson` class can be instantiated once and then reused.

```java
final SimpleJson json = new SimpleJson();

...

data = json.readValue(input);
```

## Installation

Add the following dependency:

```
<dependency>
    <groupId>de.moritzpetersen</groupId>
    <artifactId>simplejson</artifactId>
    <version>1.0.0</version>
</dependency>
```

Add the following repository:

```
<repositories>
    <repository>
        <id>moritz-petersen-maven-repo-releases</id>
        <url>https://moritzpetersen.de/maven/releases</url>
    </repository>
</repositories>
```
