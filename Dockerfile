FROM openjdk:18-jdk-slim
ARG PORT
ARG APP
ENV PRT=$PORT
ENV APP_NAME=$APP
COPY . .
RUN ./gradlew stage
EXPOSE ${PRT}
ENTRYPOINT java -Djava.security.egd=file:/dev/./urandom -Dserver.port=${PRT} -jar ${APP_NAME}