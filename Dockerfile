####
# This Dockerfile is used to build a GraalVM native image container for the Quarkus application
####

# Stage 1: Build the native executable
FROM ghcr.io/graalvm/graalvm-ce:latest AS build

# Set up environment
ENV MAVEN_HOME=/usr/share/maven \
    MAVEN_CONFIG=/var/maven/.m2 \
    GRAALVM_HOME=/opt/graalvm

# Install Maven
RUN microdnf install -y maven && \
    microdnf clean all

# Set working directory
WORKDIR /project

# Copy the Maven project
COPY pom.xml .
COPY src ./src

# Cache Maven dependencies
RUN mvn -B dependency:go-offline

# Build the native executable
RUN mvn package -Pnative -Dquarkus.native.container-build=true -DskipTests

# Stage 2: Create the runtime container
FROM registry.access.redhat.com/ubi8/ubi-minimal:8.6

# Set up environment
WORKDIR /work
COPY --from=build /project/target/*-runner /work/application

# Add license file (replace with actual license file in production)
COPY src/main/resources/license.xml /work/license.xml
ENV PDF_LICENSE_PATH=/work/license.xml

# Set up user and permissions
RUN chmod 775 /work /work/application \
    && chown -R 1001 /work \
    && chmod -R "g+rwX" /work \
    && chown -R 1001:root /work

# Configure the container
EXPOSE 8080
USER 1001

# Set entry point
ENTRYPOINT ["./application", "-Dquarkus.http.host=0.0.0.0"] 