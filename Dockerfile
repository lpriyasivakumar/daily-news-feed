FROM openjdk:18-jdk-slim
ENV PORT=8888
ENV JAVA_OPTS=""
ENV APP=""
COPY . .
RUN ./gradlew stage
EXPOSE ${PORT}
ENTRYPOINT java -Djava.security.egd=file:/dev/./urandom -Dserver.port=${PORT} ${JAVA_OPTS} -jar ${APP}
