FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY build/libs/*0.2.jar app.jar
ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5013", "-jar", "app.jar"]
