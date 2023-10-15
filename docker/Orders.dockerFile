FROM amazoncorretto:17-al2023-jdk
WORKDIR /app
ADD ./orders/build/libs/orders-0.0.1-SNAPSHOT.jar /app/orders.jar

ENTRYPOINT ["java","-jar","/app/orders.jar"]