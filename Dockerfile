#FROM cr.gitlab.switch.ch/hslu/shared/devops/docker-cache/amazoncorretto:17.0.8-alpine
FROM amazoncorretto:17.0.8-alpine
ARG CI_GIT_VERSION
ARG CI_GIT_DATE

# add jar to image
RUN echo "$GIT_VERSION,$GIT_DATE" > version.txt
COPY ./target/service.jar service.jar

# Startup
CMD java ${JAVA_OPTS} -jar ./service.jar
