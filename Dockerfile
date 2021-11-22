FROM openjdk:17
WORKDIR /app
COPY mvnw mvnw
COPY .mvn/ /app/.mvn/
COPY pom.xml /app/
RUN ./mvnw dependency:go-offline
COPY src/main/ /app/src/main/
ENTRYPOINT ./mvnw --batch-mode schema-registry:register
