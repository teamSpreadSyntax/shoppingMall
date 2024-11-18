# Step 1: Use an official Gradle image to build the project
FROM gradle:8.5-jdk17 AS builder

# Set the working directory
WORKDIR /app

# Copy build-related files
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY src ./src

# Gradle distribution file copy (if needed for local builds)
COPY gradle/gradle-8.5-bin.zip /app/gradle/gradle-8.5-bin.zip

# Firebase configuration file copy
COPY src/main/resources/superb-analog-439512-g8-firebase-adminsdk-l7nbt-2305deb251.json /app/serviceAccountKey.json

# Update gradle-wrapper.properties to use local Gradle zip (optional step for local builds)
RUN sed -i 's|https://services.gradle.org/distributions/gradle-8.5-bin.zip|file:///app/gradle/gradle-8.5-bin.zip|' gradle/wrapper/gradle-wrapper.properties

# Build the project using Gradle
RUN --mount=type=cache,target=/root/.gradle ./gradlew build -x test --no-daemon

# Step 2: Use an official OpenJDK runtime image to run the app
FROM openjdk:17-jdk-slim

# Set the working directory in the runtime container
WORKDIR /app

# Copy the JAR file from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Copy Firebase configuration file from the builder stage
COPY --from=builder /app/serviceAccountKey.json /app/serviceAccountKey.json

# Add Let’s Encrypt SSL certificates
COPY projectkkk.jks /app/projectkkk.jks

# wait-for-it.sh script for dependency checks
COPY scripts/wait-for-it.sh /app/wait-for-it.sh

# Add execution permissions to wait-for-it.sh
RUN chmod +x /app/wait-for-it.sh

# Expose port 443 for HTTPS
EXPOSE 443

# Run the Spring Boot application after waiting for Kafka and Elasticsearch to be ready
ENTRYPOINT ["/app/wait-for-it.sh", "kafka:9092", "--timeout=120", "--", "/app/wait-for-it.sh", "elasticsearch:9200", "--timeout=240", "--", "java", "-Dserver.ssl.key-store=/app/projectkkk.jks", "-Dserver.ssl.key-store-password=${KEYSTORE_PASSWORD}", "-Dserver.ssl.key-store-type=JKS", "-jar", "app.jar"]
