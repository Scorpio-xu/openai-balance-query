FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG JAR_FILE=target/openai-java-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENV KEY=type_your_key
ENV HOST=https://api.openai.com
EXPOSE 9809
ENTRYPOINT ["java","-jar","/app.jar"]
