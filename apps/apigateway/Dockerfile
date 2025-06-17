FROM openjdk:17-jdk
VOLUME /tmp
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} apigateway.jar
ENTRYPOINT ["java", "-jar", "apigateway.jar"]