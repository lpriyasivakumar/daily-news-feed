# Daily News feed application

Created from the [application continuum](https://www.appcontinuum.io/) style example using Kotlin and Ktor
that includes a single web application with two background workers.

* Basic web application
* Data analyzer
* Data collector

### Technology stack

This application fetches news data from an api and saves it to a database. 
The new articles are run through an analyzer and then displayed in the front-end application.
It uses the [Ktor](https://ktor.io) web framework, and runs on the [Netty](https://netty.io/) web server.
HTML templates are written using [Freemarker](https://freemarker.apache.org).
The codebase is tested with [JUnit](https://junit.org/) and uses [Gradle](https://gradle.org) to build a jarfile.
The code is deployed on heroku

## Getting Started

## Setup steps



1. Run docker containers in a terminal window to get database server started
   ```bash
   docker-compose up 
   ```

1. Run migrations from the root of the project with the docker containers running.
Note: verify that the gradlew script has execute privileges if you run into permission errors.
   ```bash
   ./gradlew devMigrate testMigrate
   ```

1. Build a Java Archive (jar) file. Docker containers have to be running for the tests to pass and build to complete.
   ```bash
   ./gradlew clean build
   ```


Run the servers locally using the below examples.

### Web application runs on port 8180

```bash
./gradlew applications:basic-server:run
```

### Data collector runs on port 8761

```bash
./gradlew applications:data-collector-server:run
```

### Data analyzer runs on port 8081

```bash
./gradlew applications:data-analyzer-server:run
```

## Production

The app is deployed using the github actions workflow to heroku cloud using docker

## Monitoring

Metrics and health-check endpoints are provided for observability.
A prometheus configuration file 'prometheus.yml' is provided at the root of the project to test with a prometheus server.

## Notes
The applications take a few minutes to fetch, analyze and save the news articles to the local database. 
So it takes a few minutes to see the news feed on the web page.
The webpage to view the results is at ‘http://localhost:8180/news-feed’
The filter buttons reload the page with the sentiment filters applied.

