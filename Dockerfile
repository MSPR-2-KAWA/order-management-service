# Utilisez une image de base Java 11
FROM eclipse-temurin:21
WORKDIR /app
COPY target/order-service.jar app.jar
CMD ["java", "-jar", "app.jar"]
