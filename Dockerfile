FROM openjdk:21-jdk-slim
WORKDIR /librayr-mongo
COPY target/Mongo-0.0.1-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "app.jar"]