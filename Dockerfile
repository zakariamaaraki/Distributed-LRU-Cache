# Build stage
FROM maven:3.6.0 AS BUILD_STAGE
WORKDIR /zcache
COPY . .
RUN ["mvn", "clean", "install", "-Dmaven.test.skip=true"]

# Run stage
FROM openjdk:11.0.6-jre-slim
WORKDIR /zcache
COPY --from=BUILD_STAGE /zcache/target/*.jar zcache.jar
EXPOSE 9999
ENTRYPOINT ["java", "-jar", "zcache.jar"]