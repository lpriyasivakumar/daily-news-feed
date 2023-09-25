FROM openjdk:18-jdk-slim
ENV JAVA_OPTS=""
EXPOSE ${WEB_APP_PORT}
COPY . .
RUN ./gradlew stage
ENTRYPOINT java -Djava.security.egd=file:/dev/./urandom ${JAVA_OPTS} -jar applications/basic-server/build/libs/basic-server-1.0-SNAPSHOT.jar
