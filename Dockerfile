FROM maven:3.9.6-amazoncorretto-21 AS builder
WORKDIR /application
COPY ./ ./
RUN mvn clean package \
&& java -Djarmode=layertools -jar prices-crawler-content-application/target/*.jar extract

FROM amazoncorretto:21
LABEL PROJECT_NAME=prices-crawler-content-api
WORKDIR /application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/application/ ./
ENTRYPOINT ["java", "-Djava.security.egd=/dev/./urandom", "org.springframework.boot.loader.launch.JarLauncher"]
