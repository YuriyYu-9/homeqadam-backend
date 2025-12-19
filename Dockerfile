# -------- STAGE 1: runtime --------
FROM eclipse-temurin:21-jre

# Рабочая директория внутри контейнера
WORKDIR /app

# Копируем jar
COPY target/backend-0.0.1-SNAPSHOT.jar app.jar

# Порт Spring Boot
EXPOSE 8080

# Явно указываем профиль prod
ENV SPRING_PROFILES_ACTIVE=prod

# Запуск приложения
ENTRYPOINT ["java", "-jar", "app.jar"]
