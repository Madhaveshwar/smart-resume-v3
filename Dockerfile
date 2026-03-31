FROM maven:3.8.5-openjdk-17

WORKDIR /app

COPY backend /app

RUN mvn clean install

CMD ["java", "-jar", "target/*.jar"]