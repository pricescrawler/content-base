FROM maven:3.9.12-amazoncorretto-25 AS builder
WORKDIR /application
COPY ./ ./
# Tests need Docker (Testcontainers) and already run in CI, so they are skipped here.
RUN mvn -B clean package -DskipTests \
&& java -Djarmode=tools -jar prices-crawler-content-application/target/*.jar extract --layers --launcher --destination extracted

FROM amazoncorretto:25
LABEL PROJECT_NAME=prices-crawler-content-api
WORKDIR /application
COPY --from=builder /application/extracted/dependencies/ ./
COPY --from=builder /application/extracted/spring-boot-loader/ ./
COPY --from=builder /application/extracted/snapshot-dependencies/ ./
COPY --from=builder /application/extracted/application/ ./
USER 1000:1000
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
