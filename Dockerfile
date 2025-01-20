# Step 1: Use an official Gradle image to build the project
FROM gradle:8.5-jdk17 AS builder

# Set the working directory
WORKDIR /app

# 빌드에 필요한 파일 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY src ./src

# Gradle 파일 복사
COPY gradle/gradle-8.5-bin.zip /app/gradle/gradle-8.5-bin.zip

# Firebase 설정 파일 복사
COPY src/main/resources/superb-analog-439512-g8-firebase-adminsdk-l7nbt-2305deb251.json /app/serviceAccountKey.json
COPY src/main/resources/superb-analog-439512-g8-e7979f6854cd.json /usr/share/springboot/
RUN chown root:root /usr/share/springboot/superb-analog-439512-g8-e7979f6854cd.json
RUN chmod 600 /usr/share/springboot/superb-analog-439512-g8-e7979f6854cd.json

# gradle-wrapper.properties의 distributionUrl을 로컬 파일 경로로 변경
RUN sed -i 's|https://services.gradle.org/distributions/gradle-8.5-bin.zip|file:///app/gradle/gradle-8.5-bin.zip|' gradle/wrapper/gradle-wrapper.properties

# Gradle Wrapper를 사용하여 빌드 (항상 테스트를 제외)
RUN --mount=type=cache,target=/root/.gradle ./gradlew build -x test --no-daemon

# Step 2: Use an official OpenJDK runtime image to run the app
FROM openjdk:17-jdk-slim

# Set the working directory in the runtime container
WORKDIR /app

# 빌드 결과 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# Firebase 설정 파일 복사
COPY --from=builder /app/serviceAccountKey.json /app/serviceAccountKey.json
COPY --from=builder /usr/share/springboot/superb-analog-439512-g8-e7979f6854cd.json /usr/share/springboot/

# PKCS12 키스토어 파일 복사
COPY www.projectkkk.pkcs12 /usr/share/springboot/config/www.projectkkk.pkcs12

# wait-for-it.sh 스크립트 복사
COPY scripts/wait-for-it.sh /app/wait-for-it.sh

# 권한 설정
RUN chmod +x /app/wait-for-it.sh
RUN chmod 600 /usr/share/springboot/config/www.projectkkk.pkcs12

# 노출 포트
EXPOSE 443

# 환경 변수 설정
ENV GOOGLE_APPLICATION_CREDENTIALS=/usr/share/springboot/superb-analog-439512-g8-e7979f6854cd.json

# 애플리케이션 실행
ENTRYPOINT ["/app/wait-for-it.sh", "kafka:9092", "--timeout=120", "--", "/app/wait-for-it.sh", "elasticsearch:9200", "--timeout=240", "--", "java", "-Dserver.port=443", "-Dserver.ssl.key-store=/usr/share/springboot/config/www.projectkkk.pkcs12", "-Dserver.ssl.key-store-password=Ccenter123456!", "-Dserver.ssl.key-store-type=PKCS12", "-Djavax.net.ssl.trustStore=/usr/share/springboot/config/www.projectkkk.pkcs12", "-Djavax.net.ssl.trustStorePassword=Ccenter123456!", "-Djavax.net.ssl.trustStoreType=PKCS12", "-jar", "app.jar"]
