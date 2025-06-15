FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

RUN mkdir temp && cp /app/temp/target/*.jar /app/app.jar
COPY --from=build /app /app

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
