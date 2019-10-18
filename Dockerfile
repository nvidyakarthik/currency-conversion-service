FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD target/*.jar app.jar
EXPOSE 8100
ENTRYPOINT ["sh","-c","java -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=dev -jar /app.jar"]