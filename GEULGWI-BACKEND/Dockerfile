FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app
COPY . .

RUN chmod +x ./gradlew

RUN ./gradlew clean build -x test -x asciidoctor

FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=builder /app/build/libs/geulgwi-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=dev"]
