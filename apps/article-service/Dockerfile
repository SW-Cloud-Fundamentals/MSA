FROM openjdk:17-jdk
VOLUME /tmp
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} article-service.jar
ENTRYPOINT ["java", "-jar", "article-service.jar"]