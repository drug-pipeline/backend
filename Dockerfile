# ---- build stage ----
FROM gradle:8.10.2-jdk21-alpine AS build
WORKDIR /app
COPY . .
RUN gradle clean bootJar --no-daemon

# ---- run stage ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
ENV PORT=8080
EXPOSE 8080
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java","-XX:+UseZGC","-XX:MaxRAMPercentage=75.0","-jar","/app/app.jar"]
