FROM gradle:7.5.1-jdk17-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM openjdk:18-jdk-slim
ENV JAVA_OPTS=""
EXPOSE ${ANALYZER_PORT}
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/data-analyzer.jar
ENTRYPOINT java -Djava.security.egd=file:/dev/./urandom ${JAVA_OPTS} -jar /app/data-analyzer.jar
