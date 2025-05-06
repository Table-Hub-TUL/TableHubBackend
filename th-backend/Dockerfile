FROM maven:3.9.9-amazoncorretto-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:resolve
COPY src ./src
RUN mvn clean package -DskipTests -DfinalName=th-backend

FROM amazoncorretto:21.0.6-al2
WORKDIR /app
EXPOSE 8080
COPY --from=build /app/target/*.jar ./th-backend.jar
ENTRYPOINT ["java", "-jar", "/app/th-backend.jar"]
