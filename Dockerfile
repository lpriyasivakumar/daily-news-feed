FROM openjdk:18-jdk-slim
ARG PORT
ARG APP
ENV PORT=$PORT
ENV APP=$APP
ENV PROPS="-Dserver.port=${PORT} -Djava.security.egd=file:/dev/./urandom"
COPY . .
RUN ./gradlew stage
EXPOSE $PORT
ENTRYPOINT java -jar ${PROPS} ${APP}