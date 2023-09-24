# Daily News feed application

Created from the [application continuum](https://www.appcontinuum.io/) style example using Kotlin and Ktor
that includes a single web application with two background workers.

* Basic web application
* Data analyzer
* Data collector

### Technology stack

This codebase is written in a language called [Kotlin](https://kotlinlang.org) that is able to run on the JVM with full
Java compatibility.
It uses the [Ktor](https://ktor.io) web framework, and runs on the [Netty](https://netty.io/) web server.
HTML templates are written using [Freemarker](https://freemarker.apache.org).
The codebase is tested with [JUnit](https://junit.org/) and uses [Gradle](https://gradle.org) to build a jarfile.
The [pack cli](https://buildpacks.io/docs/tools/pack/) is used to build a [Docker](https://www.docker.com/) container which is deployed to
[Google Cloud](https://cloud.google.com/) on Google's Cloud Platform.

## Getting Started

## Development

1.  Build a Java Archive (jar) file.
    ```bash
    ./gradlew clean build
    ```

1.  Configure the port that each server runs on.
    ```bash
    export PORT=8881
    ```

Run the servers locally using the below examples.

### Web application

```bash
./gradlew applications:basic-server:run
```

### Data collector

```bash
./gradlew applications:data-collector-server:run
```

### Data analyzer

```bash
./gradlew applications:data-analyzer-server:run
```

## Production

Building a Docker container and running with Docker.

## Buildpacks

1.  Install the [pack](https://buildpacks.io/docs/tools/pack/) CLI.
    ```bash
    brew install buildpacks/tap/pack
    ```

1.  Build using pack.
    ```bash
    pack build kotlin-ktor-starter --builder heroku/buildpacks:20
    ```

1.  Run with docker.
    ```bash
    docker run  -e "PORT=8882" -e "APP=applications/basic-server/build/libs/basic-server-1.0-SNAPSHOT.jar" kotlin-ktor-starter
    ```

That's a wrap for now.
