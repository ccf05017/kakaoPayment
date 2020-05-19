FROM openjdk
WORKDIR /home/kakaoPayment
COPY ./build/libs /home/kakaoPayment
CMD ["java", "-jar", "payment-0.0.1-SNAPSHOT.jar"]
