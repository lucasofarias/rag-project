# syntax=docker/dockerfile:1
# ==============================================================================
# Build Stage (JDK Alpine)
# ==============================================================================
FROM eclipse-temurin:25-jdk-alpine AS builder
WORKDIR /build

# Copy Maven wrapper and POM first to leverage layer caching
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x ./mvnw

# Pre-fetch dependencies with cache mount to accelerate builds
RUN --mount=type=cache,target=/root/.m2 ./mvnw dependency:go-offline -B

# Copy application source code and compile
COPY src/ src/
RUN --mount=type=cache,target=/root/.m2 ./mvnw clean package -DskipTests -B

# Extract Spring Boot layers using modern tools mode
RUN java -Djarmode=tools -jar target/*.jar extract --layers --destination /build/extracted --application-filename app.jar

# ==============================================================================
# Production Runtime Stage (Minimal JRE Alpine - ~76MB base image)
# ==============================================================================
FROM eclipse-temurin:25-jre-alpine AS runtime

WORKDIR /app

# Run as non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring

# Copy extracted layers in order of change frequency (least to most frequently changed)
COPY --from=builder --chown=spring:spring /build/extracted/dependencies/ ./
COPY --from=builder --chown=spring:spring /build/extracted/spring-boot-loader/ ./
COPY --from=builder --chown=spring:spring /build/extracted/snapshot-dependencies/ ./
COPY --from=builder --chown=spring:spring /build/extracted/application/ ./

USER spring:spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
