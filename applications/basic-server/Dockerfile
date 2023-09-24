FROM openjdk:18-jdk-slim
ENV JAVA_OPTS=""
COPY . .
RUN ./gradlew stage
EXPOSE ${WEB_APP_PORT}
ENTRYPOINT java -Djava.security.egd=file:/dev/./urandom ${JAVA_OPTS} -jar build/libs/basic-server-1.0-SNAPSHOT.jar
