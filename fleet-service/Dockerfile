FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml .
COPY fleet-service/pom.xml fleet-service/pom.xml
COPY rules-alerts-service/pom.xml rules-alerts-service/pom.xml
RUN mvn -B -q -e -DskipTests dependency:go-offline
COPY fleet-service/src fleet-service/src
RUN mvn -pl fleet-service -am package -DskipTests -B

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /workspace/fleet-service/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]