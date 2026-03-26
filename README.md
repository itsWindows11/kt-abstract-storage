# kt-abstract-storage

`kt-abstract-storage` is a Kotlin/JVM library that provides abstractions and implementations for:

- file and folder storage interfaces
- in-memory and system-backed storage
- stream adapters and wrappers
- ZIP archive-backed virtual folders/files

## Modules

- `:app` - publishable core library (`kt-abstract-storage-core`)
- `:utils` - utility module (`kt-abstract-storage-utils`)
- `buildSrc` - shared Gradle convention plugin

## Build and test

Use the Gradle wrapper from the repository root:

- `./gradlew build`
- `./gradlew test`
- `./gradlew :app:test`
- `./gradlew clean`

## Publishing

The shared convention configures:

- reproducible jars
- `sourcesJar` and `javadocJar`
- Maven publications
- optional signing
- optional publish targets for Maven Central (OSSRH) and GitHub Packages

Coordinates and POM metadata are controlled through `gradle.properties`.

### Local publish

- `./gradlew publishToMavenLocal`

### Publish to configured remotes

- `./gradlew publish`

If remote credentials/signing keys are missing, only local publication is expected to work.

## Upstream parity tests

The test suite includes Kotlin/JUnit ports derived from `OwlCore.Storage.Tests` (excluding HTTP-specific tests).
