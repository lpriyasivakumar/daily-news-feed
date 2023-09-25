FROM openjdk:18-jdk-slim
ENV PORT=""
ENV APP=""
COPY . .
RUN ./gradlew stage
EXPOSE ${PORT}
ENTRYPOINT java -Djava.security.egd=file:/dev/./urandom -Dserver.port=${PORT} -jar ${APP}