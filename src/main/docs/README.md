# ${project.name}

${project.description}

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
    <groupId>${project.groupId}</groupId>
    <artifactId>${project.artifactId}</artifactId>
    <version>${project.version}</version>
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
