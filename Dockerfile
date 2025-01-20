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
COPY src/main/resources/superb-analog-439512-g8-e7979f6854cd.json /usr/share/springboot/

# 파일 권한 설정
RUN chown root:root /usr/share/springboot/superb-analog-439512-g8-e7979f6854cd.json
RUN chmod 600 /usr/share/springboot/superb-analog-439512-g8-e7979f6854cd.json

# gradle-wrapper.properties의 distributionUrl을 로컬 파일 경로로 변경
RUN sed -i 's|https://services.gradle.org/distributions/gradle-8.5-bin.zip|file:///app/gradle/gradle-8.5-bin.zip|' gradle/wrapper/gradle-wrapper.properties

# Gradle Wrapper를 사용하여 빌드 (항상 테스트를 제외)
RUN --mount=type=cache,target=/root/.gradle ./gradlew build -x test --no-daemon

# Step 2: Use an official OpenJDK runtime image to run the app
FROM openjdk:17-jdk-slim

# wait-for-it.sh 스크립트를 복사
COPY scripts/wait-for-it.sh /app/wait-for-it.sh

# Set the working directory in the runtime container
WORKDIR /app

# Copy the JAR file from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Copy Firebase and GCS configuration files
COPY --from=builder /app/serviceAccountKey.json /app/serviceAccountKey.json
COPY --from=builder /usr/share/springboot/superb-analog-439512-g8-e7979f6854cd.json /usr/share/springboot/

# 파일 권한 설정
RUN chmod 600 /usr/share/springboot/superb-analog-439512-g8-e7979f6854cd.json

# Google 인증서 추가 (Google Cloud와의 SSL 통신)
COPY googleapis-root.crt /etc/google/googleapis-root.crt

RUN keytool -importcert -file /etc/google/googleapis-root.crt -alias googleapis-root \
    -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit -noprompt

# SSL 인증서 추가
COPY www.projectkkk.pkcs12 /app/www.projectkkk.pkcs12

RUN chmod 600 /app/www.projectkkk.pkcs12

# 환경 변수 설정 (Google Cloud Storage 인증)
ENV GOOGLE_APPLICATION_CREDENTIALS=/usr/share/springboot/superb-analog-439512-g8-e7979f6854cd.json

# Expose port 443 for the application
EXPOSE 443

# Run the Spring Boot application after waiting for Kafka and Elasticsearch to be ready
ENTRYPOINT ["/app/wait-for-it.sh", "kafka:9092", "--timeout=120", "--", "/app/wait-for-it.sh", "elasticsearch:9200", "--timeout=240", "--", "java", "-Dserver.port=443", "-Dserver.ssl.key-store=/app/www.projectkkk.pkcs12", "-Dserver.ssl.key-store-password=Ccenter123456!", "-Dserver.ssl.key-store-type=PKCS12", "-Djavax.net.ssl.trustStore=/usr/local/openjdk-17/lib/security/cacerts", "-Djavax.net.ssl.trustStorePassword=changeit", "-Djavax.net.ssl.trustStoreType=JKS", "-jar", "app.jar"]
