FROM amazoncorretto:17-al2023-jdk
WORKDIR /app
ADD ./merchant/build/libs/merchant-0.0.1-SNAPSHOT.jar /app/merchant.jar

ENTRYPOINT ["java","-jar","/app/merchant.jar"]