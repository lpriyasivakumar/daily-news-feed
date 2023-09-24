FROM openjdk:18-jdk-slim
ENV JAVA_OPTS=""
COPY build/libs/data-analyzer-server-1.0-SNAPSHOT.jar .
RUN ./gradlew stage
EXPOSE ${ANALYZER_PORT}
ENTRYPOINT java -Djava.security.egd=file:/dev/./urandom ${JAVA_OPTS} -jar data-analyzer-server-1.0-SNAPSHOT.jar
