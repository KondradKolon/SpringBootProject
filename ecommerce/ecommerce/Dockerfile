FROM amazoncorretto:21-alpine-jdk

WORKDIR /app

COPY . /app

# Grant execution rights on the gradlew
RUN chmod +x gradlew

# Build the application
RUN ./gradlew build -x test


# Copy the jar
RUN find build/libs -name "*.jar" ! -name "*plain.jar" -exec cp {} app.jar \;

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
