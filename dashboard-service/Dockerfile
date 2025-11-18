FROM maven:3.9-eclipse-temurin-17-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY common-library common-library
COPY market-data-service market-data-service
COPY strategy-service strategy-service
COPY virtual-broker-service virtual-broker-service
COPY dashboard-service dashboard-service
RUN mvn clean package -DskipTests -pl dashboard-service -am

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/dashboard-service/target/*.jar app.jar
EXPOSE 8080 5008
ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5008", "-jar", "app.jar"]
