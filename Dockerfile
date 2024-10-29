# Step 1: Use an official Gradle image to build the project
FROM gradle:8.5-jdk17 AS builder

# Set the working directory
WORKDIR /app

# 필요한 빌드 파일과 Gradle Wrapper 복사
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle
COPY src ./src

# Build with no daemon
RUN ./gradlew build -x test --no-daemon

# Step 2: Use an official OpenJDK runtime image to run the app
FROM openjdk:17-jdk-slim

# Set the working directory in the runtime container
WORKDIR /app

# Copy the JAR file from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# wait-for-it.sh 스크립트를 복사
COPY scripts/wait-for-it.sh /app/wait-for-it.sh

# 권한 설정 (스크립트를 실행 가능하게 만듭니다)
RUN chmod +x /app/wait-for-it.sh

# 인증서 파일 복사
COPY src/main/resources/keystore.jks /app/keystore.jks
COPY elasticsearch.crt /app/elasticsearch.crt
COPY kibana.crt /app/kibana.crt

# Expose port 8080 for the application
EXPOSE 443

# Set environment variables
ENV SPRING_DATASOURCE_URL=jdbc:mysql://zigzag-database.cnkq8ww86ffm.ap-northeast-2.rds.amazonaws.com:3306/zigzagDB \
    SPRING_DATASOURCE_USERNAME=Kang \
    SPRING_DATASOURCE_PASSWORD=alstj121! \
    SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092

# Run the Spring Boot application after waiting for Kafka and Elasticsearch to be ready
ENTRYPOINT ["/app/wait-for-it.sh", "kafka:9092", "--timeout=120", "--", "/app/wait-for-it.sh", "elasticsearch:9200", "--timeout=240", "--", "java", "-jar", "app.jar"]
