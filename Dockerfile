# Use a multi-stage build to create a smaller final image

# Stage 1: Build the application
FROM eclipse-temurin:21-jdk-jammy as builder

# Set the working directory inside the container. All subsequent commands will be run from this path.
WORKDIR /app

# Copy the Gradle wrapper and build configuration files first.
# This leverages Docker's layer caching. If these files don't change between builds,
# Docker reuses the cached layer that contains the downloaded dependencies,
# speeding up subsequent builds significantly.
COPY gradlew .
COPY gradle gradle/
COPY build.gradle .
COPY settings.gradle .

# Copy the application's source code into the container.
# This is done in a separate layer after the build files. This way, if only the source code changes,
# the previous layers (including the downloaded dependencies) are reused from the cache.
COPY . .

# Make the Gradle wrapper script executable. This permission is required to run it on Linux-based images.
RUN chmod +x gradlew

# Build the application using the Gradle wrapper.
# 'bootJar' is a Spring Boot-specific task that creates a single, executable JAR file with all dependencies.
# This command will download dependencies (if not cached), compile the code, run tests, and package the application.
RUN ./gradlew bootJar

# Stage 2: Create the final image
# Start a new, clean stage from a JRE (Java Runtime Environment) image.
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copy *only* the built JAR file from the 'builder' stage into the final image.
# The '--from=builder' flag is the core of the multi-stage build pattern. It allows us to
# selectively copy artifacts from a previous stage, leaving behind all the source code, build tools,
# and intermediate files, resulting in a minimal and more secure production image.
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the port Spring Boot typically runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
