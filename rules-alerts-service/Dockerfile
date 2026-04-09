FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY rules-alerts-service/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]