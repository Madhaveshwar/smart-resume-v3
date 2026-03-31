FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

COPY . .

WORKDIR /app/backend
RUN mvn clean install -DskipTests

FROM eclipse-temurin:17
WORKDIR /app

COPY --from=build /app/backend/target/*.jar app.jar

CMD ["java", "-jar", "app.jar"]