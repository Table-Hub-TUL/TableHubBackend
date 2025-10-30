FROM maven:3.9.9-amazoncorretto-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:resolve
COPY src ./src
COPY .mvn ./.mvn
COPY mvnw .
COPY mvnw.cmd .
RUN mvn clean package -DskipTests

FROM amazoncorretto:21.0.6-al2
WORKDIR /app
EXPOSE 8080
COPY --from=build /app/target/*.jar ./th-backend.jar
COPY .env .env
ENTRYPOINT ["java", "-jar", "/app/th-backend.jar"]
