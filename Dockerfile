FROM openjdk:18-jdk-slim
ARG PORT
ARG APP
ARG BUILD_PATH
ENV PORT=$PORT
ENV BUILD_PATH=$BUILD_PATH
ENV APP=$APP
RUN mkdir source
COPY . source
RUN cd source \
    && ./gradlew clean build -x test \
    && ./gradlew shadowJar \
    && cp $BUILD_PATH .. \
    && cd .. \
    && rm -rf source
EXPOSE $PORT
ENTRYPOINT ["java", "-jar", "$APP"]