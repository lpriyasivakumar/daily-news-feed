FROM openjdk:18-jdk-slim
ENV JAVA_OPTS=""
EXPOSE ${COLLECTOR_PORT}
COPY . .
RUN ./gradlew stage
ENTRYPOINT java -Djava.security.egd=file:/dev/./urandom ${JAVA_OPTS} -jar applications/data-collector-server/build/libs/data-collector-server-1.0-SNAPSHOT.jar
