FROM openjdk:17-oracle

EXPOSE 8080

# The application's jar file
ARG JAR_FILE=target/CollaborativeWebToDoList-0.0.1-SNAPSHOT.jar

# Add the application's jar to the container
ADD ${JAR_FILE} app.jar

# Run the jar file
ENTRYPOINT ["java","-Dspring.profiles.active=prod","-jar","/app.jar"]
