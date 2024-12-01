FROM eclipse-temurin:17-jdk

WORKDIR /testtask

COPY target/testtask-0.0.1-SNAPSHOT.jar testtask.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "testtask.jar"]