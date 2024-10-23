# Step 1: Use an official Gradle image to build the project
FROM gradle:8.5-jdk17 AS builder

# Set the working directory
WORKDIR /app

# 필요한 빌드 파일만 복사
COPY build.gradle settings.gradle ./
COPY src ./src

# Build with no daemon
RUN gradle build -x test --no-daemon

# Step 2: Use an official OpenJDK runtime image to run the app
FROM openjdk:17-jdk-slim

# Set the working directory in the runtime container
WORKDIR /app

# Copy the JAR file from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Copy the PKCS12 keystore instead of JKS
COPY /etc/letsencrypt/live/www.projectkkk.com/keystore.p12 /app/keystore.p12

# Expose port 443 for HTTPS (instead of 8080)
EXPOSE 443

# Set environment variables for Kafka, MySQL, and other Spring properties
ENV SPRING_DATASOURCE_URL=jdbc:mysql://zigzag-database.cnkq8ww86ffm.ap-northeast-2.rds.amazonaws.com:3306/zigzagDB \
    SPRING_DATASOURCE_USERNAME=Kang \
    SPRING_DATASOURCE_PASSWORD=alstj121! \
    SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092 \
    SERVER_SSL_KEY_STORE=/app/keystore.p12 \
    SERVER_SSL_KEY_STORE_PASSWORD=changeit \
    SERVER_SSL_KEY_STORE_TYPE=PKCS12 \
    SERVER_SSL_KEY_ALIAS=tomcat

# Run the Spring Boot application with the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
