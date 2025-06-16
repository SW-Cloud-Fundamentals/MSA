FROM openjdk:17-jdk
VOLUME /tmp
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} discovery.jar
ENTRYPOINT ["java", "-jar", "discovery.jar"]