# Use OpenJDK image from Docker hub
FROM openjdk:20

# Copy the compiled jar file to the docker image
COPY target/*.jar app.jar

RUN ls -l /

# Start the app
ENTRYPOINT ["java", "-jar", "/app.jar"]