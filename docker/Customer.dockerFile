FROM amazoncorretto:17-al2023-jdk
WORKDIR /app
ADD ./customer/build/libs/customer-0.0.1-SNAPSHOT.jar /app/customer.jar

ENTRYPOINT ["java","-jar","/app/customer.jar"]