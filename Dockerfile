# Stage 1: Build the application
FROM gradle:8.5-jdk17 AS builder

# 작업 디렉토리 설정
WORKDIR /app

# 필수 파일 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY src ./src
COPY gradle/gradle-8.5-bin.zip /app/gradle/gradle-8.5-bin.zip

# Firebase & GCS 설정 파일 복사
COPY src/main/resources/superb-analog-439512-g8-firebase-adminsdk-l7nbt-2305deb251.json /app/serviceAccountKey.json
COPY src/main/resources/superb-analog-439512-g8-e7979f6854cd.json /app/superb-analog-439512-g8-e7979f6854cd.json

# Gradle Wrapper 수정
RUN sed -i 's|https://services.gradle.org/distributions/gradle-8.5-bin.zip|file:///app/gradle/gradle-8.5-bin.zip|' gradle/wrapper/gradle-wrapper.properties

# 애플리케이션 빌드
RUN --mount=type=cache,target=/root/.gradle ./gradlew build -x test --no-daemon

---

# Stage 2: Run the application
FROM openjdk:17-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# SSL 인증서 및 구성 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar
COPY --from=builder /app/serviceAccountKey.json /app/serviceAccountKey.json
COPY --from=builder /app/superb-analog-439512-g8-e7979f6854cd.json /usr/share/springboot/config/superb-analog-439512-g8-e7979f6854cd.json
COPY www.projectkkk.pkcs12 /usr/share/springboot/config/www.projectkkk.pkcs12
COPY www.projectkkk.com.pem /usr/share/springboot/config/www.projectkkk.com.pem

# SSL 인증서 TrustStore에 등록
RUN apt-get update && apt-get install -y openssl ca-certificates && \
    echo -n | openssl s_client -connect www.googleapis.com:443 | sed -ne '/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p' > googleapis.crt && \
    keytool -import -trustcacerts -alias googleapis -file googleapis.crt -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit -noprompt && \
    rm googleapis.crt

# 권한 설정
RUN chmod 600 /usr/share/springboot/config/www.projectkkk.pkcs12
RUN chmod 644 /usr/share/springboot/config/superb-analog-439512-g8-e7979f6854cd.json

# Wait-for-it 스크립트 복사 및 권한 설정
COPY scripts/wait-for-it.sh /app/wait-for-it.sh
RUN chmod +x /app/wait-for-it.sh

# 포트 노출
EXPOSE 443

# 환경 변수 설정
ENV GOOGLE_APPLICATION_CREDENTIALS=/usr/share/springboot/config/superb-analog-439512-g8-e7979f6854cd.json

# 애플리케이션 실행
ENTRYPOINT ["/app/wait-for-it.sh", "kafka:9092", "--timeout=120", "--", "/app/wait-for-it.sh", "elasticsearch:9200", "--timeout=240", "--", "java", "-Dserver.port=443", "-Dserver.ssl.key-store=/usr/share/springboot/config/www.projectkkk.pkcs12", "-Dserver.ssl.key-store-password=Ccenter123456!", "-Dserver.ssl.key-store-type=PKCS12", "-Djavax.net.ssl.trustStore=/usr/share/springboot/config/www.projectkkk.pkcs12", "-Djavax.net.ssl.trustStorePassword=Ccenter123456!", "-Djavax.net.ssl.trustStoreType=PKCS12", "-jar", "app.jar"]
