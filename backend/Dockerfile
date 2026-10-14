# Multi-stage Dockerfile for Spring Boot 3.4 & Java 21 Backend
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /workspace

# Copy maven wrapper and pom.xml first to cache dependencies
COPY pom.xml .
COPY src ./src

# Build application
RUN apk add --no-cache maven && \
    mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine AS runner
WORKDIR /app

# Add a dedicated non-root user and group
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy jar from builder stage
COPY --from=builder /workspace/target/*.jar app.jar

# Set file permissions
RUN chown -R appuser:appgroup /app

USER appuser

# Expose standard port
EXPOSE 8080

# Environment variables
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"
ENV SPRING_PROFILES_ACTIVE="prod"

# Health check
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health | grep -q '"status":"UP"' || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
