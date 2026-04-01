# -------- BUILD STAGE --------
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /build

COPY backend/pom.xml .
RUN mvn -q -e -DskipTests dependency:go-offline

COPY backend/src ./src

RUN mvn clean package -DskipTests

# -------- RUN STAGE --------
FROM eclipse-temurin:17

WORKDIR /app

COPY --from=build /build/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]