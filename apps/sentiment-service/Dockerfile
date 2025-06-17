FROM openjdk:17-jdk
VOLUME /tmp
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} sentiment-service.jar
ENTRYPOINT ["java", "-jar", "sentiment-service.jar"]