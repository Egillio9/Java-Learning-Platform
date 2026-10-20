# 1) Build the React frontend into Spring Boot's static folder
FROM node:20-alpine AS web
WORKDIR /app/frontend
COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci --no-audit --no-fund
COPY frontend/ ./
RUN npm run build

# 2) Build the Spring Boot jar (frontend bundled inside)
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
COPY --from=web /app/src/main/resources/static ./src/main/resources/static
RUN mvn -B -q package -DskipTests

# 3) Run on a JDK (not a JRE): the code judge compiles submissions with javax.tools
FROM eclipse-temurin:17-jdk
WORKDIR /app
# Unprivileged user: submitted code runs as this user, never as root
RUN useradd --system --create-home judge && chown judge /app
COPY --from=build /app/target/*.jar app.jar
USER judge
EXPOSE 8080
ENTRYPOINT ["java", "-XX:+UseSerialGC", "-Xss512k", "-XX:MaxRAMPercentage=45", "-jar", "app.jar"]
