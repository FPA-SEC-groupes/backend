FROM openjdk:18
EXPOSE 8082
ADD target/HelloWay.jar HelloWay.jar
# Copying the photos directory into the Docker image
COPY photos /app/photos
ENTRYPOINT ["java", "-jar", "HelloWay.jar"]
