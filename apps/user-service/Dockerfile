FROM openjdk:17-jdk
VOLUME /tmp
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} user-service.jar
ENTRYPOINT ["java", "-jar", "user-service.jar"]