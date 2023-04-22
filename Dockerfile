FROM maven:3.8-jdk-8-slim AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

FROM openjdk:8-jdk-alpine
ARG JAR_FILE=/app/target/openai-java-0.0.1-SNAPSHOT.jar
COPY --from=build ${JAR_FILE} app.jar
ENV KEY=type_your_key
ENV HOST=https://api.openai.com
EXPOSE 9809
ENTRYPOINT ["java","-jar","/app.jar"]
