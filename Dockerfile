# ===== STAGE 1: BUILD =====
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /build

# Копируем pom и скачиваем зависимости (кешируется)
COPY pom.xml .
RUN mvn dependency:go-offline

# Копируем исходники и собираем jar
COPY src ./src
RUN mvn clean package -DskipTests

# ===== STAGE 2: RUN =====
FROM eclipse-temurin:21-jre

WORKDIR /app

# Копируем jar из build-стейджа
COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
