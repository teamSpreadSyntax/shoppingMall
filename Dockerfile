# Step 1: Use an official OpenJDK image with a JDK for building the project
FROM openjdk:17-jdk-slim AS builder

# Set the working directory
WORKDIR /app

# 필요한 빌드 파일과 Gradle Wrapper를 복사
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY src ./src

# Gradle Wrapper로 빌드 수행
RUN ./gradlew build -x test --no-daemon

# Step 2: Use an official OpenJDK runtime image to run the app
FROM openjdk:17-jdk-slim

# Set the working directory in the runtime container
WORKDIR /app

# Copy the JAR file from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# wait-for-it.sh 스크립트를 복사
COPY scripts/wait-for-it.sh /app/wait-for-it.sh

# 권한 설정
RUN chmod +x /app/wait-for-it.sh

# SSL 인증서 복사
COPY elastic-stack-ca.p12 /app/elastic-stack-ca.p12
COPY springboot.p12 /app/springboot.p12

# Expose port 443 for the application
EXPOSE 443

# Run the Spring Boot application after waiting for Kafka and Elasticsearch to be ready
ENTRYPOINT ["/app/wait-for-it.sh", "kafka:9092", "--timeout=120", "--", "/app/wait-for-it.sh", "elasticsearch:9200", "--timeout=240", "--", "java", "-jar", "app.jar"]
