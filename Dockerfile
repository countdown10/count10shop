# Build stage

FROM bellsoft/liberica-openjdk-alpine:17 AS builder

WORKDIR /app

COPY . .

RUN ./gradlew clean build -x test


# Run stage

FROM bellsoft/liberica-openjdk-alpine:17

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]

#ElasticSearch nori download
FROM docker.elastic.co/elasticsearch/elasticsearch:8.7.1

RUN elasticsearch-plugin install analysis-nori