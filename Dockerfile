FROM openjdk:18-jdk-slim
ARG PORT=""
ARG APP=""
COPY . .
RUN ./gradlew stage
EXPOSE ${PORT}
ENTRYPOINT java -Djava.security.egd=file:/dev/./urandom -Dserver.port=${PORT} -jar ${APP}