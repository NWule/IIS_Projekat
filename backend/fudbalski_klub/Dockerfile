FROM python:3.11-slim AS schema-extractor
WORKDIR /python-app

COPY fk_ai_service/requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt
COPY fk_ai_service/ .

RUN GEMINI_API_KEY=dummy python -c "import json; from main import app; print(json.dumps(app.openapi()))" > openapi.json

FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

COPY fudbalski_klub/pom.xml .

COPY --from=schema-extractor /python-app/openapi.json ./src/main/resources/openapi.json

RUN mvn dependency:go-offline -B

COPY fudbalski_klub/src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]