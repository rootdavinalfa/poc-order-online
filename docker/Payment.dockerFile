FROM amazoncorretto:17-al2023-jdk
WORKDIR /app
ADD ./payment/build/libs/payment-0.0.1-SNAPSHOT.jar /app/payment.jar

ENTRYPOINT ["java","-jar","/app/payment.jar"]