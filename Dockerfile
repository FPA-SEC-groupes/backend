FROM openjdk:18
EXPOSE 8082
ADD target/site_de_planification.jar site_de_planification.jar
ENTRYPOINT ["java", "-jar", "/HelloWay.jar"]
