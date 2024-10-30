# Step 1: Use an official OpenJDK image with a JDK for building the project
FROM openjdk:17-jdk-slim AS builder

# Set the working directory
WORKDIR /app

# 필요한 빌드 파일과 Gradle Wrapper를 복사
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY src ./src

# Gradle Wrapper에 실행 권한 추가
RUN chmod +x gradlew

# 소유권 설정 (Gradle이 권한 문제 없이 접근할 수 있도록)
RUN chown -R gradle:gradle /app /home/gradle/.gradle

# Gradle Wrapper로 빌드 수행
USER root
RUN ./gradlew build -x test --no-daemon

# Step 2: Use an official OpenJDK runtime image to run the app
FROM openjdk:17-jdk-slim

# Set the working directory in the runtime container
WORKDIR /app

# Copy the JAR file from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose port 443 for the application
EXPOSE 443

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
