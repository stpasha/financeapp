FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY cash-service/build/libs/*0.1.jar app.jar
ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5009", "-jar", "app.jar"]
