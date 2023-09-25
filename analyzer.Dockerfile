FROM openjdk:18-jdk-slim
ENV JAVA_OPTS=""
EXPOSE ${ANALYZER_PORT}
COPY . .
RUN ./gradlew stage
ENTRYPOINT java -Djava.security.egd=file:/dev/./urandom ${JAVA_OPTS} -jar applications/data-analyzer-server/build/libs/data-analyzer-server-1.0-SNAPSHOT.jar
