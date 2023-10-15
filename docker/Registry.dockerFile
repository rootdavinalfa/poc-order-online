FROM amazoncorretto:17-al2023-jdk
WORKDIR /app
ADD ../registry/build/libs/registry-0.0.1-SNAPSHOT.jar /app/registry.jar

ENTRYPOINT ["java","-jar","/app/registry.jar"]