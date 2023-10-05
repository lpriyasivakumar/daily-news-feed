FROM openjdk:18-jdk-slim
ARG PORT
ARG APP
ENV PORT=$PORT
ENV APP=$APP
COPY . .
RUN ./gradlew clean build -x test
RUN ./gradlew shadowJar
EXPOSE $PORT
ENTRYPOINT ["java", "-jar", "$APP"]