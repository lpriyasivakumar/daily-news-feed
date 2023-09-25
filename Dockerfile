FROM openjdk:18-jdk-slim
ARG PORT
ARG APP
ENV PORT=$PORT
ENV APP=$APP
ENV PROPS="-Dserver.port=${PORT}"
COPY . /app
RUN cd /app
RUN ./gradlew clean build
EXPOSE $PORT
ENTRYPOINT java -jar $APP $PROPS