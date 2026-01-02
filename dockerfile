FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

COPY . .
RUN chmod +x mvnw || true
RUN ./mvnw -B -DskipTests clean package

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/target/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
