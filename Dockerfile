# Stage 1: Build the JAR file
FROM maven:3.9.7-amazoncorretto-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
COPY opentelemetry-javaagent.jar ./opentelemetry-javaagent.jar
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM openjdk:21
VOLUME /tmp
EXPOSE 8080
COPY --from=build /app/target/*.jar app.jar
COPY --from=build /app/opentelemetry-javaagent.jar opentelemetry-javaagent.jar
ENV JAVA_TOOL_OPTIONS=-javaagent:opentelemetry-javaagent.jar
ENV OTEL_EXPORTER_OTLP_ENDPOINT=http://otel-collector:4318
ENTRYPOINT ["java", "-jar", "/app.jar"]

#curl -X GET http://localhost:8081/person/callOther/2