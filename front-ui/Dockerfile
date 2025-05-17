FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY front-ui/build/libs/*0.1.jar app.jar
ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "app.jar"]
