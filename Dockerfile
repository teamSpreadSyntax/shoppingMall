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
COPY src/main/resources/superb-analog-439512-g8-e7979f6854cd.json /usr/share/springboot/superb-analog-439512-g8-e7979f6854cd.json

# gradle-wrapper.properties의 distributionUrl을 로컬 파일 경로로 변경
RUN sed -i 's|https://services.gradle.org/distributions/gradle-8.5-bin.zip|file:///app/gradle/gradle-8.5-bin.zip|' gradle/wrapper/gradle-wrapper.properties

# Gradle Wrapper를 사용하여 빌드 (항상 테스트를 제외)
RUN --mount=type=cache,target=/root/.gradle ./gradlew build -x test --no-daemon

# Step 2: Use an official OpenJDK runtime image to run the app
FROM openjdk:17-jdk-slim

# Set the working directory in the runtime container
WORKDIR /app

# Copy the JAR file from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Copy Firebase config from builder stage
COPY --from=builder /app/serviceAccountKey.json /app/serviceAccountKey.json
COPY --from=builder /usr/share/springboot/superb-analog-439512-g8-e7979f6854cd.json /usr/share/springboot/superb-analog-439512-g8-e7979f6854cd.json

# Google 인증서 추가 (TrustStore에 추가)
COPY google.crt /tmp/google.crt
RUN keytool -importcert -file /tmp/google.crt -alias google-cert \
    -keystore /usr/lib/jvm/java-17-openjdk-amd64/lib/security/cacerts \
    -storepass changeit -noprompt \
    && rm /tmp/google.crt

# wait-for-it.sh 스크립트 복사
COPY scripts/wait-for-it.sh /app/wait-for-it.sh
RUN chmod +x /app/wait-for-it.sh

# SSL 인증서 복사
COPY www.projectkkk.pkcs12 /app/www.projectkkk.pkcs12

# 권한 설정
RUN chmod 600 /app/www.projectkkk.pkcs12

# Expose port 443 for the application
EXPOSE 443

# Run the Spring Boot application after waiting for Kafka and Elasticsearch to be ready
ENTRYPOINT ["/app/wait-for-it.sh", "kafka:9092", "--timeout=120", "--", "/app/wait-for-it.sh", "elasticsearch:9200", "--timeout=240", "--", "java", "-jar", "app.jar"]
CMD [
    "-Dserver.port=443",
    "-Dserver.ssl.key-store=/app/www.projectkkk.pkcs12",
    "-Dserver.ssl.key-store-password=Ccenter123456!",
    "-Dserver.ssl.key-store-type=PKCS12",
    "-Djavax.net.ssl.trustStore=/usr/lib/jvm/java-17-openjdk-amd64/lib/security/cacerts",
    "-Djavax.net.ssl.trustStorePassword=changeit",
    "-Djavax.net.ssl.trustStoreType=PKCS12",
    "-DGOOGLE_APPLICATION_CREDENTIALS=/usr/share/springboot/superb-analog-439512-g8-e7979f6854cd.json"
]
