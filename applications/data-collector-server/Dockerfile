FROM openjdk:18-jdk-slim
ENV JAVA_OPTS=""
COPY . .
RUN ./gradlew stage
EXPOSE ${COLLECTOR_PORT}
ENTRYPOINT java -Djava.security.egd=file:/dev/./urandom ${JAVA_OPTS} -jar build/libs/data-collector-server-1.0-SNAPSHOT.jar
