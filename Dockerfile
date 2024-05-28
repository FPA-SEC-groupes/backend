FROM openjdk:18
EXPOSE 8082
ADD build/libs/HelloWay.jar HelloWay.jar
ENTRYPOINT ["java", "-jar", "HelloWay.jar"]
