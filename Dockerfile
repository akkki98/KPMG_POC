## Multi-stage Dockerfile for Spring Boot backend
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
# Pre-fetch dependencies for layer caching
RUN --mount=type=cache,target=/root/.m2 mvn -q -DskipTests dependency:go-offline || true
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -q -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
RUN useradd -r -u 1001 appuser
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENV JAVA_OPTS=""
# Expect secrets via env; do not bake in
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]
