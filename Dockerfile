# Build stage
FROM gradle:8.5-jdk17 AS build
WORKDIR /home/gradle/src
COPY --chown=gradle:gradle . .
RUN gradle build --no-daemon -x test

# Run stage
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy the built artifact from the build stage
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar

# Copy SSL keystore
COPY src/main/resources/keystore.jks /app/keystore.jks

# Default environment variables
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=443
ENV SPRING_JPA_HIBERNATE_DDL_AUTO=update
ENV SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.MySQLDialect
ENV SPRINGDOC_SWAGGER_UI_ENABLED=true
ENV SPRINGDOC_API_DOCS_ENABLED=true
ENV SPRINGDOC_SWAGGER_UI_PATH=/swagger-ui.html
ENV SERVER_SSL_KEY_STORE=/app/keystore.jks
ENV SERVER_SSL_KEY_STORE_TYPE=JKS
ENV SERVER_SSL_KEY_ALIAS=tomcat

# Expose the port the app runs on
EXPOSE 443

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]

COPY src/main/resources/keystore.jks /app/keystore.jks
