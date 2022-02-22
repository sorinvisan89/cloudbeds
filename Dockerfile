FROM maven:3.6.0-jdk-11-slim AS builder
RUN mkdir -p /app/cloudbeds-app

WORKDIR /app/cloudbeds-app
ADD . /app/cloudbeds-app

RUN mvn clean install -DskipTests
RUN ls -la /app/cloudbeds-app/target/

FROM adoptopenjdk/openjdk11:jre-11.0.3_7-alpine
RUN mkdir -p /app
COPY --from=builder /app/cloudbeds-app/target/*.jar /app/cloudbeds.jar
WORKDIR /app

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/cloudbeds.jar"]