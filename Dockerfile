# Step 1: Use an official Gradle image to build the project
FROM gradle:8.5-jdk17 AS builder

# Set the working directory
WORKDIR /app

# 빌드에 필요한 파일들 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY src ./src
# Gradle 파일 복사
COPY gradle/gradle-8.5-bin.zip /app/gradle/gradle-8.5-bin.zip

# Firebase 설정 파일 복사
COPY src/main/resources/superb-analog-439512-g8-firebase-adminsdk-l7nbt-2305deb251.json /app/serviceAccountKey.json

# gradle-wrapper.properties의 distributionUrl을 로컬 파일 경로로 변경
RUN sed -i 's|https://services.gradle.org/distributions/gradle-8.5-bin.zip|file:///app/gradle/gradle-8.5-bin.zip|' gradle/wrapper/gradle-wrapper.properties

# Gradle Wrapper를 사용하여 빌드
RUN --mount=type=cache,target=/root/.gradle ./gradlew build -x test --no-daemon

# Step 2: Use an official OpenJDK runtime image to run the app
FROM openjdk:17-jdk-slim

# Set the working directory in the runtime container
WORKDIR /app

# Copy the JAR file from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Copy Firebase config from builder stage
COPY --from=builder /app/serviceAccountKey.json /app/serviceAccountKey.json

# wait-for-it.sh 스크립트를 복사
COPY scripts/wait-for-it.sh /app/wait-for-it.sh

# SSL 인증서 복사
#COPY elastic-truststore.p12 /usr/share/elasticsearch/config/elastic-truststore.p12
#COPY elastic-stack-ca.p12 /usr/share/elasticsearch/config/elastic-stack-ca.p12
COPY www.projectkkk.com.pkcs12 /app/www.projectkkk.com.pkcs12
COPY ca.crt /usr/share/elasticsearch/config/ca.crt

RUN chmod +x /app/wait-for-it.sh
RUN chmod 644 /app/www.projectkkk.com.pkcs12

# 권한 설정
RUN chmod 644 /usr/share/elasticsearch/config/ca.crt
#RUN chmod 600 /usr/share/elasticsearch/config/elastic-truststore.p12
#RUN chmod 600 /usr/share/elasticsearch/config/elastic-stack-ca.p12

# Expose port 443 for the application
EXPOSE 443

# Run the Spring Boot application after waiting for Kafka and Elasticsearch to be ready
ENTRYPOINT ["/app/wait-for-it.sh", "kafka:9092", "--timeout=120", "--", "/app/wait-for-it.sh", "elasticsearch:9200", "--timeout=240", "--", "java", "-Dserver.port=443", "-Dserver.ssl.key-store=/app/www.projectkkk.com.pkcs12", "-Dserver.ssl.key-store-password=Ccenter123456!", "-Dserver.ssl.key-store-type=PKCS12", "-Djavax.net.ssl.trustStore=/usr/share/elasticsearch/config/elastic-truststore.p12", "-Djavax.net.ssl.trustStorePassword=Ccenter123456!", "-Djavax.net.ssl.trustStoreType=PKCS12", "-jar", "app.jar"]
