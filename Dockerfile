# Stage 1: Build the application
FROM maven:3.9.7-eclipse-temurin-22 AS maven_build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn package -DskipTests

# Stage 2: Run the application
FROM amazoncorretto:22

EXPOSE 8080

COPY --from=maven_build /app/target/*.jar /app/application.jar

CMD ["java", "-jar", "/app/application.jar"]
