# Receta para construir y ejecutar la app en la nube (Render, Railway, etc.)

# --- Etapa 1: compilar el proyecto con Maven ---
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q clean package -DskipTests

# --- Etapa 2: ejecutar el .jar con un Java liviano ---
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
