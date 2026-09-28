# Etapa 1: Compilación (Build) con Maven y Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# 1. Copiar el pom.xml primero para aprovechar el caché de Docker
COPY pom.xml .
# Descargar dependencias (si el pom no cambia, Docker no las vuelve a descargar)
RUN mvn dependency:resolve -B

# 2. Copiar el código fuente y empaquetar la aplicación
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Imagen final ligera de ejecución (Runtime)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Crear un usuario no-root por seguridad
RUN addgroup -S spring && adduser -S spring -G spring

# Crear la carpeta de uploads y otorgarle permisos al usuario spring
RUN mkdir -p /app/uploads && chown -R spring:spring /app/uploads

# Copiar el archivo .jar generado en la etapa anterior
COPY --from=build /app/target/*.jar app.jar
RUN chown spring:spring app.jar

# Cambiar al usuario seguro
USER spring:spring

# Exponer el puerto por defecto de Spring Boot
EXPOSE 8080

# Declarar volumen para que las imágenes subidas no se borren al apagar el contenedor
VOLUME /app/uploads

# Parámetros óptimos de memoria para contenedores Java
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]