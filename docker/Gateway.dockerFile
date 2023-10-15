FROM amazoncorretto:17-al2023-jdk
WORKDIR /app
ADD ./gateway/build/libs/gateway-0.0.1-SNAPSHOT.jar /app/gateway.jar

ENTRYPOINT ["java","-jar","/app/gateway.jar"]