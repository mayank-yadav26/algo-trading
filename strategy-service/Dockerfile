FROM maven:3.9-eclipse-temurin-17-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY common-library common-library
COPY market-data-service market-data-service
COPY strategy-service strategy-service
COPY virtual-broker-service virtual-broker-service
COPY dashboard-service dashboard-service
RUN mvn clean package -DskipTests -pl strategy-service -am

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/strategy-service/target/*.jar app.jar
EXPOSE 8082 5006
ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5006", "-jar", "app.jar"]
